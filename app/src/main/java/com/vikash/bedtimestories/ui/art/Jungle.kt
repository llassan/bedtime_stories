package com.vikash.bedtimestories.ui.art

import androidx.compose.ui.graphics.Color
import kotlin.math.sin

private val White = Color.White
private val Orange = Color(0xFFFFA63D)

internal fun Pen.elephant(t: Float) {
    val grey = Color(0xFF9AA3BD)
    val dark = Color(0xFF7E87A3)
    val flap = sin(t * 1.4f) * 0.015f
    arc(-0.40f, 0.34f, 0.10f, 60f, 120f, 0.025f, dark)
    for (x in listOf(-0.24f, -0.06f, 0.12f)) box(x, 0.11f, 0.13f, 0.24f, 0.05f, dark)
    oval(-0.06f, 0.38f, 0.40f, 0.30f, grey)
    box(0.22f, 0.11f, 0.13f, 0.24f, 0.05f, grey)
    oval(0.12f + flap, 0.62f, 0.19f, 0.25f, dark)
    oval(0.13f + flap, 0.62f, 0.12f, 0.18f, Color(0xFFE7B9C6), 0.7f)
    dot(0.34f, 0.62f, 0.25f, grey)
    // Trunk, curling at the tip.
    line(0.52f, 0.58f, 0.60f, 0.36f, 0.10f, grey)
    line(0.60f, 0.36f, 0.56f, 0.20f, 0.09f, grey)
    arc(0.63f, 0.20f, 0.07f, 30f, 150f, 0.08f, grey)
    poly(Cream, 0.44f, 0.50f, 0.50f, 0.48f, 0.52f, 0.36f)
    eyes(0.67f, 0f, t % 4.5f < 0.14f, 0.03f, x = 0.38f)
    dot(0.30f, 0.57f, 0.04f, Blush, 0.45f)
}

internal fun Pen.crocodile(t: Float) {
    val green = Color(0xFF5FA85A)
    val dark = Color(0xFF437F45)
    val wag = sin(t * 1.8f) * 0.03f
    poly(green, -0.34f, 0.24f, -0.74f, 0.12f + wag, -0.34f, 0.05f)
    for (x in listOf(-0.26f, 0.16f)) oval(x, 0.05f, 0.09f, 0.045f, dark)
    oval(-0.04f, 0.15f, 0.40f, 0.12f, green)
    oval(-0.02f, 0.10f, 0.32f, 0.06f, Color(0xFFD8ECA8))
    for (i in 0..5) dot(-0.30f + i * 0.10f, 0.27f, 0.03f, dark)
    box(0.46f, 0.16f, 0.38f, 0.13f, 0.06f, green)
    for (i in 0..3) poly(White, 0.36f + i * 0.07f, 0.10f, 0.40f + i * 0.07f, 0.10f, 0.38f + i * 0.07f, 0.06f)
    dot(0.30f, 0.27f, 0.06f, green)
    dot(0.30f, 0.28f, 0.035f, White)
    dot(0.31f, 0.28f, 0.018f, Ink)
    dot(0.62f, 0.21f, 0.012f, dark)
    arc(0.48f, 0.18f, 0.10f, 20f, 60f, 0.012f, dark)
}

/** A bird standing on the ground: crow, dove or goose depending on the colours. */
internal fun Pen.groundBird(t: Float, body: Color, wing: Color, beak: Color, neck: Float = 0f) {
    val bobbing = sin(t * 2.2f) * 0.01f
    line(-0.04f, 0.10f, -0.04f, 0f, 0.02f, Orange); line(0.05f, 0.10f, 0.05f, 0f, 0.02f, Orange)
    line(-0.04f, 0f, 0.02f, 0f, 0.02f, Orange); line(0.05f, 0f, 0.11f, 0f, 0.02f, Orange)
    poly(wing, -0.14f, 0.30f, -0.34f, 0.26f, -0.30f, 0.16f)
    oval(0f, 0.27f, 0.18f, 0.20f, body)
    oval(-0.04f, 0.27f, 0.11f, 0.14f, wing)
    if (neck > 0f) line(0.08f, 0.40f, 0.10f, 0.50f + neck, 0.09f, body)
    dot(0.10f, 0.54f + neck + bobbing, 0.13f, body)
    poly(beak, 0.20f, 0.58f + neck, 0.20f, 0.51f + neck, 0.35f, 0.54f + neck)
    dot(0.14f, 0.58f + neck + bobbing, 0.034f, White)
    dot(0.15f, 0.58f + neck + bobbing, 0.02f, Ink)
}

