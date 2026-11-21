package com.vikash.bedtimestories.data

/** One page of a longer retelling: who is in the picture, the English text, and the Hindi (traditional tales only). */
internal class LP(val art: String, val en: String, val hi: String? = null)

/**
 * The longer retellings (about five to seven minutes each), keyed by story id. English
 * and Hindi use these; the other languages keep the short versions until they are
 * rewritten too. A story with no entry here stays as it was.
 */
internal val LONG_ORIGINALS: Map<String, List<LP>> by lazy { LONG_ORIGINALS_A + LONG_ORIGINALS_B }
internal val LONG_TALES: Map<String, List<LP>> by lazy {
    LONG_TALES_1 + LONG_TALES_2 + LONG_TALES_3 + LONG_TALES_4 + LONG_TALES_5 + LONG_TALES_6 + LONG_TALES_7 + LONG_TALES_8
}
