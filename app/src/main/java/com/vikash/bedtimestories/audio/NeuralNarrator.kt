package com.vikash.bedtimestories.audio

import android.content.Context
import android.content.res.AssetManager
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.k2fsa.sherpa.onnx.OfflineTts
import com.k2fsa.sherpa.onnx.OfflineTtsConfig
import com.k2fsa.sherpa.onnx.OfflineTtsKokoroModelConfig
import com.k2fsa.sherpa.onnx.OfflineTtsModelConfig
import com.vikash.bedtimestories.data.Lang
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.security.MessageDigest
import java.util.concurrent.Executors
import kotlin.math.min

/**
 * A storyteller. [engine] names what reads it ("kokoro" for the bundled English
 * voices, or [SYSTEM_VOICE] for the phone's own voice), [sid] picks the speaker
 * inside it, and [pace] is the speed that sounds like calm bedtime reading.
 * [sampleRate] is the rate the audio is played at; above the model's own 24 kHz it
 * raises the voice's pitch.
 */
data class StoryVoice(
    val name: String,
    val blurb: String,
    val lang: Lang,
    val engine: String,
    val sid: Int,
    val sampleRate: Int,
    val pace: Float,
)

/** The engine name for voices that are read by the phone's built-in text-to-speech. */
const val SYSTEM_VOICE = "system"

/** The engine name for the Hindi storytellers, whose model is downloaded separately. */
const val HINDI_ENGINE = "kokoro-hi"

val STORY_VOICES = listOf(
    StoryVoice("Bella", "Warm and bright", Lang.EN, "kokoro", 1, 24_000, 0.72f),
    StoryVoice("Nicole", "Soft and hushed", Lang.EN, "kokoro", 2, 24_000, 0.72f),
    StoryVoice("Emma", "Gentle, British", Lang.EN, "kokoro", 7, 24_000, 0.72f),
    StoryVoice("George", "Calm, grandfatherly", Lang.EN, "kokoro", 9, 24_000, 0.72f),
    // Only English has a bundled voice whose licence allows this app to ship it;
    // every other language is read by the phone's own text-to-speech voice.
    StoryVoice("फ़ोन की आवाज़", "आपके फ़ोन की हिन्दी आवाज़", Lang.HI, SYSTEM_VOICE, 0, 0, 1f),
    StoryVoice("फोनचा आवाज", "तुमच्या फोनमधील मराठी आवाज", Lang.MR, SYSTEM_VOICE, 0, 0, 1f),
    StoryVoice("ફોનનો અવાજ", "તમારા ફોનનો ગુજરાતી અવાજ", Lang.GU, SYSTEM_VOICE, 0, 0, 1f),
    StoryVoice("போன் குரல்", "உங்கள் தொலைபேசியின் தமிழ்க் குரல்", Lang.TA, SYSTEM_VOICE, 0, 0, 1f),
    StoryVoice("ఫోన్ స్వరం", "మీ ఫోన్‌లోని తెలుగు స్వరం", Lang.TE, SYSTEM_VOICE, 0, 0, 1f),
    StoryVoice("ফোনের কণ্ঠ", "আপনার ফোনের বাংলা কণ্ঠ", Lang.BN, SYSTEM_VOICE, 0, 0, 1f),
    StoryVoice("ಫೋನ್ ಧ್ವನಿ", "ನಿಮ್ಮ ಫೋನ್‌ನ ಕನ್ನಡ ಧ್ವನಿ", Lang.KN, SYSTEM_VOICE, 0, 0, 1f),
    StoryVoice("ഫോണിന്റെ ശബ്ദം", "നിങ്ങളുടെ ഫോണിലെ മലയാളം ശബ്ദം", Lang.ML, SYSTEM_VOICE, 0, 0, 1f),
    StoryVoice("ਫ਼ੋਨ ਦੀ ਆਵਾਜ਼", "ਤੁਹਾਡੇ ਫ਼ੋਨ ਦੀ ਪੰਜਾਬੀ ਆਵਾਜ਼", Lang.PA, SYSTEM_VOICE, 0, 0, 1f),
    // Children's voices. Kokoro has no child speakers, so these are grown-up voices made
    // younger: voiced more slowly, then played faster by the same factor, which raises the
    // pitch by that much and keeps the calm pace. Bella x1.30 is about 274 Hz and Sarah
    // x1.25 about 240 Hz, in the range of a young child. They go at the end of this list
    // because saved choices are positions in it.
    StoryVoice("Poppy", "A little girl", Lang.EN, "kokoro", 1, 31_200, 0.72f / 1.30f),
    StoryVoice("Teddy", "A little boy", Lang.EN, "kokoro", 3, 30_000, 0.72f / 1.25f),
    // Hindi storytellers from Kokoro v1.0 (Apache-2.0). Their model is the on-demand Play
    // asset pack "hindivoice"; until it is downloaded the phone's own voice reads instead.
    // The speaker numbers are Kokoro v1.0's: 31 is hf_alpha, 33 is hm_omega.
    StoryVoice("मीरा", "मीठी, कोमल आवाज़", Lang.HI, HINDI_ENGINE, 31, 24_000, 0.75f),
    StoryVoice("राघव", "शांत, गहरी आवाज़", Lang.HI, HINDI_ENGINE, 33, 24_000, 0.75f),
)

