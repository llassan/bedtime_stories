package com.vikash.bedtimestories.audio

import android.content.Context
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.android.play.core.assetpacks.AssetPackManagerFactory
import com.google.android.play.core.assetpacks.AssetPackState
import com.google.android.play.core.assetpacks.AssetPackStateUpdateListener
import com.google.android.play.core.assetpacks.model.AssetPackStatus
import java.io.File

/**
 * The Hindi storytellers' voice model, which is not inside the app: it is a Play
 * asset pack of about 90 MB that is downloaded from Google Play the first time a
 * family picks a Hindi storyteller. Until it is on the phone, Hindi stories are
 * read by the phone's own voice.
 */
class VoicePack(context: Context) {
    enum class State { MISSING, DOWNLOADING, NEEDS_CONFIRM, READY, FAILED }

    var state by mutableStateOf(State.MISSING)
        private set
    /** How much of the download has arrived, from 0 to 1. */
    var progress by mutableFloatStateOf(0f)
        private set
    /** Megabytes Play says the download is, once known. */
    var sizeMb by mutableStateOf(0)
        private set

    private val manager = AssetPackManagerFactory.getInstance(context.applicationContext)
    private val listener = AssetPackStateUpdateListener { s -> if (s.name() == PACK) update(s) }

    init {
        manager.registerListener(listener)
        if (dir() != null) state = State.READY
        else manager.getPackStates(listOf(PACK))
            .addOnSuccessListener { states -> states.packStates()[PACK]?.let(::update) }
            .addOnFailureListener { Log.i(TAG, "pack state unavailable: ${it.message}") }
    }

    /** The folder with the model files, or null while the pack is not on the phone. */
    fun dir(): File? = runCatching {
        manager.getPackLocation(PACK)?.assetsPath()
            ?.let { File(it, "kokoro-hi") }
            ?.takeIf { File(it, "model.int8.onnx").exists() }
    }.getOrNull()

    /** Starts the download. Play keeps it going if the app is closed. */
    fun download() {
        if (state == State.READY || state == State.DOWNLOADING) return
        state = State.DOWNLOADING
        progress = 0f
        manager.fetch(listOf(PACK)).addOnFailureListener {
            Log.w(TAG, "download could not start", it)
            state = State.FAILED
        }
    }

    /** Play asks before a large download on mobile data; this shows its question. */
    fun confirm(launcher: ActivityResultLauncher<IntentSenderRequest>) {
        manager.showConfirmationDialog(launcher)
    }

    private fun update(s: AssetPackState) {
        if (s.totalBytesToDownload() > 0) sizeMb = ((s.totalBytesToDownload() + 999_999) / 1_000_000).toInt()
        state = when (s.status()) {
            AssetPackStatus.PENDING, AssetPackStatus.DOWNLOADING, AssetPackStatus.TRANSFERRING -> {
                if (s.totalBytesToDownload() > 0) progress = s.bytesDownloaded().toFloat() / s.totalBytesToDownload()
                State.DOWNLOADING
            }
            AssetPackStatus.COMPLETED -> if (dir() != null) State.READY else State.FAILED
            AssetPackStatus.WAITING_FOR_WIFI, AssetPackStatus.REQUIRES_USER_CONFIRMATION -> State.NEEDS_CONFIRM
            AssetPackStatus.FAILED, AssetPackStatus.CANCELED -> {
                Log.w(TAG, "download stopped: status ${s.status()}, error ${s.errorCode()}")
                State.FAILED
            }
            else -> if (dir() != null) State.READY else State.MISSING
        }
    }

    fun release() {
        manager.unregisterListener(listener)
    }

    private companion object {
        const val PACK = "hindivoice"
        const val TAG = "VoicePack"
    }
}
