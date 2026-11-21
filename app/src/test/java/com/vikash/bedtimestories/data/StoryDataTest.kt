package com.vikash.bedtimestories.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Guards the story data: lengths, languages and the short versions the other languages keep. */
class StoryDataTest {
    private val others = Lang.entries - Lang.EN - Lang.HI

    @Test
    fun originalsAreLongAndEnglish() {
        for (s in StoryLibrary.stories) {
            assertTrue("${s.id} is not long", s.long)
            assertTrue("${s.id} reads ${s.minutes(Lang.EN)} min", s.minutes(Lang.EN) in 5..8)
            assertTrue("${s.id} child named", s.pages.count { "{name}" in it.text } >= 3)
        }
    }

    @Test
    fun talesAreLongInEnglishAndHindiAndShortElsewhere() {
        assertEquals(40, ClassicTales.stories.size)
        for (s in ClassicTales.stories) {
            assertTrue("${s.id} is not long", s.long)
            assertTrue("${s.id} reads ${s.minutes(Lang.EN)} min", s.minutes(Lang.EN) in 5..8)
            assertTrue("${s.id} missing Hindi", s.pages.all { it.hi != null } && s.has(Lang.HI))
            for (lang in others) {
                assertTrue("${s.id} lost $lang", s.has(lang))
                val short = s.pages(lang)
                assertTrue("${s.id} $lang should be the short version", short.size in 4..6)
                assertTrue("${s.id} $lang text is not $lang", short.all { it.text(lang) != it.text })
                assertTrue("${s.id} $lang recordings must stay with the short version", s.voiceKey(lang) == s.id)
            }
            assertEquals("${s.id}+long", s.voiceKey(Lang.EN))
        }
    }
}
