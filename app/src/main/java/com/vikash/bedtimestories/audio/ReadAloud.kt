package com.vikash.bedtimestories.audio

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.vikash.bedtimestories.AppState
import com.vikash.bedtimestories.SleepMode
import com.vikash.bedtimestories.data.Story
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Reads a story aloud page after page. It runs outside the screen's composition,
 * so it keeps going when the phone is locked or the app is in the background,
 * where Compose stops recomposing. The reader screen follows [page] when it is
 * visible, and [turn] tells it the page was turned by the player rather than by
 * the listener.
 */
class ReadAloud(
    private val app: AppState,
    private val neural: NeuralNarrator,
    private val narrator: Narrator,
    private val voice: VoiceBox,
) {
    var storyId by mutableStateOf<String?>(null)
        private set
    var page by mutableIntStateOf(0)
        private set
    /** Goes up each time the player itself moves to another page. */
    var turn by mutableIntStateOf(0)
        private set
    /** The part of the current page that is being spoken. */
    var highlight by mutableStateOf<Pair<Int, Int>?>(null)
        private set
    /** True when no voice could read the page, so the screen can say why it stopped. */
    var failed by mutableStateOf(false)
        private set

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var job: Job? = null

    /** Starts reading [story] at page [from], with the parent's recordings when [myVoice] is on. */
    fun start(story: Story, from: Int, myVoice: Boolean) {
        job?.cancel()
        storyId = story.id
        page = from
        failed = false
        highlight = null
        job = scope.launch {
            var p = from
            while (isActive) {
                if (p != page) {
                    page = p
                    turn++
                }
                if (p >= story.pages.size) {
                    // "The End": stop, and go to sleep if the timer was waiting for the story to finish.
                    if (app.sleepMode == SleepMode.END) app.triggerGoodnight() else app.playing = false
                    break
                }
                delay(350)
                val text = app.text(story.pages[p])
                val own = voice.file(story.id, p)
                val ok = if (myVoice && own.length() > 0) {
                    voice.play(own)
                } else {
                    // The storyteller first; the phone's own voice only if that cannot run here.
                    neural.speak(text, app.storyVoice, app.speed) { s, e -> highlight = s to e } ||
                        narrator.speak(text, app.slowVoice, app.lang.locale) { s, e -> highlight = s to e }
                }
                highlight = null
                if (!ok) {
                    failed = true
                    app.playing = false
                    break
                }
                delay(1000)
                p++
            }
        }
    }

    /** Stops at once, mid-sentence. */
    fun stop() {
        job?.cancel()
        job = null
        highlight = null
    }

    fun release() {
        stop()
        scope.cancel()
    }
}