internal fun Pen.crane(t: Float) {
    line(-0.04f, 0.50f, -0.06f, 0f, 0.02f, Orange); line(0.05f, 0.50f, 0.07f, 0f, 0.02f, Orange)
    poly(Color(0xFF4A4F66), -0.16f, 0.60f, -0.36f, 0.50f, -0.14f, 0.48f)
    oval(0f, 0.58f, 0.21f, 0.15f, White)
    oval(-0.04f, 0.57f, 0.13f, 0.09f, Color(0xFFDFE4F0))
    line(0.14f, 0.64f, 0.20f, 0.98f + sin(t * 1.5f) * 0.01f, 0.06f, White)
    dot(0.20f, 1.02f, 0.085f, White)
    dot(0.20f, 1.08f, 0.04f, Color(0xFFE5564E))
    poly(Orange, 0.26f, 1.05f, 0.26f, 0.99f, 0.56f, 1.00f)
    dot(0.23f, 1.03f, 0.018f, Ink)
}

internal fun Pen.crab(t: Float) {
    val red = Color(0xFFF07A5A)
    val snap = sin(t * 3f) * 0.02f
    for (s in intArrayOf(-1, 1)) {
        for (i in 0..2) line(s * 0.14f, 0.10f, s * (0.26f + i * 0.04f), 0.02f + i * 0.03f, 0.02f, red)
        line(s * 0.16f, 0.18f, s * 0.30f, 0.28f + snap, 0.03f, red)
        dot(s * 0.33f, 0.31f + snap, 0.075f, red)
        line(s * 0.05f, 0.24f, s * 0.07f, 0.34f, 0.016f, red)
        dot(s * 0.07f, 0.35f, 0.03f, White); dot(s * 0.07f, 0.35f, 0.016f, Ink)
    }
    oval(0f, 0.15f, 0.21f, 0.13f, red)
    smile(0f, 0.15f, 0.04f)
    blush(0.13f, 0.12f, 0.03f)
}

internal fun Pen.snake(t: Float) {
    val green = Color(0xFF7CC36A)
    val dark = Color(0xFF4F9A4E)
    oval(0f, 0.07f, 0.30f, 0.075f, dark)
    oval(0.02f, 0.17f, 0.22f, 0.07f, green)
    val sway = sin(t * 1.6f) * 0.03f
    line(0.06f, 0.20f, 0.10f + sway, 0.46f, 0.10f, green)
    dot(0.11f + sway, 0.50f, 0.10f, green)
    for ((x, y) in listOf(-0.12f to 0.17f, 0.02f to 0.18f, 0.14f to 0.16f)) dot(x, y, 0.022f, Sun)
    eyes(0.52f, 0.04f, t % 4f < 0.14f, 0.02f, x = 0.12f + sway)
    line(0.19f + sway, 0.47f, 0.27f + sway, 0.46f, 0.012f, Color(0xFFE5564E))
}

internal fun Pen.ant(t: Float) {
    val c = Color(0xFF8A3B2E)
    val step = sin(t * 6f) * 0.02f
    for (x in listOf(-0.10f, 0f, 0.10f)) { line(x, 0.14f, x - 0.06f + step, 0f, 0.016f, c); line(x, 0.14f, x + 0.06f - step, 0f, 0.016f, c) }
    dot(-0.18f, 0.16f, 0.10f, c); dot(0f, 0.15f, 0.075f, c); dot(0.16f, 0.19f, 0.09f, c)
    line(0.20f, 0.26f, 0.26f, 0.36f, 0.012f, c); line(0.14f, 0.27f, 0.12f, 0.38f, 0.012f, c)
    dot(0.19f, 0.21f, 0.022f, White); dot(0.195f, 0.21f, 0.012f, Ink)
}

internal fun Pen.pitcher() {
    val clay = Color(0xFFCB7D4C)
    oval(0f, 0.23f, 0.24f, 0.23f, clay)
    box(0f, 0.46f, 0.20f, 0.12f, 0.02f, clay)
    oval(0f, 0.52f, 0.13f, 0.035f, Color(0xFFA85F35))
    oval(0f, 0.515f, 0.09f, 0.02f, Color(0xFF6FB6E8))
    arc(0f, 0.23f, 0.18f, 200f, 140f, 0.02f, Color(0xFFE8A878))
    for (x in listOf(-0.10f, 0f, 0.10f)) dot(x, 0.20f, 0.02f, Color(0xFFE8A878))
}

