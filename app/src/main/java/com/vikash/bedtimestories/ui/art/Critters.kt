package com.vikash.bedtimestories.ui.art

import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import kotlin.math.sin

internal enum class Ear { NONE, ROUND, POINT, LONG, FLOPPY, TUFT, ANTLER, HORN, SIDE }
internal enum class Tail { NONE, FOX, CAT, SQUIRREL, THIN, PUFF, STUB, DRAGON, LONG }
internal enum class Trait { PLAIN, OWL, HEDGEHOG, PENGUIN, FROG, SHEEP, DRAGON, WHISKERS, CONDUCTOR, LION, MONKEY, DONKEY }

/** Every four-legged friend is the same soft, round body with different ears, tail and face. */
internal class Kind(
    val body: Color,
    val belly: Color,
    val ear: Ear,
    val tail: Tail = Tail.NONE,
    val trait: Trait = Trait.PLAIN,
    val muzzle: Color? = null,
    val earIn: Color = Color(0xFFFFB7C5),
)

private val White = Color.White
private val Orange = Color(0xFFFFA63D)

internal val KINDS = mapOf(
    "bear" to Kind(Color(0xFFA7744F), Color(0xFFEACDA6), Ear.ROUND, Tail.STUB, muzzle = Color(0xFFEACDA6), earIn = Color(0xFFEACDA6)),
    "fox" to Kind(Color(0xFFF28C3D), White, Ear.POINT, Tail.FOX, muzzle = White, earIn = Color(0xFF5A3524)),
    "rabbit" to Kind(Color(0xFFEDE7F3), White, Ear.LONG, Tail.PUFF),
    "mouse" to Kind(Color(0xFFBDB7CC), Color(0xFFEFEAF6), Ear.ROUND, Tail.THIN, Trait.WHISKERS),
    "cat" to Kind(Color(0xFFF2B15E), Color(0xFFFFF0D2), Ear.POINT, Tail.CAT, Trait.WHISKERS),
    "conductor" to Kind(Color(0xFF9C97B5), Color(0xFFECE9F6), Ear.POINT, Tail.CAT, Trait.CONDUCTOR),
    "puppy" to Kind(Color(0xFFE7BD80), Color(0xFFFFF1D9), Ear.FLOPPY, Tail.STUB, muzzle = Color(0xFFFFF1D9), earIn = Color(0xFF9C6A3F)),
    "squirrel" to Kind(Color(0xFFCB6E3C), Color(0xFFFFE3C2), Ear.TUFT, Tail.SQUIRREL),
    "hedgehog" to Kind(Color(0xFFF2D6AE), Color(0xFFFFEFD6), Ear.NONE, Tail.NONE, Trait.HEDGEHOG),
    "owl" to Kind(Color(0xFF8E6C52), Color(0xFFF3DDBB), Ear.TUFT, Tail.NONE, Trait.OWL),
    "penguin" to Kind(Color(0xFF333A5C), White, Ear.NONE, Tail.NONE, Trait.PENGUIN),
    "frog" to Kind(Color(0xFF6FC56C), Color(0xFFD9F2B4), Ear.NONE, Tail.NONE, Trait.FROG),
    "deer" to Kind(Color(0xFFCB9C6C), Color(0xFFF6E3C8), Ear.ANTLER, Tail.PUFF, muzzle = Color(0xFFF6E3C8)),
    "sheep" to Kind(Color(0xFFF3D9C4), White, Ear.FLOPPY, Tail.NONE, Trait.SHEEP, earIn = Color(0xFFE3BFA6)),
    "monkey" to Kind(Color(0xFF9C6B48), Color(0xFFF3D6B4), Ear.SIDE, Tail.LONG, Trait.MONKEY, earIn = Color(0xFFF3D6B4)),
    "lion" to Kind(Color(0xFFF4B852), Color(0xFFFFE8B8), Ear.ROUND, Tail.CAT, Trait.LION, muzzle = Color(0xFFFFE8B8), earIn = Color(0xFFD98B3A)),
    "jackal" to Kind(Color(0xFFCBA36C), Color(0xFFFFF0D8), Ear.POINT, Tail.FOX, muzzle = Color(0xFFFFF0D8), earIn = Color(0xFF6B4A2E)),
    "bluejackal" to Kind(Color(0xFF4F80E2), Color(0xFFD3E2FF), Ear.POINT, Tail.FOX, muzzle = Color(0xFFD3E2FF), earIn = Color(0xFF2E4FA6)),
    "wolf" to Kind(Color(0xFF8D94AA), Color(0xFFE8EBF3), Ear.POINT, Tail.FOX, muzzle = Color(0xFFE8EBF3), earIn = Color(0xFF4A4F66)),
    "donkey" to Kind(Color(0xFF8F8A99), Color(0xFFE4E0EA), Ear.LONG, Tail.STUB, Trait.DONKEY, earIn = Color(0xFF6A6575)),
    "goat" to Kind(Color(0xFFF3EFE6), Color(0xFFFFFFFF), Ear.HORN, Tail.STUB, muzzle = Color(0xFFF6D9CC)),
    "dragon" to Kind(Color(0xFF5FBE7E), Color(0xFFFFEBA9), Ear.HORN, Tail.DRAGON, Trait.DRAGON),
)

