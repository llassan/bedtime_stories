package com.vikash.bedtimestories.audio

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.graphics.drawable.Icon
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaMetadata
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.content.ContextCompat
import com.vikash.bedtimestories.MainActivity
import com.vikash.bedtimestories.R

/**
 * Keeps a story playing while the phone is locked or the app is in the background.
 * It holds the app in the foreground for Android (with a media notification and
 * lock-screen controls), keeps the processor awake between sentences while the
 * next one is voiced, and pauses the story when headphones are unplugged or
 * another app takes over the sound, such as a phone call.
 *
 * The story itself is read by [ReadAloud]; this service only keeps it alive and
 * passes a "pause" from the notification, headphones or lock screen to [onPause].
 */
class PlaybackService : Service() {
    private var session: MediaSession? = null
    private var wake: PowerManager.WakeLock? = null
    private var focus: AudioFocusRequest? = null
    private var noisyRegistered = false

    private val noisy = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) onPause?.invoke()
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PAUSE -> {
                onPause?.invoke()
                return START_NOT_STICKY
            }
            else -> show(intent?.getStringExtra(EXTRA_TITLE) ?: getString(R.string.app_name))
        }
        return START_NOT_STICKY
    }

    private fun show(title: String) {
        val s = session ?: MediaSession(this, "BedtimeStories").also { session = it }
        s.setCallback(object : MediaSession.Callback() {
            override fun onPause() { PlaybackService.onPause?.invoke() }
            override fun onStop() { PlaybackService.onPause?.invoke() }
        })
        s.setMetadata(
            MediaMetadata.Builder()
                .putString(MediaMetadata.METADATA_KEY_TITLE, title)
                .putString(MediaMetadata.METADATA_KEY_ARTIST, getString(R.string.app_name))
                .build()
        )
        s.setPlaybackState(
            PlaybackState.Builder()
                .setActions(PlaybackState.ACTION_PAUSE or PlaybackState.ACTION_STOP or PlaybackState.ACTION_PLAY_PAUSE)
                .setState(PlaybackState.STATE_PLAYING, PlaybackState.PLAYBACK_POSITION_UNKNOWN, 1f)
                .build()
        )
        s.isActive = true

        val manager = getSystemService(NotificationManager::class.java)
        if (manager.getNotificationChannel(CHANNEL) == null) {
            manager.createNotificationChannel(
                NotificationChannel(CHANNEL, "Story playing", NotificationManager.IMPORTANCE_LOW).apply {
                    description = "Shows the story that is being read, with a pause button."
                    setShowBadge(false)
                }
            )
        }
        val open = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val pause = PendingIntent.getService(
            this, 1,
            Intent(this, PlaybackService::class.java).setAction(ACTION_PAUSE),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val notification = Notification.Builder(this, CHANNEL)
            .setSmallIcon(R.drawable.ic_stat_moon)
            .setContentTitle(title)
            .setContentText(getString(R.string.app_name))
            .setContentIntent(open)
            .setOngoing(true)
            .setCategory(Notification.CATEGORY_TRANSPORT)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .addAction(Notification.Action.Builder(Icon.createWithResource(this, R.drawable.ic_pause), "Pause", pause).build())
            .setStyle(Notification.MediaStyle().setMediaSession(s.sessionToken).setShowActionsInCompactView(0))
            .build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        if (wake == null) {
            wake = getSystemService(PowerManager::class.java)
                .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "BedtimeStories:story")
                .apply { setReferenceCounted(false); acquire(3 * 60 * 60 * 1000L) }
        }
        if (focus == null) {
            val audio = getSystemService(AudioManager::class.java)
            focus = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build()
                )
                .setOnAudioFocusChangeListener { change ->
                    // A call, an alarm or another app's audio: pause rather than talk over it.
                    if (change == AudioManager.AUDIOFOCUS_LOSS || change == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                        onPause?.invoke()
                    }
                }
                .build()
                .also { audio.requestAudioFocus(it) }
        }
        if (!noisyRegistered) {
            ContextCompat.registerReceiver(this, noisy, IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY), ContextCompat.RECEIVER_NOT_EXPORTED)
            noisyRegistered = true
        }
    }

    override fun onDestroy() {
        if (noisyRegistered) runCatching { unregisterReceiver(noisy) }
        focus?.let { getSystemService(AudioManager::class.java).abandonAudioFocusRequest(it) }
        wake?.let { if (it.isHeld) it.release() }
        session?.run { isActive = false; release() }
        session = null
        super.onDestroy()
    }

    companion object {
        private const val CHANNEL = "story"
        private const val NOTIFICATION_ID = 7
        private const val ACTION_PAUSE = "com.vikash.bedtimestories.PAUSE"
        private const val EXTRA_TITLE = "title"

        /** Set by the activity: what to do when the listener presses pause outside the app. */
        @Volatile var onPause: (() -> Unit)? = null

        fun start(context: Context, title: String) {
            context.startForegroundService(Intent(context, PlaybackService::class.java).putExtra(EXTRA_TITLE, title))
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, PlaybackService::class.java))
        }
    }
}
