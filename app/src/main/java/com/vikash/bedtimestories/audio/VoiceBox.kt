package com.vikash.bedtimestories.audio

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.resume

/** Records a parent's own reading of each page and plays it back. Files stay on the device. */
class VoiceBox(private val context: Context) {
    private var recorder: MediaRecorder? = null
    private var recordTarget: File? = null
    private var player: MediaPlayer? = null

    private fun dir(storyId: String) = File(context.filesDir, "voice/" + storyId.replace(':', '_'))
    fun file(storyId: String, page: Int) = File(dir(storyId), "$page.m4a")
    fun has(storyId: String, page: Int) = file(storyId, page).length() > 0
    fun count(storyId: String) = dir(storyId).listFiles { f -> f.name.endsWith(".m4a") }?.size ?: 0

    val isRecording get() = recorder != null

    fun startRecording(storyId: String, page: Int): Boolean {
        stopRecording()
        stopPlayback()
        val target = file(storyId, page)
        target.parentFile?.mkdirs()
        val tmp = File(target.path + ".tmp")
        val r = if (Build.VERSION.SDK_INT >= 31) MediaRecorder(context) else @Suppress("DEPRECATION") MediaRecorder()
        return try {
            r.setAudioSource(MediaRecorder.AudioSource.MIC)
            r.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            r.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            r.setAudioEncodingBitRate(96_000)
            r.setAudioSamplingRate(44_100)
            r.setOutputFile(tmp.path)
            r.prepare()
            r.start()
            recorder = r
            recordTarget = target
            true
        } catch (e: Exception) {
            r.release()
            tmp.delete()
            false
        }
    }

    /** Returns true when a recording was saved. */
    fun stopRecording(): Boolean {
        val r = recorder ?: return false
        val target = recordTarget
        recorder = null
        recordTarget = null
        // stop() throws when almost nothing was captured; treat that as "no recording".
        val ok = try { r.stop(); true } catch (e: RuntimeException) { false }
        r.release()
        if (target == null) return false
        val tmp = File(target.path + ".tmp")
        return if (ok && tmp.length() > 0) {
            target.delete()
            tmp.renameTo(target)
        } else {
            tmp.delete()
            false
        }
    }

    /** Plays a recording and suspends until it ends. Cancelling the caller stops it. */
    suspend fun play(file: File): Boolean = suspendCancellableCoroutine { cont ->
        stopPlayback()
        val mp = MediaPlayer()
        player = mp
        try {
            mp.setDataSource(file.path)
            mp.setOnCompletionListener {
                stopPlayback()
                if (cont.isActive) cont.resume(true)
            }
            mp.setOnErrorListener { _, _, _ ->
                stopPlayback()
                if (cont.isActive) cont.resume(false)
                true
            }
            mp.prepare()
            mp.start()
        } catch (e: Exception) {
            stopPlayback()
            if (cont.isActive) cont.resume(false)
        }
        cont.invokeOnCancellation { stopPlayback() }
    }

    private fun stopPlayback() {
        player?.let { runCatching { it.release() } }
        player = null
    }

    fun delete(storyId: String, page: Int) {
        file(storyId, page).delete()
    }

    fun deleteStory(storyId: String) {
        dir(storyId).deleteRecursively()
    }

    fun release() {
        stopRecording()
        stopPlayback()
    }
}