internal fun Pen.critter(k: Kind, t: Float, asleep: Boolean) {
    val breath = sin(t * 1.7f + cx * 0.01f) * 0.008f
    val closed = asleep || (t + cx * 0.017f) % 4.2f < 0.14f
    val dark = Color(k.body.red * 0.8f, k.body.green * 0.8f, k.body.blue * 0.8f)

    when (k.tail) {
        Tail.FOX -> { oval(0.33f, 0.20f, 0.21f, 0.115f, k.body); oval(0.47f, 0.21f, 0.08f, 0.075f, White) }
        Tail.CAT -> arc(0.30f, 0.30f, 0.17f, 270f, 190f, 0.07f, k.body)
        Tail.SQUIRREL -> { oval(0.33f, 0.44f, 0.17f, 0.30f, k.body); oval(0.36f, 0.48f, 0.08f, 0.19f, k.belly, 0.6f) }
        Tail.THIN -> arc(0.32f, 0.16f, 0.15f, 290f, 200f, 0.022f, k.earIn)
        Tail.PUFF -> dot(0.27f, 0.13f, 0.07f, White)
        Tail.STUB -> dot(0.27f, 0.16f, 0.06f, dark)
        Tail.DRAGON -> { poly(k.body, 0.18f, 0.30f, 0.66f, 0.05f, 0.18f, 0.04f); poly(k.belly, 0.66f, 0.05f, 0.58f, 0.18f, 0.52f, 0.07f) }
        Tail.LONG -> arc(0.36f, 0.30f, 0.20f, 250f, 220f, 0.05f, k.body)
        Tail.NONE -> {}
    }

    when (k.trait) {
        Trait.HEDGEHOG -> {
            val spike = Color(0xFF7B5642)
            oval(-0.03f, 0.45f, 0.40f, 0.44f, spike)
            for (i in 0..9) {
                val a = Math.toRadians(15.0 + i * 17.0)
                val bx = (-0.03 + 0.42 * cos(a)).toFloat(); val by = (0.45 + 0.46 * sin(a)).toFloat()
                dot(bx, by, 0.055f, spike)
            }
        }
        Trait.LION -> {
            val mane = Color(0xFFC06F2E)
            dot(0f, 0.70f, 0.34f, mane)
            for (i in 0..11) {
                val a = Math.toRadians(i * 30.0)
                dot((0.33 * cos(a)).toFloat(), 0.70f + (0.33 * sin(a)).toFloat(), 0.085f, mane)
            }
        }
        Trait.SHEEP -> for (i in 0..9) {
            val a = Math.toRadians(i * 36.0)
            dot((0.27 * cos(a)).toFloat(), 0.32f + (0.24 * sin(a)).toFloat(), 0.13f, White)
        }
        Trait.DRAGON -> {
            // Small scalloped wings.
            for (s in intArrayOf(-1, 1)) {
                poly(Color(0xFF3F9C63), s * 0.16f, 0.50f, s * 0.50f, 0.74f, s * 0.56f, 0.52f, s * 0.44f, 0.50f, s * 0.46f, 0.40f, s * 0.34f, 0.42f, s * 0.30f, 0.34f)
                dot(s * 0.50f, 0.74f, 0.03f, Color(0xFF3F9C63))
            }
        }
        else -> {}
    }

    // Feet, body, belly, arms.
    val foot = if (k.trait == Trait.PENGUIN || k.trait == Trait.OWL) Orange else dark
    oval(-0.13f, 0.03f, 0.09f, 0.042f, foot)
    oval(0.13f, 0.03f, 0.09f, 0.042f, foot)
    val bodyColor = if (k.trait == Trait.SHEEP) White else k.body
    oval(0f, 0.30f, 0.29f, 0.29f + breath, bodyColor)
    oval(0f, 0.25f, 0.185f, 0.205f, k.belly)
    val arm = if (k.trait == Trait.SHEEP) k.body else dark
    oval(-0.27f, 0.32f, 0.065f, 0.13f, arm)
    oval(0.27f, 0.32f, 0.065f, 0.13f, arm)

    // Ears that sit behind the head.
    for (s in intArrayOf(-1, 1)) when (k.ear) {
        Ear.ROUND -> { dot(s * 0.20f, 0.93f, if (k.tail == Tail.THIN) 0.13f else 0.09f, k.body); dot(s * 0.20f, 0.93f, if (k.tail == Tail.THIN) 0.085f else 0.05f, k.earIn) }
        Ear.POINT -> { poly(k.body, s * 0.06f, 0.90f, s * 0.27f, 0.84f, s * 0.23f, 1.10f); poly(k.earIn, s * 0.12f, 0.91f, s * 0.23f, 0.89f, s * 0.215f, 1.03f) }
        Ear.LONG -> { oval(s * 0.11f, 1.10f, 0.068f, 0.21f, k.body); oval(s * 0.11f, 1.10f, 0.032f, 0.15f, k.earIn) }
        Ear.TUFT -> poly(dark, s * 0.08f, 0.92f, s * 0.25f, 0.86f, s * 0.26f, 1.05f)
        Ear.SIDE -> { dot(s * 0.26f, 0.72f, 0.10f, k.body); dot(s * 0.26f, 0.72f, 0.06f, k.earIn) }
        Ear.HORN -> poly(Cream, s * 0.09f, 0.93f, s * 0.21f, 0.90f, s * 0.19f, 1.09f)
        Ear.ANTLER -> {
            val c = Color(0xFF8A6442)
            line(s * 0.12f, 0.92f, s * 0.20f, 1.14f, 0.03f, c)
            line(s * 0.17f, 1.04f, s * 0.29f, 1.10f, 0.026f, c)
            line(s * 0.19f, 1.10f, s * 0.13f, 1.20f, 0.024f, c)
            oval(s * 0.27f, 0.84f, 0.07f, 0.04f, k.body)
        }
        else -> {}
    }

    // Head.
    val headColor = if (k.trait == Trait.SHEEP) k.body else k.body
    if (k.trait == Trait.FROG) {
        oval(0f, 0.68f, 0.29f, 0.22f, k.body)
        for (s in intArrayOf(-1, 1)) { dot(s * 0.14f, 0.88f, 0.095f, k.body); dot(s * 0.14f, 0.89f, 0.062f, White) }
    } else dot(0f, 0.70f, 0.25f, headColor)
    if (k.trait == Trait.SHEEP) for (x in listOf(-0.15f, -0.05f, 0.05f, 0.15f)) dot(x, 0.90f, 0.085f, White)
    if (k.ear == Ear.FLOPPY) for (s in intArrayOf(-1, 1)) oval(s * 0.26f, 0.66f, 0.075f, 0.16f, k.earIn)
    if (k.trait == Trait.MONKEY) {
        // The pale, heart-shaped face.
        dot(-0.09f, 0.75f, 0.11f, k.belly); dot(0.09f, 0.75f, 0.11f, k.belly)
        oval(0f, 0.66f, 0.17f, 0.13f, k.belly)
    }
    if (k.trait == Trait.DONKEY) {
        // A dark tuft of mane and a long, pale muzzle with nostrils.
        for ((x, y) in listOf(-0.05f to 0.93f, 0.03f to 0.95f, 0.09f to 0.91f)) dot(x, y, 0.06f, Color(0xFF4F4A5A))
        oval(0f, 0.60f, 0.19f, 0.14f, k.belly)
        dot(-0.06f, 0.58f, 0.018f, Ink, 0.7f); dot(0.06f, 0.58f, 0.018f, Ink, 0.7f)
    }
    k.muzzle?.let { oval(0f, 0.63f, 0.135f, 0.10f, it) }
    if (k.trait == Trait.DRAGON) for (x in listOf(-0.07f, 0.07f)) dot(x, 0.62f, 0.012f, Ink, 0.6f)

    // Face.
    when (k.trait) {
        Trait.OWL -> {
            for (s in intArrayOf(-1, 1)) dot(s * 0.105f, 0.74f, 0.10f, Cream)
            eyes(0.74f, 0.105f, closed, 0.042f)
            poly(Orange, -0.035f, 0.67f, 0.035f, 0.67f, 0f, 0.60f)
            for (x in listOf(-0.07f, 0.07f)) for (y in listOf(0.30f, 0.20f)) arc(x, y, 0.035f, 20f, 140f, 0.012f, dark, 0.7f)
        }
        Trait.PENGUIN -> {
            oval(0f, 0.68f, 0.18f, 0.16f, White)
            eyes(0.72f, 0.085f, closed)
            poly(Orange, -0.04f, 0.66f, 0.04f, 0.66f, 0f, 0.60f)
            blush(0.65f, 0.13f, 0.034f)
        }
        Trait.FROG -> {
            for (s in intArrayOf(-1, 1)) if (closed) arc(s * 0.14f, 0.90f, 0.035f, 20f, 140f, 0.016f, Ink) else dot(s * 0.14f, 0.89f, 0.032f, Ink)
            arc(0f, 0.70f, 0.13f, 30f, 120f, 0.018f, Ink)
            blush(0.66f, 0.20f)
        }
        else -> {
            eyes(if (k.trait == Trait.DONKEY) 0.77f else 0.73f, 0.095f, closed)
            if (k.trait != Trait.DONKEY) oval(0f, 0.665f, 0.03f, 0.022f, Ink)
            val mouthY = if (k.trait == Trait.DONKEY) 0.55f else 0.655f
            if (asleep) dot(0f, mouthY - 0.03f, 0.012f, Ink, 0.7f) else smile(0f, mouthY, 0.036f, 0.014f)
            blush(0.655f, 0.165f)
            if (k.trait == Trait.WHISKERS || k.trait == Trait.CONDUCTOR) for (s in intArrayOf(-1, 1)) {
                line(s * 0.11f, 0.655f, s * 0.24f, 0.675f, 0.008f, Ink, 0.5f)
                line(s * 0.11f, 0.64f, s * 0.24f, 0.625f, 0.008f, Ink, 0.5f)
            }
        }
    }

    if (k.trait == Trait.CONDUCTOR) {
        val blue = Color(0xFF3F5BD9)
        box(0f, 0.985f, 0.26f, 0.10f, 0.03f, blue)
        box(0.04f, 0.935f, 0.34f, 0.035f, 0.015f, Color(0xFF2B3FA6))
        dot(0f, 0.99f, 0.022f, Sun)
    }
    if (k.trait == Trait.OWL) {
        oval(-0.27f, 0.32f, 0.08f, 0.17f, dark)
        oval(0.27f, 0.32f, 0.08f, 0.17f, dark)
    }
}
