package com.vikash.bedtimestories.ui.art

import androidx.compose.ui.graphics.Color
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val White = Color.White

internal fun Pen.snail(t: Float, asleep: Boolean) {
    val skin = Color(0xFFF0D2A6)
    oval(0.06f, 0.07f, 0.34f, 0.07f, skin)
    line(0.36f, 0.18f, 0.32f, 0.38f, 0.022f, skin); dot(0.32f, 0.39f, 0.03f, skin)
    line(0.40f, 0.18f, 0.44f, 0.38f, 0.022f, skin); dot(0.44f, 0.39f, 0.03f, skin)
    dot(0.37f, 0.16f, 0.10f, skin)
    dot(-0.05f, 0.29f, 0.23f, Color(0xFFE58C5C))
    arc(-0.05f, 0.29f, 0.15f, 0f, 300f, 0.03f, Color(0xFFFFC79E))
    arc(-0.05f, 0.29f, 0.07f, 90f, 300f, 0.03f, Color(0xFFFFC79E))
    eyes(0.17f, 0.04f, asleep || t % 4f < 0.14f, 0.018f, x = 0.39f)
    arc(0.39f, 0.14f, 0.03f, 25f, 130f, 0.012f, Ink)
}

internal fun Pen.fish(t: Float, glowing: Boolean) {
    val wag = sin(t * 5f) * 0.04f
    val body = Color(0xFFFFA24B)
    poly(Color(0xFFFF7E3D), -0.18f, 0.30f, -0.40f, 0.44f + wag, -0.40f, 0.16f + wag)
    oval(0f, 0.30f, 0.24f, 0.16f, body)
    oval(-0.02f, 0.30f, 0.035f, 0.14f, White, 0.85f)
    oval(-0.12f, 0.30f, 0.03f, 0.11f, White, 0.85f)
    poly(Color(0xFFFF7E3D), -0.04f, 0.44f, 0.08f, 0.44f, -0.02f, 0.54f)
    dot(0.13f, 0.33f, 0.035f, Ink); dot(0.14f, 0.34f, 0.012f, White)
    arc(0.16f, 0.27f, 0.03f, 30f, 120f, 0.012f, Ink)
    if (glowing) {
        val pulse = 0.75f + 0.25f * sin(t * 2.4f)
        line(0.08f, 0.44f, 0.22f, 0.58f, 0.014f, Color(0xFFFF7E3D))
        glow(0.24f, 0.61f, 0.30f * pulse, Sun, 0.7f)
        dot(0.24f, 0.61f, 0.045f, Color(0xFFFFF3B0))
    }
}

internal fun Pen.turtle(t: Float) {
    val skin = Color(0xFF8ED49A)
    oval(-0.20f, 0.10f, 0.10f, 0.05f, skin); oval(0.14f, 0.10f, 0.10f, 0.05f, skin)
    dot(0.30f, 0.24f, 0.11f, skin)
    oval(0f, 0.22f, 0.27f, 0.19f, Color(0xFF4E9F6B))
    for ((x, y) in listOf(-0.10f to 0.28f, 0.06f to 0.30f, -0.02f to 0.17f, 0.14f to 0.18f, -0.16f to 0.16f)) dot(x, y, 0.045f, Color(0xFF7CC88A))
    eyes(0.26f, 0.045f, t % 4.4f < 0.14f, 0.022f, x = 0.32f)
    arc(0.32f, 0.22f, 0.03f, 25f, 130f, 0.012f, Ink)
}

internal fun Pen.whale(t: Float, asleep: Boolean) {
    val body = Color(0xFF5C82DA)
    poly(body, -0.42f, 0.36f, -0.68f, 0.58f, -0.60f, 0.34f, -0.68f, 0.14f)
    oval(0f, 0.34f, 0.50f, 0.28f, body)
    oval(0.06f, 0.22f, 0.40f, 0.15f, Color(0xFFD3E2FF))
    oval(0.05f, 0.14f, 0.12f, 0.05f, Color(0xFF4A6EC4))
    eyes(0.40f, 0f, asleep || t % 5f < 0.15f, 0.032f, x = 0.30f)
    arc(0.34f, 0.31f, 0.06f, 30f, 110f, 0.016f, Ink)
    dot(0.24f, 0.33f, 0.04f, Blush, 0.45f)
    // The spout rises and falls.
    val h = 0.16f + 0.05f * sin(t * 2f)
    line(0.04f, 0.62f, 0.04f, 0.62f + h, 0.022f, White, 0.7f)
    arc(-0.03f, 0.62f + h, 0.07f, 180f, 180f, 0.02f, White, 0.7f)
    arc(0.11f, 0.62f + h, 0.07f, 180f, 180f, 0.02f, White, 0.7f)
}

