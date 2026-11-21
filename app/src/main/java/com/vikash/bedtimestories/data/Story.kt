package com.vikash.bedtimestories.data

import androidx.compose.ui.graphics.Color

/** The languages stories can be read and heard in. */
enum class Lang(val label: String, val tag: String) {
    EN("English", "en-US"), HI("हिन्दी", "hi-IN"), MR("मराठी", "mr-IN"), GU("ગુજરાતી", "gu-IN"),
    TA("தமிழ்", "ta-IN"), TE("తెలుగు", "te-IN"),
    BN("বাংলা", "bn-IN"), KN("ಕನ್ನಡ", "kn-IN"),
    ML("മലയാളം", "ml-IN"), PA("ਪੰਜਾਬੀ", "pa-IN");

    val locale: java.util.Locale get() = java.util.Locale.forLanguageTag(tag)
}

/** A whole story in one more language: its title, its moral and the text of each page in order. */
class Tr(val title: String, val moral: String, val pages: List<String>)

/** What a story is gently about. Shown as a filter on the home screen. */
enum class Mood(val label: String, val labelHi: String) {
    KINDNESS("Kindness", "दया"),
    COURAGE("Courage", "साहस"),
    FRIENDSHIP("Friendship", "दोस्ती"),
    CALM("Calm", "शांति"),
    WONDER("Wonder", "अचरज"),
    WISDOM("Wisdom", "समझदारी"),
}

/** A painted backdrop: sky gradient, hills, and whether the moon and stars are out. */
enum class Scene(
    val top: Color,
    val bottom: Color,
    val hill: Color,
    val hillFar: Color,
    val stars: Boolean = true,
    val moon: Boolean = true,
) {
    NIGHT(Color(0xFF0B1033), Color(0xFF34377F), Color(0xFF161A47), Color(0xFF22276A)),
    FOREST(Color(0xFF0C2233), Color(0xFF1F5A55), Color(0xFF0E2F29), Color(0xFF16453C)),
    SEA(Color(0xFF052A47), Color(0xFF1689A8), Color(0xFF0A4F66), Color(0xFF0E6480), stars = false, moon = false),
    OCEAN(Color(0xFF0A1238), Color(0xFF2B3E8C), Color(0xFF16307A), Color(0xFF1F45A0)),
    SKY(Color(0xFF34286A), Color(0xFFE39AA6), Color(0xFF4A3470), Color(0xFF7A4F8C), moon = false),
    MEADOW(Color(0xFF1C2757), Color(0xFF6F84BD), Color(0xFF1F4D3D), Color(0xFF2F6B55)),
    SNOW(Color(0xFF17254A), Color(0xFF7FA3C7), Color(0xFFDCE8F5), Color(0xFFB4C9E2)),
    JUNGLE(Color(0xFF173A4E), Color(0xFFE9B872), Color(0xFF1E5A3C), Color(0xFF2F7A4C), moon = false),
    RIVER(Color(0xFF1B2A5C), Color(0xFF8E7FB8), Color(0xFF245C44), Color(0xFF356F58)),
}

/**
 * One page of a story. [text] may contain `{name}`; [art] names who is in the
 * picture, e.g. "hero owl star" ("hero" is the child; "name~" shows it asleep).
 */
data class Page(val text: String, val art: String, val hi: String? = null, val more: Map<Lang, String> = emptyMap()) {
    fun text(lang: Lang) = (if (lang == Lang.HI) hi else more[lang]) ?: text
    fun has(lang: Lang) = lang == Lang.EN || (if (lang == Lang.HI) hi != null else lang in more)
}

data class Story(
    val id: String,
    val title: String,
    val mood: Mood,
    val scene: Scene,
    val cover: String,
    val moral: String,
    val pages: List<Page>,
    val custom: Boolean = false,
    val titleHi: String? = null,
    val moralHi: String? = null,
    /** A traditional tale (Panchatantra or a well-known fable) rather than one written for this app. */
    val classic: Boolean = false,
    /** Titles and morals in languages beyond English and Hindi. */
    val more: Map<Lang, Tr> = emptyMap(),
    /**
     * Languages that still read the short version of a story whose English and Hindi
     * were lengthened: their own pages, with the pictures of the short version.
     */
    val short: Map<Lang, List<Page>> = emptyMap(),
    /** True when [pages] is a longer retelling that replaced an earlier short one. */
    val long: Boolean = false,
) {
    /** The pages read in [lang]: the short version where that language has not been lengthened yet. */
    fun pages(lang: Lang): List<Page> = short[lang] ?: pages

    /**
     * Where a parent's recordings of this story are kept. Recordings are saved by page
     * number, so the longer retelling gets a fresh place and recordings made for the short
     * version stay with the short version.
     */
    fun voiceKey(lang: Lang): String = if (long && lang !in short) "$id+long" else id

    fun title(lang: Lang) = (if (lang == Lang.HI) titleHi else more[lang]?.title) ?: title
    fun moral(lang: Lang) = (if (lang == Lang.HI) moralHi else more[lang]?.moral) ?: moral
    /** Whether the whole story exists in [lang]. */
    fun has(lang: Lang) = lang in short || pages.all { it.has(lang) }

    /** Read-aloud time at a slow bedtime pace, counted on the English words of the version [lang] reads. */
    fun minutes(lang: Lang): Int =
        ((pages(lang).sumOf { it.text.split(' ').size } + 99) / 100).coerceAtLeast(1)
}