/**
 * The storytellers: offline neural voices run by sherpa-onnx, with no internet.
 * Pages are voiced one sentence at a time. Each sentence is saved once made,
 * and the next one is prepared while the current one plays, so narration flows
 * without gaps after the first sentence.
 */
class NeuralNarrator(private val context: Context) {
    /** The Hindi storytellers' model, downloaded from Play when a family first picks one. */
    val hindiPack = VoicePack(context)
    private fun hindiDir(): File? = hindiPack.dir()

    enum class State { IDLE, LOADING, READY, FAILED }

    var state by mutableStateOf(State.IDLE)
        private set
    /** True while the listener is waiting for a sentence that is still being voiced. */
    var buffering by mutableStateOf(false)
        private set

    // The models are heavy and not thread-safe, so all voicing runs on one background thread.
    private val synth = Executors.newSingleThreadExecutor { r ->
        Thread(r, "storyteller").apply { priority = Thread.NORM_PRIORITY - 1 }
    }.asCoroutineDispatcher()
    private val scope = CoroutineScope(SupervisorJob() + synth)
    private val cacheDir = File(context.cacheDir, "narration")
    private val engines = HashMap<String, OfflineTts>()
    private val broken = HashSet<String>()
    private var track: AudioTrack? = null
    private var trackRate = 0
    private var framesWritten = 0
    private var prefetch: Job? = null

    /** Starts loading [voice] in the background so it is ready by the time play is pressed. */
    fun warmUp(voice: StoryVoice) {
        if (voice.engine == SYSTEM_VOICE) return
        if (voice.engine == HINDI_ENGINE && hindiDir() == null) return
        scope.launch { engineFor(voice.engine) }
    }

