package com.vikash.bedtimestories.ui.art

import androidx.compose.ui.graphics.Color

/** How the child looks in every illustration. Each value is an index into the lists below. */
data class ChildLook(val skin: Int = 1, val hair: Int = 2, val hairColor: Int = 0, val pajama: Int = 0)

val SKIN_TONES = listOf(Color(0xFFFFE2CC), Color(0xFFF6CBA4), Color(0xFFDDA873), Color(0xFFAD7646), Color(0xFF75492B))
val HAIR_COLORS = listOf(Color(0xFF4A2C1A), Color(0xFF1E1B26), Color(0xFFEBBE55), Color(0xFFC25A2E), Color(0xFF8E5E38))
val PAJAMA_COLORS = listOf(Color(0xFF7286FF), Color(0xFFFF8FB3), Color(0xFF55C9A2), Color(0xFFFFC555), Color(0xFFB48CFF))
val HAIR_STYLES = listOf("Short", "Long", "Pigtails", "Curly", "Bun")

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

    // Hair on top of the head.
    oval(0f, 0.885f, 0.245f, 0.125f, hair)
    oval(-0.10f, 0.86f, 0.15f, 0.09f, hair)
    oval(0.12f, 0.87f, 0.12f, 0.08f, hair)
    if (style == 3) for (x in listOf(-0.16f, -0.05f, 0.07f, 0.17f)) dot(x, 0.93f, 0.075f, hair)
    if (style == 2) { dot(-0.25f, 0.80f, 0.035f, Blush); dot(0.25f, 0.80f, 0.035f, Blush) }

    // Face.
    eyes(0.73f, 0.095f, blink, 0.03f)
    if (painter) { arc(-0.095f, 0.73f, 0.055f, 0f, 360f, 0.01f, Ink, 0.7f); arc(0.095f, 0.73f, 0.055f, 0f, 360f, 0.01f, Ink, 0.7f) }
    blush(0.665f, 0.155f)
    if (asleep) dot(0f, 0.645f, 0.018f, Ink, 0.8f) else smile(0f, 0.67f)
}

/** Only the head and shoulders, for the round profile picture. */
internal fun Pen.childPortrait(look: ChildLook) = child(look, 0f, asleep = false)
