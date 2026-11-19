package com.vikash.bedtimestories.audio

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume

/** Reads a page aloud with the device's text-to-speech voice and reports which word is being spoken. */
class Narrator(context: Context) {
    private class Active(
        val id: String,
        val cont: CancellableContinuation<Boolean>,
        val onRange: (Int, Int) -> Unit,
    )

    private val main = Handler(Looper.getMainLooper())
    private val ready = CompletableDeferred<Boolean>()
    private var active: Active? = null
    private lateinit var tts: TextToSpeech

    init {
        // The init callback is posted so that `tts` is assigned before configure() touches it.
        tts = TextToSpeech(context.applicationContext) { status ->
            main.post { ready.complete(status == TextToSpeech.SUCCESS && configure()) }
        }
    }

    private fun configure(): Boolean {
        val result = tts.setLanguage(Locale.US)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            val fallback = tts.setLanguage(Locale.getDefault())
            if (fallback == TextToSpeech.LANG_MISSING_DATA || fallback == TextToSpeech.LANG_NOT_SUPPORTED) return false
        }
        tts.setPitch(1.0f)
        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}
            override fun onDone(utteranceId: String?) = finish(utteranceId, true)
            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) = finish(utteranceId, false)
            override fun onError(utteranceId: String?, errorCode: Int) = finish(utteranceId, false)
            override fun onRangeStart(utteranceId: String?, start: Int, end: Int, frame: Int) {
                main.post { active?.takeIf { it.id == utteranceId }?.onRange?.invoke(start, end) }
            }
        })
        return true
    }

    private fun finish(id: String?, ok: Boolean) {
        main.post {
            val a = active
            if (a != null && a.id == id) {
                active = null
                if (a.cont.isActive) a.cont.resume(ok)
            }
        }
    }

    /**
     * Speaks [text] and suspends until it has been read. Returns false when no
     * voice is available. Cancelling the caller stops the speech.
     */
    suspend fun speak(text: String, slow: Boolean, locale: Locale = Locale.US, onRange: (start: Int, end: Int) -> Unit): Boolean {
        if (!ready.await()) return false
        val lang = tts.setLanguage(locale)
        if (lang == TextToSpeech.LANG_MISSING_DATA || lang == TextToSpeech.LANG_NOT_SUPPORTED) return false
        tts.setSpeechRate(if (slow) 0.68f else 0.85f)
        return suspendCancellableCoroutine { cont ->
            val id = "page-" + System.nanoTime()
            active = Active(id, cont, onRange)
            if (tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, id) != TextToSpeech.SUCCESS) {
                active = null
                cont.resume(false)
            }
            cont.invokeOnCancellation {
                if (active?.id == id) active = null
                tts.stop()
            }
        }
    }

    fun shutdown() {
        active = null
        tts.stop()
        tts.shutdown()
    }
}