internal fun Pen.seal(t: Float) {
    val body = Color(0xFFA3ACC4)
    poly(body, -0.30f, 0.12f, -0.46f, 0.24f, -0.44f, 0.04f)
    oval(0f, 0.13f, 0.34f, 0.13f, body)
    dot(0.27f, 0.22f, 0.13f, body)
    oval(0.31f, 0.18f, 0.06f, 0.045f, Color(0xFFD9DEEA))
    eyes(0.24f, 0.05f, true, 0.022f, x = 0.29f)
    dot(0.33f, 0.19f, 0.014f, Ink)
    val z = (t * 0.25f) % 1f
    dot(0.42f + z * 0.1f, 0.34f + z * 0.2f, 0.02f + z * 0.02f, White, 0.6f * (1f - z))
}

/** Nimbo, the little cloud. */
internal fun Pen.cloud(t: Float, asleep: Boolean) {
    for ((x, y, r) in listOf(Triple(-0.24f, 0.26f, 0.17f), Triple(0f, 0.34f, 0.24f), Triple(0.24f, 0.27f, 0.18f), Triple(-0.10f, 0.20f, 0.18f), Triple(0.12f, 0.20f, 0.18f))) dot(x, y, r, White)
    eyes(0.27f, 0.10f, asleep || t % 3.8f < 0.14f, 0.028f)
    if (asleep) dot(0f, 0.19f, 0.016f, Ink, 0.7f) else smile(0f, 0.22f, 0.04f)
    blush(0.21f, 0.18f)
}

internal fun Pen.train(t: Float) {
    val blue = Color(0xFF4F6FE8)
    val dark = Color(0xFF2E3F9E)
    // Puffs of steam drifting up and back.
    for (i in 0..2) {
        val p = (t * 0.35f + i / 3f) % 1f
        dot(0.20f - p * 0.35f, 0.66f + p * 0.40f, 0.05f + p * 0.07f, White, 0.55f * (1f - p))
    }
    box(-0.20f, 0.42f, 0.34f, 0.46f, 0.05f, blue)
    box(-0.20f, 0.67f, 0.42f, 0.06f, 0.03f, dark)
    box(-0.20f, 0.47f, 0.18f, 0.16f, 0.03f, Sun)
    box(0.16f, 0.30f, 0.50f, 0.24f, 0.08f, blue)
    box(0.20f, 0.50f, 0.09f, 0.18f, 0.02f, dark)
    box(0.20f, 0.60f, 0.14f, 0.04f, 0.02f, dark)
    dot(0.38f, 0.30f, 0.05f, Sun)
    poly(dark, 0.40f, 0.20f, 0.52f, 0.10f, 0.40f, 0.10f)
    box(0f, 0.17f, 0.80f, 0.05f, 0.02f, dark)
    for (x in listOf(-0.24f, 0.02f, 0.26f)) {
        dot(x, 0.10f, 0.10f, Ink)
        dot(x, 0.10f, 0.045f, Color(0xFFB9C4FF))
        val a = t * 3f
        line(x, 0.10f, x + 0.07f * cos(a), 0.10f + 0.07f * sin(a), 0.014f, Color(0xFFB9C4FF))
    }
}

internal fun Pen.boat(t: Float) {
    val wood = Color(0xFFB9784A)
    line(0f, 0.20f, 0f, 0.98f, 0.03f, Color(0xFF7A4E30))
    poly(Cream, 0.03f, 0.94f, 0.03f, 0.32f, 0.40f, 0.32f)
    poly(Color(0xFFFFD9A8), -0.03f, 0.80f, -0.03f, 0.32f, -0.30f, 0.32f)
    poly(Blush, 0f, 0.98f, 0.14f + 0.02f * sin(t * 4f), 0.94f, 0f, 0.90f)
    poly(wood, -0.46f, 0.26f, 0.46f, 0.26f, 0.32f, 0.04f, -0.32f, 0.04f)
    box(0f, 0.25f, 0.92f, 0.04f, 0.02f, Color(0xFF8F5936))
    // A quilt patch, because this boat is really a blanket.
    box(-0.12f, 0.15f, 0.12f, 0.10f, 0.02f, Color(0xFF7286FF), 0.8f)
    box(0.10f, 0.14f, 0.12f, 0.10f, 0.02f, Blush, 0.8f)
}

