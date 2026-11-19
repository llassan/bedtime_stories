package com.vikash.bedtimestories.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.min
import kotlin.math.sin

/**
 * A soft music-box lullaby, synthesized in memory (no audio files) and looped
 * quietly underneath the story.
 */
class Lullaby {
    private var track: AudioTrack? = null

    suspend fun start() {
        val t = track ?: withContext(Dispatchers.Default) { build() }.also { track = it }
        runCatching { t.play() }
    }

    fun stop() {
        runCatching { track?.pause() }
    }

    fun release() {
        runCatching { track?.release() }
        track = null
    }

    private fun build(): AudioTrack {
        val pcm = render()
        val t = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setTransferMode(AudioTrack.MODE_STATIC)
            .setBufferSizeInBytes(pcm.size * 2)
            .build()
        t.write(pcm, 0, pcm.size)
        t.setLoopPoints(0, pcm.size, -1)
        t.setVolume(0.22f)
        return t
    }

    private fun render(): ShortArray {
        val step = (RATE * 0.8).toInt()
        val total = step * MELODY.size
        val mix = FloatArray(total)

        // Notes ring past the end of the buffer and wrap around, so the loop has no seam.
        fun add(startSample: Int, freq: Double, amp: Float, decay: Double, attack: Double, seconds: Double) {
            val n = (RATE * seconds).toInt()
            for (i in 0 until n) {
                val time = i.toDouble() / RATE
                val env = min(1.0, time / attack) * exp(-time * decay)
                val wave = sin(2 * PI * freq * time) + 0.28 * sin(4 * PI * freq * time)
                mix[(startSample + i) % total] += (amp * env * wave).toFloat()
            }
        }

        MELODY.forEachIndexed { i, freq ->
            if (freq > 0) add(i * step, freq, 0.5f, 2.6, 0.006, 2.4)
            // A low, slow note under the first beat of every bar.
            if (i % 4 == 0) add(i * step, BASS[(i / 4) % BASS.size], 0.32f, 0.9, 0.25, 3.2)
        }

        val peak = mix.maxOf { abs(it) }.coerceAtLeast(0.001f)
        return ShortArray(total) { (mix[it] / peak * 0.8f * Short.MAX_VALUE).toInt().toShort() }
    }

    private companion object {
        const val RATE = 22_050
        const val G4 = 392.00
        const val A4 = 440.00
        const val C5 = 523.25
        const val D5 = 587.33
        const val E5 = 659.25
        const val G5 = 783.99
        const val A5 = 880.00

        // An original pentatonic tune; 0.0 is a rest.
        val MELODY = doubleArrayOf(
            E5, G5, A5, G5, E5, D5, C5, 0.0,
            D5, E5, G5, E5, D5, C5, A4, 0.0,
            C5, D5, E5, G5, A5, G5, E5, 0.0,
            D5, E5, D5, C5, A4, G4, C5, 0.0,
        )
        val BASS = doubleArrayOf(130.81, 110.00, 87.31, 98.00, 130.81, 110.00, 87.31, 130.81)
    }
}
