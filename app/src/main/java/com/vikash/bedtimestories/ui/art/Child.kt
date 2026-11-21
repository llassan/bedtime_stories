package com.vikash.bedtimestories.ui.art

import androidx.compose.ui.graphics.Color

/**
 * How the child looks in every illustration. Each number is an index into the lists
 * below; [girl] picks the hairstyles on offer and adds a bow and eyelashes.
 */
data class ChildLook(val skin: Int = 1, val hair: Int = 2, val hairColor: Int = 0, val pajama: Int = 0, val girl: Boolean = true) {
    companion object {
        /** For profiles saved before the boy/girl choice: guessed from the hairstyle. */
        fun looksLikeGirl(hair: Int) = hair in GIRL_STYLES && hair !in BOY_STYLES
    }
}

val SKIN_TONES = listOf(Color(0xFFFFE2CC), Color(0xFFF6CBA4), Color(0xFFDDA873), Color(0xFFAD7646), Color(0xFF75492B))
val HAIR_COLORS = listOf(Color(0xFF4A2C1A), Color(0xFF1E1B26), Color(0xFFEBBE55), Color(0xFFC25A2E), Color(0xFF8E5E38))
val PAJAMA_COLORS = listOf(Color(0xFF7286FF), Color(0xFFFF8FB3), Color(0xFF55C9A2), Color(0xFFFFC555), Color(0xFFB48CFF))
val HAIR_STYLES = listOf("Short", "Long", "Pigtails", "Curly", "Bun", "Spiky", "Side part", "Bob")
/** Indexes into [HAIR_STYLES] offered for each, in the order they are shown. Curly suits both. */
val GIRL_STYLES = listOf(2, 1, 4, 7, 3)
val BOY_STYLES = listOf(0, 3, 5, 6)

private fun Color.shade(f: Float) = Color(red * f, green * f, blue * f, alpha)

