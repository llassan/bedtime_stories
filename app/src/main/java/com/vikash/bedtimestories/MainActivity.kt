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
        setContent {
            BedtimeTheme {
                // A developer-only screen for reviewing the artwork (see Gallery.kt).
                val debuggable = applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE != 0
                if (debuggable && intent.getBooleanExtra("gallery", false)) {
                    Box(Modifier.fillMaxSize().background(NightBrush)) { com.vikash.bedtimestories.ui.art.Gallery() }
                } else Root(app, narrator, neural, voice, lullaby)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        app.foreground = true
    }

    override fun onStop() {
        // Nothing keeps talking or playing once the app leaves the screen.
        app.playing = false
        app.foreground = false
        lullaby.stop()
        super.onStop()
    }

    override fun onDestroy() {
        narrator.shutdown()
        neural.release()
        voice.release()
        lullaby.release()
        super.onDestroy()
    }
}

@Composable
private fun Root(app: AppState, narrator: Narrator, neural: NeuralNarrator, voice: VoiceBox, lullaby: Lullaby) {
    val screen = app.screen

    // Load whichever storyteller is chosen, so it is ready before play is pressed.
    LaunchedEffect(app.voice) { neural.warmUp(app.storyVoice) }

    // Background lullaby only while a story is open.
    val wantMusic = app.musicOn && app.foreground && !app.goodnight && app.onboarded && screen is Screen.Reader
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
                    else ReaderScreen(app, story, narrator, neural, voice)
                }
            }
           }
           // One banner on the browsing screens only: never during a story, the welcome or the goodnight screen.
           if (app.onboarded && !fullBleed && !app.goodnight) BannerAd()
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