    /** The model behind a voice, loaded on first use. Runs on the voicing thread. */
    private fun engineFor(id: String): OfflineTts? {
        engines[id]?.let { return it }
        if (id in broken) return null
        // The Hindi model is not there until its download finishes; that is not a failure.
        val hindi = if (id == HINDI_ENGINE) hindiDir() ?: return null else null
        return try {
            state = State.LOADING
            val started = System.currentTimeMillis()
            // Each model holds a lot of memory, so only one is kept loaded at a time.
            engines.values.forEach { runCatching { it.release() } }
            engines.clear()
            val dataDir = espeakData()
            val model = OfflineTtsModelConfig().apply {
                numThreads = Runtime.getRuntime().availableProcessors().coerceIn(2, 4)
                provider = "cpu"
                debug = false
            }
            val engine = if (hindi == null) {
                model.kokoro = OfflineTtsKokoroModelConfig().apply {
                    this.model = "kokoro/model.int8.onnx"
                    voices = "kokoro/voices.bin"
                    tokens = "kokoro/tokens.txt"
                    this.dataDir = dataDir.absolutePath
                }
                // The English model is read straight from the APK.
                OfflineTts(context.assets, OfflineTtsConfig().apply { this.model = model })
            } else {
                model.kokoro = OfflineTtsKokoroModelConfig().apply {
                    this.model = File(hindi, "model.int8.onnx").path
                    voices = File(hindi, "voices.bin").path
                    tokens = File(hindi, "tokens.txt").path
                    this.dataDir = dataDir.absolutePath
                    lang = "hi"
                }
                // The downloaded model is a plain file on the phone.
                OfflineTts(null, OfflineTtsConfig().apply { this.model = model })
            }
            cacheDir.mkdirs()
            engines[id] = engine
            Log.i(TAG, "voice $id ready in ${System.currentTimeMillis() - started} ms, ${engine.sampleRate()} Hz")
            state = State.READY
            engine
        } catch (e: Throwable) {
            Log.w(TAG, "voice $id failed to load", e)
            broken += id
            state = if (engines.isEmpty() && id != HINDI_ENGINE) State.FAILED else State.READY
            null
        }
    }

    /** The pronunciation data both models share. It has to be real files, so it is copied out once. */
    private fun espeakData(): File {
        val dataDir = File(context.filesDir, "kokoro/espeak-ng-data")
        val marker = File(context.filesDir, "kokoro/.copied-v1")
        if (!marker.exists()) {
            dataDir.deleteRecursively()
            copyAssets(context.assets, "kokoro/espeak-ng-data", dataDir)
            marker.writeText("ok")
        }
        return dataDir
    }

    private fun copyAssets(assets: AssetManager, path: String, target: File) {
        val children = assets.list(path).orEmpty()
        if (children.isEmpty()) {
            target.parentFile?.mkdirs()
            assets.open(path).use { input -> target.outputStream().use { input.copyTo(it) } }
        } else {
            target.mkdirs()
            children.forEach { copyAssets(assets, "$path/$it", File(target, it)) }
        }
    }

    private fun cacheFile(spoken: String, voice: StoryVoice, speed: Float): File {
        val digest = MessageDigest.getInstance("SHA-1").digest("${voice.engine}|${voice.sid}|$speed|$spoken".toByteArray())
        return File(cacheDir, digest.joinToString("") { "%02x".format(it) } + ".pcm")
    }

    /** The sentence as 16-bit audio, from the saved copy when there is one. Runs on the voicing thread. */
    private fun pcmFor(spoken: String, voice: StoryVoice, speed: Float): ShortArray {
        val file = cacheFile(spoken, voice, speed)
        if (file.length() > 0) {
            val bytes = file.readBytes()
            val out = ShortArray(bytes.size / 2)
            ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(out)
            file.setLastModified(System.currentTimeMillis())
            return out
        }
        val engine = engineFor(voice.engine) ?: error("voice ${voice.engine} is not available")
        val started = System.currentTimeMillis()
        val samples = engine.generate(spoken, voice.sid, speed).samples
        check(samples.isNotEmpty()) { "the voice produced no audio" }
        val pcm = ShortArray(samples.size) { (samples[it].coerceIn(-1f, 1f) * Short.MAX_VALUE).toInt().toShort() }
        Log.i(TAG, "${voice.engine}: ${spoken.split(' ').size} words, ${pcm.size * 1000L / voice.sampleRate} ms of audio, in ${System.currentTimeMillis() - started} ms at speed $speed")
        // Saving is only a speed-up. Android may empty the cache folder at any time when the
        // phone is low on space, so make it again, and keep reading even if saving fails.
        runCatching {
            val bytes = ByteBuffer.allocate(pcm.size * 2).order(ByteOrder.LITTLE_ENDIAN)
            bytes.asShortBuffer().put(pcm)
            cacheDir.mkdirs()
            val tmp = File(file.path + ".tmp")
            tmp.writeBytes(bytes.array())
            tmp.renameTo(file)
        }.onFailure { Log.w(TAG, "could not save a sentence: ${it.message}") }
        return pcm
    }

