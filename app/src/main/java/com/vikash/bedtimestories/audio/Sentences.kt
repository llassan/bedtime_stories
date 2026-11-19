package com.vikash.bedtimestories.audio

/** A sentence inside a page: where it sits in the page text, and what the voice should say. */
data class Sentence(val start: Int, val end: Int, val spoken: String)

object Sentences {
    /**
     * Splits page text into sentences. A sentence ends at `.`, `!`, `?` or the
     * Hindi danda `।` (plus a closing quote) when a new sentence clearly starts
     * after it: a capital letter, an opening quote, or a letter from a script
     * without capitals. So “In... and out.” stays in one piece, and so does
     * “Ouch!” and hopped away.
     */
    fun split(text: String): List<Sentence> {
        val cuts = mutableListOf<Int>()
        var i = 0
        while (i < text.length) {
            if (text[i] in ENDS) {
                var j = i + 1
                while (j < text.length && (text[j] in ENDS || text[j] == '”')) j++
                var k = j
                while (k < text.length && text[k].isWhitespace()) k++
                if (k > j && k < text.length && startsSentence(text[k])) cuts += k
                i = j
            } else i++
        }
        val bounds = listOf(0) + cuts + text.length
        val raw = bounds.zipWithNext().filter { (a, b) -> text.substring(a, b).isNotBlank() }

        // A tiny fragment sounds clipped when voiced alone, so join it to its neighbour.
        val merged = mutableListOf<Pair<Int, Int>>()
        for (r in raw) {
            val last = merged.lastOrNull()
            if (last != null && last.second - last.first < 14) merged[merged.size - 1] = last.first to r.second
            else merged += r
        }
        return merged.map { (a, b) ->
            val end = text.substring(a, b).trimEnd().length + a
            Sentence(a, end, spoken(text.substring(a, end)))
        }
    }

    private const val ENDS = ".!?।"

    private fun startsSentence(c: Char) = c.isUpperCase() || c == '“' || (c.isLetter() && !c.isLowerCase())

    private fun spoken(s: String) = s
        .replace("“", "").replace("”", "")
        .replace('’', '\'')
        .replace("...", ", ")
        .replace('।', '.')
        .replace(Regex("\\s+"), " ")
        .trim()
}