internal fun Pen.honey() {
    val pot = Color(0xFFD98D3C)
    oval(0f, 0.19f, 0.21f, 0.19f, pot)
    box(0f, 0.36f, 0.34f, 0.07f, 0.03f, Color(0xFFB36E28))
    oval(0f, 0.39f, 0.13f, 0.03f, Sun)
    poly(Sun, -0.10f, 0.36f, -0.02f, 0.36f, -0.06f, 0.20f)
    box(0f, 0.18f, 0.22f, 0.12f, 0.03f, Cream)
    line(-0.06f, 0.18f, 0.06f, 0.18f, 0.02f, pot)
}

internal fun Pen.star(t: Float, size: Float = 0.22f, y: Float = 0.30f, x: Float = 0f, alpha: Float = 1f) {
    val pts = FloatArray(20)
    val spin = sin(t * 0.8f) * 0.15f
    for (i in 0 until 10) {
        val r = if (i % 2 == 0) size else size * 0.45f
        val a = spin + i * PI.toFloat() / 5f + PI.toFloat() / 2f
        pts[i * 2] = x + r * cos(a)
        pts[i * 2 + 1] = y + r * sin(a)
    }
    glow(x, y, size * 2.2f, Sun, 0.55f * alpha)
    poly(Sun, *pts, a = alpha)
}

internal fun Pen.heart(t: Float) {
    val s = 1f + 0.08f * sin(t * 3f)
    val c = Color(0xFFFF7FA3)
    dot(-0.085f * s, 0.36f, 0.10f * s, c)
    dot(0.085f * s, 0.36f, 0.10f * s, c)
    poly(c, -0.175f * s, 0.32f, 0.175f * s, 0.32f, 0f, 0.10f)
}

internal fun Pen.ladder() {
    val wood = Color(0xFFC89A63)
    line(-0.14f, 0f, -0.08f, 1.5f, 0.035f, wood)
    line(0.14f, 0f, 0.08f, 1.5f, 0.035f, wood)
    for (i in 1..7) { val y = i * 0.19f; line(-0.13f + y * 0.035f, y, 0.13f - y * 0.035f, y, 0.03f, wood) }
}

internal fun Pen.zzz(t: Float) {
    for (i in 0..2) {
        val p = (t * 0.22f + i / 3f) % 1f
        val x = -0.10f + p * 0.30f
        val y = 0.10f + p * 0.70f
        val s = 0.05f + p * 0.07f
        val a = (1f - p) * 0.9f
        line(x - s, y + s, x + s, y + s, 0.022f, White, a)
        line(x + s, y + s, x - s, y - s, 0.022f, White, a)
        line(x - s, y - s, x + s, y - s, 0.022f, White, a)
    }
}

internal fun Pen.house(t: Float) {
    for (i in 0..1) {
        val p = (t * 0.25f + i / 2f) % 1f
        dot(0.20f + p * 0.08f, 0.86f + p * 0.30f, 0.04f + p * 0.05f, White, 0.4f * (1f - p))
    }
    box(0.20f, 0.74f, 0.09f, 0.22f, 0.01f, Color(0xFF8A5A44))
    box(0f, 0.28f, 0.62f, 0.56f, 0.03f, Color(0xFFF3E3C8))
    poly(Color(0xFFC96A55), -0.40f, 0.54f, 0.40f, 0.54f, 0f, 0.92f)
    box(-0.12f, 0.16f, 0.16f, 0.32f, 0.03f, Color(0xFF8A5A44))
    dot(-0.07f, 0.16f, 0.015f, Sun)
    val glow = 0.8f + 0.2f * sin(t * 1.3f)
    glow(0.14f, 0.32f, 0.34f, Sun, 0.6f * glow)
    box(0.14f, 0.32f, 0.18f, 0.18f, 0.02f, Sun)
    line(0.14f, 0.23f, 0.14f, 0.41f, 0.012f, Color(0xFFC89A63))
    line(0.05f, 0.32f, 0.23f, 0.32f, 0.012f, Color(0xFFC89A63))
}

