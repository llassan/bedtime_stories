package com.vikash.bedtimestories

import android.graphics.Color as AndroidColor
import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vikash.bedtimestories.ads.Ads
import com.vikash.bedtimestories.ads.BannerAd
import com.vikash.bedtimestories.audio.Lullaby
import com.vikash.bedtimestories.audio.Narrator
import com.vikash.bedtimestories.audio.NeuralNarrator
import com.vikash.bedtimestories.audio.PlaybackService
import com.vikash.bedtimestories.audio.ReadAloud
import com.vikash.bedtimestories.audio.VoiceBox
import com.vikash.bedtimestories.data.Scene
import com.vikash.bedtimestories.ui.BedtimeTheme
import com.vikash.bedtimestories.ui.HomeScreen
import com.vikash.bedtimestories.ui.MakerScreen
import com.vikash.bedtimestories.ui.Mist
import com.vikash.bedtimestories.ui.NightBrush
import com.vikash.bedtimestories.ui.ProfileScreen
import com.vikash.bedtimestories.ui.ReaderScreen
import com.vikash.bedtimestories.ui.art.Illustration
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private lateinit var app: AppState
    private lateinit var narrator: Narrator
    private lateinit var voice: VoiceBox
    private lateinit var neural: NeuralNarrator
    private lateinit var player: ReadAloud
    private val lullaby = Lullaby()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)
        app = AppState(this)
        narrator = Narrator(this)
        voice = VoiceBox(this)
        Ads.init(this)
        neural = NeuralNarrator(this)
        neural.warmUp(app.storyVoice)
        player = ReadAloud(app, neural, narrator, voice)
        // While a story plays, a media service keeps it going with the phone locked.
        app.onPlayingChange = { on ->
            if (on) {
                val title = (app.screen as? Screen.Reader)?.let { app.story(it.storyId) }?.let { app.title(it) }
                PlaybackService.start(this, title ?: getString(R.string.app_name))
            } else {
                player.stop()
                PlaybackService.stop(this)
                if (!app.foreground) lullaby.stop()
            }
        }
        PlaybackService.onPause = { runOnUiThread { app.playing = false } }
        setContent {
            BedtimeTheme {
                // A developer-only screen for reviewing the artwork (see Gallery.kt).
                val debuggable = applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE != 0
                when {
                    debuggable && intent.getBooleanExtra("gallery", false) ->
                        Box(Modifier.fillMaxSize().background(NightBrush)) { com.vikash.bedtimestories.ui.art.Gallery() }
                    debuggable && intent.getBooleanExtra("promo", false) -> com.vikash.bedtimestories.ui.art.Promo()
                    // "noads" hides the test banner so store screenshots never show an advertisement.
                    else -> Root(app, narrator, neural, voice, lullaby, player, showAds = !(debuggable && intent.getBooleanExtra("noads", false)))
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        app.foreground = true
    }

    override fun onStop() {
        // A story that is being read keeps going, with its lullaby, when the phone is locked
        // or the app is left; anything else falls quiet.
        app.foreground = false
        if (!app.playing) lullaby.stop()
        super.onStop()
    }

    override fun onDestroy() {
        PlaybackService.onPause = null
        app.onPlayingChange = null
        app.playing = false
        player.release()
        PlaybackService.stop(this)
        narrator.shutdown()
        neural.release()
        voice.release()
        lullaby.release()
        super.onDestroy()
    }
}

@Composable
private fun Root(app: AppState, narrator: Narrator, neural: NeuralNarrator, voice: VoiceBox, lullaby: Lullaby, player: ReadAloud, showAds: Boolean = true) {
    val screen = app.screen

    // Load whichever storyteller is chosen, so it is ready before play is pressed.
    // Also once the Hindi voice finishes downloading, so it is ready when play is pressed.
    LaunchedEffect(app.voice, neural.hindiPack.state) { neural.warmUp(app.storyVoice) }

    // Background lullaby only while a story is open.
    val wantMusic = app.musicOn && (app.foreground || app.playing) && !app.goodnight && app.onboarded && screen is Screen.Reader
    LaunchedEffect(wantMusic) {
        if (wantMusic) lullaby.start() else lullaby.stop()
    }

    // Timed sleep: when the deadline passes, everything goes quiet.
    LaunchedEffect(app.sleepDeadline) {
        val deadline = app.sleepDeadline
        if (deadline > 0L) {
            delay((deadline - SystemClock.elapsedRealtime()).coerceAtLeast(0L))
            app.triggerGoodnight()
        }
    }

    BackHandler(enabled = app.goodnight || (app.onboarded && screen != Screen.Home)) {
        if (app.goodnight) app.goodnight = false
        app.go(Screen.Home)
    }

    Box(Modifier.fillMaxSize().background(NightBrush)) {
        // The storybook paints right up under the status bar; every other screen stays clear of it.
        val fullBleed = app.onboarded && screen is Screen.Reader
        key(fullBleed) {
          Column(if (fullBleed) Modifier.fillMaxSize().navigationBarsPadding() else Modifier.fillMaxSize().safeDrawingPadding()) {
           Box(Modifier.weight(1f)) {
            when {
                !app.onboarded -> ProfileScreen(app, neural, narrator, first = true)
                screen is Screen.Home -> HomeScreen(app, voice)
                screen is Screen.Maker -> MakerScreen(app)
                screen is Screen.Profile -> ProfileScreen(app, neural, narrator, first = false)
                screen is Screen.Reader -> {
                    val story = remember(screen.storyId) { app.story(screen.storyId) }
                    if (story == null) LaunchedEffect(Unit) { app.go(Screen.Home) }
                    else ReaderScreen(app, story, narrator, neural, voice, player)
                }
            }
           }
           // One banner on the browsing screens only: never during a story, the welcome or the goodnight screen.
           if (showAds && app.onboarded && !fullBleed && !app.goodnight) BannerAd()
        }
        }
        if (app.goodnight) Goodnight(app)
    }
}

@Composable
private fun Goodnight(app: AppState) {
    Box(
        Modifier
            .fillMaxSize()
            .clickable(remember { MutableInteractionSource() }, indication = null) {
                app.goodnight = false
                app.go(Screen.Home)
            }
    ) {
        Illustration(Scene.NIGHT, "hero~ zzz", app.look, Modifier.fillMaxSize(), animated = true)
        Column(
            Modifier.fillMaxSize().safeDrawingPadding().padding(32.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 90.dp))
            Text(
                app.t("Goodnight, ${app.childName}", "शुभ रात्रि, ${app.childName}"),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
            )
            Text(
                app.t("Sweet dreams. The stories will be here tomorrow.", "मीठे सपने। कहानियाँ कल फिर मिलेंगी।"),
                color = Mist,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 12.dp),
            )
        }
    }
}