internal fun Pen.grapes(t: Float) {
    val wood = Color(0xFF7A5238)
    line(-0.26f, 0f, -0.26f, 1.20f, 0.05f, wood)
    line(-0.30f, 1.18f, 0.34f, 1.18f, 0.05f, wood)
    val sway = sin(t * 1.2f) * 0.012f
    oval(0.02f, 1.12f, 0.12f, 0.07f, Color(0xFF4FA35A)); oval(0.22f, 1.14f, 0.10f, 0.06f, Color(0xFF3F8F4C))
    line(0.12f, 1.16f, 0.12f + sway, 1.04f, 0.014f, wood)
    val purple = Color(0xFF8E5BD0)
    var row = 0
    for (n in listOf(4, 3, 2, 1)) {
        for (i in 0 until n) dot(0.12f + sway + (i - (n - 1) / 2f) * 0.075f, 1.00f - row * 0.07f, 0.045f, purple)
        row++
    }
    dot(0.09f + sway, 1.01f, 0.014f, White, 0.6f)
}

internal fun Pen.well() {
    val stone = Color(0xFF9AA0B5)
    for (s in intArrayOf(-1, 1)) line(s * 0.26f, 0.36f, s * 0.26f, 0.86f, 0.04f, Color(0xFF7A5238))
    poly(Color(0xFFC96A55), -0.38f, 0.84f, 0.38f, 0.84f, 0f, 1.08f)
    line(0f, 0.86f, 0f, 0.56f, 0.012f, Cream)
    box(0f, 0.50f, 0.12f, 0.12f, 0.02f, Color(0xFF8A5A44))
    box(0f, 0.19f, 0.62f, 0.38f, 0.05f, stone)
    for ((x, y) in listOf(-0.18f to 0.26f, 0.06f to 0.28f, -0.04f to 0.12f, 0.20f to 0.13f, -0.24f to 0.10f)) box(x, y, 0.14f, 0.08f, 0.02f, Color(0xFFB4BACD))
    oval(0f, 0.38f, 0.31f, 0.06f, Color(0xFF7C8299))
    oval(0f, 0.38f, 0.24f, 0.04f, Color(0xFF2F4E8C))
}

/** A cloth cap, drawn on top of a child's head. */
internal fun Pen.cap(c: Color) {
    oval(0f, 0.97f, 0.20f, 0.10f, c)
    box(0.12f, 0.92f, 0.26f, 0.04f, 0.02f, Color(c.red * 0.8f, c.green * 0.8f, c.blue * 0.8f))
}

internal fun Pen.hen(t: Float) {
    groundBird(t, Color(0xFFFFF7EA), Color(0xFFF1DEC2), Orange)
    val red = Color(0xFFE5564E)
    dot(0.07f, 0.69f, 0.04f, red); dot(0.13f, 0.70f, 0.045f, red)
    dot(0.19f, 0.47f, 0.03f, red)
}

internal fun Pen.goldenEgg(t: Float) {
    val pulse = 0.8f + 0.2f * sin(t * 2f)
    glow(0f, 0.17f, 0.42f * pulse, Sun, 0.6f)
    oval(0f, 0.17f, 0.13f, 0.17f, Color(0xFFFFCF4A))
    oval(-0.04f, 0.23f, 0.035f, 0.055f, Color(0xFFFFF3B0), 0.8f)
}

internal fun Pen.bell(t: Float) {
    val swing = sin(t * 3f) * 0.03f
    val brass = Color(0xFFE9B949)
    dot(swing, 0.52f, 0.035f, Color(0xFFB8862B))
    poly(brass, -0.06f + swing, 0.50f, 0.06f + swing, 0.50f, 0.17f + swing, 0.20f, -0.17f + swing, 0.20f)
    box(swing, 0.19f, 0.40f, 0.05f, 0.025f, Color(0xFFCF9B32))
    dot(swing * 2f, 0.13f, 0.04f, Color(0xFFB8862B))
    oval(-0.05f + swing, 0.36f, 0.025f, 0.08f, Color.White, 0.45f)
}

/** The Sun as a character, with a kind face. */
internal fun Pen.sun(t: Float) {
    val turn = t * 0.2f
    glow(0f, 0.36f, 0.62f, Sun, 0.55f)
    for (i in 0 until 10) {
        val a = turn + i * (2f * Math.PI.toFloat() / 10f)
        line(0.30f * kotlin.math.cos(a), 0.36f + 0.30f * kotlin.math.sin(a), 0.40f * kotlin.math.cos(a), 0.36f + 0.40f * kotlin.math.sin(a), 0.035f, Sun)
    }
    dot(0f, 0.36f, 0.25f, Color(0xFFFFD257))
    eyes(0.40f, 0.09f, t % 4.5f < 0.14f, 0.028f)
    smile(0f, 0.33f, 0.05f)
    blush(0.31f, 0.16f, 0.04f)
}