internal fun Pen.oak() {
    box(0f, 0.25f, 0.14f, 0.50f, 0.04f, Color(0xFF7A5238))
    val leaf = Color(0xFF3F8F62)
    for ((x, y, r) in listOf(Triple(-0.22f, 0.62f, 0.22f), Triple(0.22f, 0.62f, 0.22f), Triple(0f, 0.80f, 0.26f), Triple(-0.12f, 0.50f, 0.18f), Triple(0.12f, 0.50f, 0.18f))) dot(x, y, r, leaf)
    for ((x, y) in listOf(-0.20f to 0.70f, 0.10f to 0.86f, 0.24f to 0.60f)) dot(x, y, 0.07f, Color(0xFF58A878), 0.7f)
}

/** The Thank-You Tree: bare winter branches hung with ribbons and paper stars. */
internal fun Pen.thankTree(t: Float) {
    val bark = Color(0xFF6E5646)
    line(0f, 0f, 0f, 0.62f, 0.07f, bark)
    val tips = listOf(-0.34f to 0.84f, -0.14f to 1.02f, 0.10f to 1.04f, 0.34f to 0.82f, -0.40f to 0.60f, 0.40f to 0.58f)
    for ((x, y) in tips) line(0f, 0.50f, x, y, 0.035f, bark)
    val colors = listOf(Blush, Sun, Color(0xFF7286FF), Color(0xFF55C9A2), Color(0xFFFF7FA3), Sun)
    tips.forEachIndexed { i, (x, y) ->
        val sway = sin(t * 1.5f + i) * 0.015f
        line(x, y, x + sway, y - 0.10f, 0.008f, White, 0.7f)
        if (i % 2 == 0) star(t, 0.055f, y - 0.14f, x + sway) else dot(x + sway, y - 0.13f, 0.045f, colors[i])
    }
}

internal fun Pen.button(t: Float) {
    val pulse = 0.8f + 0.2f * sin(t * 2.2f)
    glow(0f, 0.30f, 0.34f * pulse, White, 0.6f)
    dot(0f, 0.30f, 0.12f, Color(0xFFDDE3F2))
    arc(0f, 0.30f, 0.085f, 0f, 360f, 0.012f, Color(0xFFAEB7CF))
    for ((x, y) in listOf(-0.03f to 0.33f, 0.03f to 0.33f, -0.03f to 0.27f, 0.03f to 0.27f)) dot(x, y, 0.013f, Color(0xFF8E97B3))
}

internal fun Pen.sparkle(t: Float) {
    for ((i, p) in listOf(Triple(-0.14f, 0.42f, 0.10f), Triple(0.12f, 0.24f, 0.07f), Triple(0.16f, 0.56f, 0.05f)).withIndex()) {
        val (x, y, s) = p
        val k = s * (0.6f + 0.4f * sin(t * 2.5f + i * 2f))
        poly(Color(0xFFFFF3B0), x, y + k, x + k * 0.28f, y + k * 0.28f, x + k, y, x + k * 0.28f, y - k * 0.28f, x, y - k, x - k * 0.28f, y - k * 0.28f, x - k, y, x - k * 0.28f, y + k * 0.28f)
    }
}

internal fun Pen.bird(t: Float) {
    val blue = Color(0xFF7FB5F5)
    val flap = sin(t * 7f) * 0.06f
    poly(Color(0xFF5C93D9), -0.14f, 0.30f, -0.30f, 0.36f, -0.28f, 0.24f)
    oval(0f, 0.30f, 0.16f, 0.12f, blue)
    poly(Color(0xFF5C93D9), -0.08f, 0.32f, 0.06f, 0.32f, -0.04f, 0.46f + flap)
    dot(0.12f, 0.36f, 0.09f, blue)
    poly(Color(0xFFFFA63D), 0.20f, 0.38f, 0.20f, 0.33f, 0.28f, 0.355f)
    dot(0.14f, 0.38f, 0.018f, Ink)
}

/** A glass bubble that lets the child breathe under the sea. */
internal fun Pen.bubble() {
    dot(0f, 0.52f, 0.66f, Color.White, 0.10f)
    arc(0f, 0.52f, 0.66f, 0f, 360f, 0.016f, Color.White, 0.5f)
    arc(0f, 0.52f, 0.56f, 200f, 50f, 0.03f, Color.White, 0.55f)
}