/** The child, standing in pyjamas. [painter] draws the old Star Painter with the same body. */
internal fun Pen.child(look: ChildLook, t: Float, asleep: Boolean, painter: Boolean = false) {
    val skin = if (painter) Color(0xFFF1CDB0) else SKIN_TONES[look.skin % SKIN_TONES.size]
    val hair = if (painter) Color(0xFFE4E2EE) else HAIR_COLORS[look.hairColor % HAIR_COLORS.size]
    val cloth = if (painter) Color(0xFF8E6BD1) else PAJAMA_COLORS[look.pajama % PAJAMA_COLORS.size]
    val style = if (painter) 4 else look.hair % HAIR_STYLES.size
    val breath = kotlin.math.sin(t * 1.6f) * 0.006f
    val blink = asleep || (t + cx * 0.013f) % 4.6f < 0.14f

    // Hair that falls behind the head.
    when (style) {
        1 -> oval(0f, 0.63f, 0.29f, 0.33f, hair)
        2 -> { dot(-0.30f, 0.64f, 0.10f, hair); dot(0.30f, 0.64f, 0.10f, hair) }
        3 -> for (i in 0..8) {
            val a = Math.toRadians(-20.0 + i * 27.5)
            dot((0.27 * kotlin.math.cos(a)).toFloat(), 0.76f + (0.25 * kotlin.math.sin(a)).toFloat(), 0.085f, hair)
        }
        4 -> dot(0f, 1.03f, 0.10f, hair)
        7 -> oval(0f, 0.72f, 0.285f, 0.22f, hair)
    }

    // Legs and slippers.
    box(-0.075f, 0.13f, 0.10f, 0.22f, 0.04f, cloth.shade(0.9f))
    box(0.075f, 0.13f, 0.10f, 0.22f, 0.04f, cloth.shade(0.9f))
    oval(-0.085f, 0.025f, 0.085f, 0.04f, Cream)
    oval(0.085f, 0.025f, 0.085f, 0.04f, Cream)

    // Arms, then the body over them.
    oval(-0.22f, 0.36f, 0.06f, 0.15f, cloth.shade(0.92f))
    oval(0.22f, 0.36f, 0.06f, 0.15f, cloth.shade(0.92f))
    dot(-0.23f, 0.22f, 0.05f, skin)
    dot(0.23f, 0.22f, 0.05f, skin)
    box(0f, 0.36f, 0.36f, 0.34f + breath, 0.14f, cloth)
    // Pyjama details: collar and two buttons.
    oval(0f, 0.50f, 0.10f, 0.035f, Color.White, 0.55f)
    dot(0f, 0.40f, 0.018f, Color.White, 0.8f)
    dot(0f, 0.31f, 0.018f, Color.White, 0.8f)
    if (painter) poly(Color(0xFFB79BEA), -0.2f, 0.52f, 0.2f, 0.52f, 0.26f, 0.34f, 0f, 0.42f, -0.26f, 0.34f)

    // Head and ears.
    dot(-0.235f, 0.74f, 0.045f, skin)
    dot(0.235f, 0.74f, 0.045f, skin)
    dot(0f, 0.75f, 0.24f, skin)

    // A bob falls to the jaw on both sides, over the ears.
    if (style == 7) {
        oval(-0.215f, 0.75f, 0.065f, 0.15f, hair)
        oval(0.215f, 0.75f, 0.065f, 0.15f, hair)
    }

    // Hair on top of the head.
    oval(0f, 0.885f, 0.245f, 0.125f, hair)
    oval(-0.10f, 0.86f, 0.15f, 0.09f, hair)
    oval(0.12f, 0.87f, 0.12f, 0.08f, hair)
    when (style) {
        3 -> for (x in listOf(-0.16f, -0.05f, 0.07f, 0.17f)) dot(x, 0.93f, 0.075f, hair)
        // Spiky: little tufts standing up, tallest in the middle.
        5 -> for ((x, h) in listOf(-0.18f to 0.05f, -0.07f to 0.09f, 0.05f to 0.10f, 0.16f to 0.06f)) {
            poly(hair, x - 0.065f, 0.95f, x + 0.01f, 1.0f + h, x + 0.065f, 0.95f)
        }
        // Side part: a parting on one side and a fringe swept across.
        6 -> {
            oval(0.07f, 0.865f, 0.18f, 0.075f, hair)
            line(-0.10f, 0.995f, -0.075f, 0.92f, 0.014f, hair.shade(0.7f))
        }
        // Bob: a straight fringe.
        7 -> box(0f, 0.875f, 0.40f, 0.10f, 0.05f, hair)
    }
    if (style == 2) { dot(-0.25f, 0.80f, 0.035f, Blush); dot(0.25f, 0.80f, 0.035f, Blush) }
    if (look.girl && !painter) {
        // A small bow on one side of the head.
        poly(Blush, 0.16f, 0.985f, 0.095f, 1.03f, 0.10f, 0.935f)
        poly(Blush, 0.16f, 0.985f, 0.225f, 1.03f, 0.22f, 0.935f)
        dot(0.16f, 0.985f, 0.022f, Blush.shade(0.82f))
    }

    // Face.
    eyes(0.73f, 0.095f, blink, 0.03f)
    if (look.girl && !painter && !blink) for (side in intArrayOf(-1, 1)) {
        // Two little lashes at the outer corner of each eye.
        val ex = side * 0.095f
        line(ex + side * 0.021f, 0.752f, ex + side * 0.040f, 0.770f, 0.010f, Ink)
        line(ex + side * 0.028f, 0.740f, ex + side * 0.049f, 0.750f, 0.010f, Ink)
    }
    if (painter) { arc(-0.095f, 0.73f, 0.055f, 0f, 360f, 0.01f, Ink, 0.7f); arc(0.095f, 0.73f, 0.055f, 0f, 360f, 0.01f, Ink, 0.7f) }
    blush(0.665f, 0.155f)
    if (asleep) dot(0f, 0.645f, 0.018f, Ink, 0.8f) else smile(0f, 0.67f)
}

/** Only the head and shoulders, for the round profile picture. */
internal fun Pen.childPortrait(look: ChildLook) = child(look, 0f, asleep = false)
