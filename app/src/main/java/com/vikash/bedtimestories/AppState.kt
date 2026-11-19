package com.vikash.bedtimestories

import android.content.Context
import android.os.SystemClock
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.vikash.bedtimestories.audio.STORY_VOICES
import com.vikash.bedtimestories.audio.StoryVoice
import com.vikash.bedtimestories.data.ClassicTales
import com.vikash.bedtimestories.data.Lang
import com.vikash.bedtimestories.data.Story
import com.vikash.bedtimestories.data.UI_TEXT
import com.vikash.bedtimestories.data.StoryLibrary
import com.vikash.bedtimestories.data.StoryMaker
import com.vikash.bedtimestories.ui.art.ChildLook

sealed interface Screen {
    data object Home : Screen
    data class Reader(val storyId: String) : Screen
    data object Maker : Screen
    data object Profile : Screen
}

enum class SleepMode(val label: String, val labelHi: String, val minutes: Int) {
    OFF("Off", "बंद", 0),
    M5("5 minutes", "5 मिनट", 5),
    M15("15 minutes", "15 मिनट", 15),
    M30("30 minutes", "30 मिनट", 30),
    END("When this story ends", "कहानी ख़त्म होने पर", -1),
}

/** Everything the screens share: the child's profile, saved choices, and what is on screen. */
class AppState(context: Context) {
    private val prefs = context.getSharedPreferences("bedtime", Context.MODE_PRIVATE)

    var childName by mutableStateOf(prefs.getString("name", "") ?: "")
        private set
    var look by mutableStateOf(
        ChildLook(prefs.getInt("skin", 1), prefs.getInt("hair", 2), prefs.getInt("hairColor", 0), prefs.getInt("pajama", 0))
    )
        private set
    /** Which storyteller reads: an index into [STORY_VOICES]. */
    var lang by mutableStateOf(runCatching { Lang.valueOf(prefs.getString("lang", "EN") ?: "EN") }.getOrDefault(Lang.EN))
        private set
    var voice by mutableStateOf(savedVoice(lang))
        private set
    var onboarded by mutableStateOf(prefs.getBoolean("onboarded", false))
        private set
    var favorites by mutableStateOf(prefs.getStringSet("favorites", emptySet())?.toSet() ?: emptySet())
        private set
    var customIds by mutableStateOf(
        (prefs.getString("custom", "") ?: "").split(';').filter { StoryMaker.fromId(it) != null }
    )
        private set
    var musicOn by mutableStateOf(prefs.getBoolean("music", true))
        private set
    var slowVoice by mutableStateOf(prefs.getBoolean("slowVoice", false))
        private set

    var screen by mutableStateOf<Screen>(Screen.Home)
        private set
    var playing by mutableStateOf(false)
    var foreground by mutableStateOf(true)
    var goodnight by mutableStateOf(false)
    var sleepMode by mutableStateOf(SleepMode.OFF)
        private set
    /** elapsedRealtime at which the sleep timer fires; 0 when no timed sleep is set. */
    var sleepDeadline by mutableLongStateOf(0L)
        private set

    fun go(to: Screen) {
        playing = false
        screen = to
    }

    val storyVoice: StoryVoice get() = STORY_VOICES[voice]
    /** Each voice has its own calm pace; "extra slow" stretches it a little further. */
    val speed: Float get() = storyVoice.pace * (if (slowVoice) 0.84f else 1f)

    /** The voice last chosen for a language, or the first one that speaks it. */
    private fun savedVoice(l: Lang): Int {
        val saved = prefs.getInt("voice_" + l.name, -1)
        return if (saved in STORY_VOICES.indices && STORY_VOICES[saved].lang == l) saved else STORY_VOICES.indexOfFirst { it.lang == l }
    }

    fun chooseLang(l: Lang) {
        playing = false
        lang = l
        voice = savedVoice(l)
        prefs.edit().putString("lang", l.name).apply()
    }

    /** Picks the English or the Hindi wording for the app's own labels. */
    fun t(en: String, hi: String): String = when (lang) {
        Lang.EN -> en
        Lang.HI -> hi
        else -> {
            // Marathi and Gujarati wording lives in Strings.kt, keyed by the English text,
            // with the child's and the storyteller's names swapped out for placeholders.
            var key = en
            if (childName.isNotEmpty()) key = key.replace(childName, "{n}")
            key = key.replace(storyVoice.name, "{v}")
            UI_TEXT[lang]?.get(key)?.replace("{n}", childName)?.replace("{v}", storyVoice.name) ?: en
        }
    }

    fun saveProfile(name: String, look: ChildLook) {
        childName = name.trim()
        this.look = look
        onboarded = true
        prefs.edit().putString("name", childName)
            .putInt("skin", look.skin).putInt("hair", look.hair)
            .putInt("hairColor", look.hairColor).putInt("pajama", look.pajama)
            .putBoolean("onboarded", true).apply()
    }

    fun chooseVoice(index: Int) {
        voice = index
        prefs.edit().putInt("voice_" + lang.name, index).apply()
    }

    fun setSlow(slow: Boolean) {
        slowVoice = slow
        prefs.edit().putBoolean("slowVoice", slow).apply()
    }

    fun toggleFavorite(id: String) {
        favorites = if (id in favorites) favorites - id else favorites + id
        prefs.edit().putStringSet("favorites", favorites).apply()
    }

    fun toggleMusic() {
        musicOn = !musicOn
        prefs.edit().putBoolean("music", musicOn).apply()
    }

    fun addCustom(id: String) {
        if (id !in customIds) {
            customIds = listOf(id) + customIds
            prefs.edit().putString("custom", customIds.joinToString(";")).apply()
        }
    }

    fun removeCustom(id: String) {
        customIds = customIds - id
        if (id in favorites) toggleFavorite(id)
        prefs.edit().putString("custom", customIds.joinToString(";")).apply()
    }

    fun setSleep(mode: SleepMode) {
        sleepMode = mode
        sleepDeadline = if (mode.minutes > 0) SystemClock.elapsedRealtime() + mode.minutes * 60_000L else 0L
    }

    fun triggerGoodnight() {
        playing = false
        sleepMode = SleepMode.OFF
        sleepDeadline = 0L
        goodnight = true
    }

    fun allStories(): List<Story> = (customIds.mapNotNull { StoryMaker.fromId(it) } + StoryLibrary.stories + ClassicTales.stories).filter { it.has(lang) }

    fun story(id: String): Story? = (StoryLibrary.stories + ClassicTales.stories).firstOrNull { it.id == id } ?: StoryMaker.fromId(id)

    fun title(story: Story) = fill(story.title(lang))
    fun text(page: com.vikash.bedtimestories.data.Page) = fill(page.text(lang))

    /** Puts the child's name into story text. */
    fun fill(text: String): String = text.replace("{name}", childName)
}