    /**
     * Reads [text] aloud and suspends until it is finished. [onSentence] is told
     * which part of the text is being spoken. Returns false if the voice is not
     * available, so the caller can fall back. Cancelling stops the audio at once.
     */
    suspend fun speak(text: String, voice: StoryVoice, speed: Float, onSentence: (start: Int, end: Int) -> Unit): Boolean {
        if (voice.engine == SYSTEM_VOICE) return false
        val parts = Sentences.split(text)
        return try {
            coroutineScope {
                val jobs = parts.map { p -> async(synth, start = CoroutineStart.LAZY) { pcmFor(p.spoken, voice, speed) } }
                try {
                    for (i in parts.indices) {
                        jobs[i].start()
                        jobs.getOrNull(i + 1)?.start()
                        if (!jobs[i].isCompleted) buffering = true
                        val pcm = jobs[i].await()
                        buffering = false
                        onSentence(parts[i].start, parts[i].end)
                        play(pcm, voice.sampleRate)
                        delay(450) // a breath between sentences
                    }
                } finally {
                    buffering = false
                    jobs.forEach { it.cancel() }
                }
            }
            true
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            Log.w(TAG, "narration failed", e)
            false
        }
    }

    /** Voices these texts ahead of time, quietly, so they start instantly later. */
    fun prepare(texts: List<String>, voice: StoryVoice, speed: Float) {
        prefetch?.cancel()
        if (voice.engine == SYSTEM_VOICE) return
        prefetch = scope.launch {
            for (text in texts) for (s in Sentences.split(text)) {
                ensureActive()
                if (runCatching { pcmFor(s.spoken, voice, speed) }.isFailure) return@launch
                yield() // let a sentence the listener is waiting for jump the queue
            }
            prune()
        }
    }

    private suspend fun play(pcm: ShortArray, rate: Int) = withContext(Dispatchers.IO) {
        if (trackRate != rate) {
            runCatching { track?.release() }
            track = null
            framesWritten = 0
        }
        val t = track ?: newTrack(rate).also { track = it; trackRate = rate }
        var finished = false
        try {
            t.play()
            var offset = 0
            while (offset < pcm.size) {
                ensureActive()
                val n = t.write(pcm, offset, min(2048, pcm.size - offset))
                if (n <= 0) break
                offset += n
                framesWritten += n
            }
            // write() returns once the audio is queued; wait until it has actually been heard.
            val deadline = System.currentTimeMillis() + pcm.size * 1000L / rate + 1500
            while (isActive && t.playbackHeadPosition < framesWritten && System.currentTimeMillis() < deadline) delay(20)
            ensureActive()
            finished = true
        } finally {
            if (!finished) {
                runCatching { t.pause(); t.flush() }
                framesWritten = 0
            }
        }
    }

    private fun newTrack(rate: Int): AudioTrack {
        val format = AudioFormat.Builder()
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setSampleRate(rate)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
            .build()
        val minSize = AudioTrack.getMinBufferSize(rate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT)
        return AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
            )
            .setAudioFormat(format)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .setBufferSizeInBytes(minSize * 2)
            .build()
    }

    /** Keeps saved narration under a fixed size by dropping what was heard longest ago. */
    private fun prune() {
        val files = cacheDir.listFiles()?.sortedBy { it.lastModified() } ?: return
        var total = files.sumOf { it.length() }
        for (f in files) {
            if (total <= MAX_CACHE_BYTES) break
            total -= f.length()
            f.delete()
        }
    }

    fun release() {
        hindiPack.release()
        scope.coroutineContext[Job]?.cancel()
        runCatching { track?.release() }
        track = null
        // Engines are freed on their own thread so one is never pulled out from under a sentence in progress.
        synth.executor.execute {
            engines.values.forEach { runCatching { it.release() } }
            engines.clear()
        }
        synth.close()
    }

    private companion object {
        const val TAG = "Storyteller"
        const val MAX_CACHE_BYTES = 200L * 1024 * 1024
    }
}
