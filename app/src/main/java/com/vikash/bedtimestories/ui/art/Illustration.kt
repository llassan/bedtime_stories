package com.vikash.bedtimestories.ui.art

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vikash.bedtimestories.data.Scene
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

/** How much room a character takes ([width]), how big it is drawn ([scale]), and whether it hovers ([lift]). */
private class Actor(
    val width: Float,
    val scale: Float = 1f,
    val lift: Float = 0f,
    val shadow: Boolean = true,
    val draw: Pen.(t: Float, asleep: Boolean, look: ChildLook) -> Unit,
)

private val ACTORS: Map<String, Actor> = buildMap {
    put("hero", Actor(0.62f) { t, z, look -> child(look, t, z) })
    put("painter", Actor(0.62f, 1.05f) { t, z, look -> child(look, t, z, painter = true) })
    for ((name, kind) in KINDS) {
        val scale = when (name) { "lion" -> 1.15f; "monkey" -> 0.9f; "donkey" -> 1.05f; "mouse" -> 0.6f; "squirrel", "hedgehog", "frog" -> 0.75f; "rabbit", "cat", "puppy", "penguin", "owl" -> 0.85f; "bear" -> 1.12f; "dragon" -> 1.15f; else -> 1f }
        val width = when (name) { "fox", "squirrel", "dragon", "jackal", "bluejackal", "wolf", "lion", "monkey" -> 0.9f; else -> 0.72f }
        put(name, Actor(width, scale) { t, z, _ -> critter(kind, t, z) })
    }
    put("elephant", Actor(1.3f, 1.25f) { t, _, _ -> elephant(t) })
    put("crocodile", Actor(1.5f, 1.05f) { t, _, _ -> crocodile(t) })
    put("crow", Actor(0.6f, 0.8f) { t, _, _ -> groundBird(t, Color(0xFF2E2E42), Color(0xFF1C1C2B), Color(0xFF5A5A70)) })
    put("dove", Actor(0.6f, 0.75f) { t, _, _ -> groundBird(t, Color(0xFFF2F3F8), Color(0xFFD3D8E6), Color(0xFFFFA63D)) })
    put("goose", Actor(0.7f, 1f) { t, _, _ -> groundBird(t, Color.White, Color(0xFFE3E7F2), Color(0xFFFFA63D), neck = 0.16f) })
    put("crane", Actor(0.8f, 1.05f) { t, _, _ -> crane(t) })
    put("crab", Actor(0.7f, 0.8f) { t, _, _ -> crab(t) })
    put("snake", Actor(0.7f, 0.9f) { t, _, _ -> snake(t) })
    put("ant", Actor(0.5f, 0.55f) { t, _, _ -> ant(t) })
    put("pitcher", Actor(0.55f, 0.9f) { _, _, _ -> pitcher() })
    put("grapes", Actor(0.75f, 1.1f) { t, _, _ -> grapes(t) })
    put("well", Actor(0.9f, 1.1f) { _, _, _ -> well() })
    put("boy", Actor(0.62f, 0.95f) { t, z, _ -> child(ChildLook(2, 0, 1, 2), t, z) })
    put("capseller", Actor(0.62f, 1.05f) { t, z, _ -> child(ChildLook(3, 0, 1, 3), t, z); cap(Color(0xFFE5564E)) })
    put("hen", Actor(0.6f, 0.85f) { t, _, _ -> hen(t) })
    put("egg", Actor(0.45f, 0.8f, shadow = false) { t, _, _ -> goldenEgg(t) })
    put("bell", Actor(0.5f, 0.9f, lift = 0.3f, shadow = false) { t, _, _ -> bell(t) })
    put("sun", Actor(0.9f, 1.1f, lift = 0.75f, shadow = false) { t, _, _ -> sun(t) })
    put("snail", Actor(0.8f, 0.7f) { t, z, _ -> snail(t, z) })
    put("fish", Actor(0.7f, 0.9f, lift = 0.35f, shadow = false) { t, _, _ -> fish(t, glowing = false) })
    put("lumi", Actor(0.75f, 1f, lift = 0.35f, shadow = false) { t, _, _ -> fish(t, glowing = true) })
    put("turtle", Actor(0.75f, 0.9f, lift = 0.20f, shadow = false) { t, _, _ -> turtle(t) })
    put("tortoise", Actor(0.8f, 1f) { t, _, _ -> turtle(t) })
    put("whale", Actor(1.25f, 1.25f, lift = 0.15f, shadow = false) { t, z, _ -> whale(t, z) })
    put("seal", Actor(0.9f, 0.9f) { t, _, _ -> seal(t) })
    put("cloud", Actor(0.9f, 1.1f, lift = 0.85f, shadow = false) { t, z, _ -> cloud(t, z) })
    put("train", Actor(1.15f, 1.15f) { t, _, _ -> train(t) })
    put("boat", Actor(1.1f, 1.1f, shadow = false) { t, _, _ -> boat(t) })
    put("honey", Actor(0.5f, 0.8f) { _, _, _ -> honey() })
    put("star", Actor(0.5f, 1f, lift = 0.55f, shadow = false) { t, _, _ -> star(t) })
    put("heart", Actor(0.45f, 0.9f, lift = 0.55f, shadow = false) { t, _, _ -> heart(t) })
    put("ladder", Actor(0.45f, 1f) { _, _, _ -> ladder() })
    put("zzz", Actor(0.4f, 1f, lift = 0.5f, shadow = false) { t, _, _ -> zzz(t) })
    put("house", Actor(0.95f, 1.2f) { t, _, _ -> house(t) })
    put("oak", Actor(0.8f, 1.5f) { _, _, _ -> oak() })
    put("tree", Actor(0.95f, 1.2f) { t, _, _ -> thankTree(t) })
    put("button", Actor(0.4f, 0.9f, lift = 0.35f, shadow = false) { t, _, _ -> button(t) })
    put("sparkle", Actor(0.45f, 1f, lift = 0.35f, shadow = false) { t, _, _ -> sparkle(t) })
    put("bird", Actor(0.55f, 0.8f, lift = 1.0f, shadow = false) { t, _, _ -> bird(t) })
}

/** The names that can appear in a page's cast. */
val ALL_ACTORS: List<String> get() = ACTORS.keys.toList()

private class Speck(val x: Float, val y: Float, val r: Float, val phase: Float)

/**
 * A story illustration: a painted, gently moving backdrop for [scene] with the
 * characters named in [cast] standing in it. [cast] is a space-separated list
 * such as "hero owl star"; add "~" to a name ("hero~") to show it asleep.
 */
@Composable
fun Illustration(
    scene: Scene, cast: String, look: ChildLook, modifier: Modifier = Modifier, animated: Boolean = false,
    /** Space at the top that buttons cover, so the moon is kept clear of them. */
    topInset: Dp = 0.dp,
) {
    val time = remember { mutableFloatStateOf(0f) }
    if (animated) LaunchedEffect(Unit) {
        val start = withFrameNanos { it }
        while (true) withFrameNanos { time.floatValue = (it - start) / 1_000_000_000f }
    }
    val seed = cast.hashCode() * 31 + scene.ordinal
    val specks = remember(seed) {
        val r = Random(seed)
        List(40) { Speck(r.nextFloat(), r.nextFloat(), 0.5f + r.nextFloat(), r.nextFloat() * 6.28f) }
    }
    val tokens = remember(cast) { cast.split(' ').filter { it.isNotBlank() } }

    Canvas(modifier.clipToBounds()) {
        val t = time.floatValue
        backdrop(scene, t, specks, topInset.toPx())

        val players = tokens.mapNotNull { tok -> ACTORS[tok.trimEnd('~')]?.let { Triple(it, tok.endsWith('~'), tok.trimEnd('~')) } }
        if (players.isEmpty()) return@Canvas
        val w = size.width
        val h = size.height
        val total = players.sumOf { (it.first.width * it.first.scale).toDouble() }.toFloat()
        val unit = min(h * 0.46f, w * 0.92f / total)
        val sea = scene == Scene.SEA
        val afloat = sea || scene == Scene.OCEAN
        val ground = h * (if (sea) 0.80f else 0.87f)
        var x = (w - total * unit) / 2f
        players.forEachIndexed { i, (actor, asleep, name) ->
            val span = actor.width * actor.scale * unit
            val cx = x + span / 2f
            x += span
            val u = unit * actor.scale
            // Under the sea everything drifts; on land only the hovering things do.
            val floats = actor.lift > 0f || afloat
            val lift = if (sea && actor.lift == 0f) 0.12f else actor.lift
            val bob = if (floats) sin(t * 1.3f + i * 1.7f) * 0.03f else 0f
            val gy = ground - (lift + bob) * u
            if (actor.shadow && !afloat) drawOval(Color.Black.copy(alpha = 0.16f), Offset(cx - span * 0.36f, ground - u * 0.03f), Size(span * 0.72f, u * 0.07f))
            val flip = players.size > 1 && i > (players.size - 1) / 2f
            val pen = Pen(this, cx, gy, u, flip)
            if (sea && name == "hero") pen.bubble()
            actor.draw(pen, t, asleep, look)
        }
    }
}

/** The child's face in a circle, for the profile button. */
@Composable
fun ChildPortrait(look: ChildLook, modifier: Modifier = Modifier, background: Color = Color(0xFF2A3180)) {
    Canvas(modifier.clip(CircleShape)) {
        drawRect(background)
        val u = size.minDimension * 1.55f
        Pen(this, size.width / 2f, size.height * 0.5f + 0.72f * u, u, false).childPortrait(look)
    }
}

private fun DrawScope.backdrop(scene: Scene, t: Float, specks: List<Speck>, topInset: Float) {
    val w = size.width
    val h = size.height
    drawRect(Brush.verticalGradient(listOf(scene.top, scene.bottom)))

    if (scene == Scene.SKY || scene == Scene.JUNGLE) {
        // The last glow of sunset on the horizon.
        drawCircle(Brush.radialGradient(listOf(Color(0x99FFD9A0), Color.Transparent), Offset(w * 0.3f, h * 0.78f), w * 0.7f), w * 0.7f, Offset(w * 0.3f, h * 0.78f))
    }
    if (scene == Scene.SEA) {
        // Shafts of light from the surface.
        for (i in 0..3) {
            val x = w * (0.1f + i * 0.26f) + sin(t * 0.3f + i) * w * 0.02f
            val ray = Path().apply { moveTo(x, 0f); lineTo(x + w * 0.10f, 0f); lineTo(x + w * 0.02f, h); lineTo(x - w * 0.16f, h); close() }
            drawPath(ray, Color.White.copy(alpha = 0.05f))
        }
    }

    if (scene.stars) specks.take(if (scene == Scene.SKY || scene == Scene.JUNGLE) 16 else 34).forEach { s ->
        val twinkle = 0.55f + 0.45f * sin(t * (0.8f + s.r) + s.phase)
        drawCircle(Color.White.copy(alpha = (0.25f + 0.6f * twinkle).coerceIn(0f, 1f)), (0.6f + s.r * 0.9f) * density, Offset(s.x * w, s.y * h * 0.55f))
    }

    if (scene.moon) {
        val r = min(w, h) * 0.085f
        val c = Offset(w * 0.80f, maxOf(h * 0.20f, topInset + r * 1.4f))
        drawCircle(Brush.radialGradient(listOf(Color(0x55FFF3C4), Color.Transparent), c, r * 3.4f), r * 3.4f, c)
        drawCircle(Color(0xFFFFF3C4), r, c)
        drawCircle(Color(0x22B79B5E), r * 0.22f, c + Offset(-r * 0.3f, -r * 0.25f))
        drawCircle(Color(0x22B79B5E), r * 0.15f, c + Offset(r * 0.35f, r * 0.2f))
        drawCircle(Color(0x22B79B5E), r * 0.11f, c + Offset(-r * 0.1f, r * 0.45f))
    }

    // Drifting clouds.
    if (scene == Scene.NIGHT || scene == Scene.SKY || scene == Scene.MEADOW) {
        val tint = if (scene == Scene.SKY) Color(0xFFFFD6DC) else Color.White
        val alpha = if (scene == Scene.SKY) 0.55f else 0.10f
        for (i in 0..2) {
            val cx = ((0.15f + i * 0.38f + t * 0.006f * (i + 1)) % 1.3f - 0.15f) * w
            val cy = h * (0.16f + i * 0.11f)
            val s = h * (0.07f + i * 0.015f)
            softCloud(cx, cy, s, tint.copy(alpha = alpha))
        }
    }

    // Far hills.
    val far = Path().apply {
        moveTo(0f, h * 0.70f)
        quadraticTo(w * 0.25f, h * 0.54f, w * 0.55f, h * 0.70f)
        quadraticTo(w * 0.80f, h * 0.82f, w, h * 0.62f)
        lineTo(w, h); lineTo(0f, h); close()
    }
    drawPath(far, scene.hillFar)

    when (scene) {
        Scene.FOREST -> {
            for (i in 0..7) pine(w * (0.02f + i * 0.14f), h * (0.74f + (i % 3) * 0.02f), h * (0.34f + (i % 2) * 0.09f), Color(0xFF123A33))
            for (x in listOf(0.04f, 0.93f)) pine(w * x, h * 0.90f, h * 0.62f, Color(0xFF0A2621))
        }
        Scene.JUNGLE -> for ((i, x) in listOf(0.04f, 0.17f, 0.80f, 0.94f).withIndex()) {
            // Broad-leafed trees with round crowns.
            val base = h * 0.78f
            val tall = h * (0.42f + (i % 2) * 0.10f)
            val leaf = if (i % 2 == 0) Color(0xFF1C5A3E) else Color(0xFF256B48)
            drawRect(Color(0xFF4A3526), Offset(x * w - tall * 0.035f, base - tall * 0.6f), Size(tall * 0.07f, tall * 0.6f))
            for ((dx, dy, r) in listOf(Triple(0f, 0.78f, 0.26f), Triple(-0.2f, 0.62f, 0.2f), Triple(0.2f, 0.62f, 0.2f), Triple(0f, 0.58f, 0.2f))) {
                drawCircle(leaf, tall * r, Offset(x * w + tall * dx, base - tall * dy))
            }
        }
        Scene.RIVER -> {
            // A slow river between the far bank and the near one.
            drawRect(Brush.verticalGradient(listOf(Color(0xFF3B5FB8), Color(0xFF27408F)), h * 0.66f, h * 0.90f), Offset(0f, h * 0.66f), Size(w, h * 0.24f))
            for (i in 0..4) {
                val y = h * (0.685f + i * 0.022f)
                val half = w * (0.04f + i * 0.015f)
                val x = w * 0.80f + sin(t * 0.9f + i) * w * 0.012f
                drawLine(Color(0xFFFFF3C4).copy(alpha = 0.4f - i * 0.05f), Offset(x - half, y), Offset(x + half, y), 2f * density, StrokeCap.Round)
            }
            for (i in 0..3) {
                val x = ((0.1f + i * 0.27f + t * 0.01f) % 1f) * w
                drawLine(Color.White.copy(alpha = 0.18f), Offset(x, h * (0.70f + (i % 2) * 0.04f)), Offset(x + w * 0.08f, h * (0.70f + (i % 2) * 0.04f)), 1.6f * density, StrokeCap.Round)
            }
        }
        Scene.NIGHT -> for (x in listOf(0.08f, 0.16f, 0.90f)) pine(w * x, h * 0.74f, h * 0.20f, Color(0xFF151A4A))
        Scene.SNOW -> for ((i, x) in listOf(0.06f, 0.20f, 0.86f, 0.95f).withIndex()) pine(w * x, h * 0.76f, h * (0.26f + (i % 2) * 0.08f), Color(0xFF2E5A6E), snow = true)
        else -> {}
    }

    // The ground the characters stand on.
    val near = Path().apply {
        moveTo(0f, h * 0.82f)
        quadraticTo(w * 0.5f, h * 0.72f, w, h * 0.84f)
        lineTo(w, h); lineTo(0f, h); close()
    }
    drawPath(near, scene.hill)

    when (scene) {
        Scene.OCEAN -> {
            // Moonlight on the water, and slow rolling waves.
            for (i in 0..5) {
                val y = h * (0.80f + i * 0.035f)
                val half = w * (0.05f + i * 0.02f)
                val x = w * 0.80f + sin(t * 0.9f + i) * w * 0.012f
                drawLine(Color(0xFFFFF3C4).copy(alpha = 0.35f - i * 0.04f), Offset(x - half, y), Offset(x + half, y), 2.2f * density, StrokeCap.Round)
            }
            for (row in 0..2) {
                val y = h * (0.86f + row * 0.05f)
                val wave = Path().apply {
                    moveTo(0f, y)
                    var x = 0f
                    var up = true
                    val step = w / 8f
                    val shift = sin(t * 0.8f + row * 1.4f) * h * 0.006f
                    while (x < w) {
                        quadraticTo(x + step / 2f, y + (if (up) -1 else 1) * h * 0.012f + shift, x + step, y)
                        x += step
                        up = !up
                    }
                }
                drawPath(wave, Color.White.copy(alpha = 0.16f), style = androidx.compose.ui.graphics.drawscope.Stroke(1.6f * density, cap = StrokeCap.Round))
            }
        }
        Scene.SEA -> {
            for (i in 0..5) {
                val bx = w * (0.05f + i * 0.18f)
                val sway = sin(t * 1.1f + i) * w * 0.012f
                val weed = Path().apply {
                    moveTo(bx, h)
                    quadraticTo(bx + sway * 2f, h * 0.86f, bx + sway, h * (0.74f - (i % 2) * 0.05f))
                }
                drawPath(weed, Color(0xFF2FA58A), style = androidx.compose.ui.graphics.drawscope.Stroke(w * 0.014f, cap = StrokeCap.Round))
            }
            specks.take(12).forEach { s ->
                val y = ((s.y - t * 0.03f * s.r) % 1f + 1f) % 1f
                drawCircle(Color.White.copy(alpha = 0.22f), (1.5f + s.r * 3f) * density, Offset(s.x * w + sin(t + s.phase) * 6f, y * h))
            }
        }
        Scene.MEADOW -> specks.take(14).forEach { s ->
            val fx = s.x * w
            val fy = h * (0.84f + s.y * 0.14f)
            val c = if (s.r > 1f) Color(0xFFFFE08A) else Color(0xFFFFB3C7)
            for (k in 0..4) {
                val a = k * 2f * PI.toFloat() / 5f
                drawCircle(c, 2.2f * density, Offset(fx + cos(a) * 3f * density, fy + sin(a) * 3f * density))
            }
            drawCircle(Color.White, 1.6f * density, Offset(fx, fy))
        }
        Scene.SNOW -> specks.forEach { s ->
            val y = ((s.y + t * 0.04f * s.r) % 1f)
            drawCircle(Color.White.copy(alpha = 0.8f), (0.9f + s.r) * density, Offset(s.x * w + sin(t * 0.7f + s.phase) * 8f, y * h))
        }
        else -> {}
    }

    // Fireflies.
    if (scene == Scene.FOREST || scene == Scene.MEADOW || scene == Scene.JUNGLE) specks.takeLast(9).forEach { s ->
        val fx = s.x * w + sin(t * 0.5f + s.phase) * w * 0.03f
        val fy = h * (0.45f + s.y * 0.35f) + cos(t * 0.4f + s.phase) * h * 0.02f
        val glow = 0.5f + 0.5f * sin(t * 1.8f + s.phase)
        drawCircle(Brush.radialGradient(listOf(Color(0xFFFFF29A).copy(alpha = 0.5f * glow), Color.Transparent), Offset(fx, fy), 9f * density), 9f * density, Offset(fx, fy))
        drawCircle(Color(0xFFFFF29A).copy(alpha = 0.5f + 0.5f * glow), 1.8f * density, Offset(fx, fy))
    }
}

private fun DrawScope.softCloud(cx: Float, cy: Float, s: Float, c: Color) {
    // One outline, so the overlapping puffs do not show through each other.
    fun puff(x: Float, y: Float, r: Float) = Path().apply { addOval(androidx.compose.ui.geometry.Rect(Offset(x, y), r)) }
    var shape = puff(cx - s * 1.1f, cy + s * 0.15f, s)
    for (p in listOf(puff(cx, cy - s * 0.2f, s * 1.35f), puff(cx + s * 1.2f, cy + s * 0.12f, s * 1.05f),
        Path().apply { addOval(androidx.compose.ui.geometry.Rect(cx - s * 2f, cy + s * 0.1f, cx + s * 2f, cy + s * 1.2f)) })) {
        shape = Path.combine(androidx.compose.ui.graphics.PathOperation.Union, shape, p)
    }
    drawPath(shape, c)
}

private fun DrawScope.pine(x: Float, base: Float, height: Float, c: Color, snow: Boolean = false) {
    val half = height * 0.26f
    drawRect(Color(0xFF3A2A22), Offset(x - height * 0.03f, base - height * 0.12f), Size(height * 0.06f, height * 0.12f))
    for (i in 0..2) {
        val top = base - height + i * height * 0.24f
        val bottom = base - height * 0.10f - (2 - i) * height * 0.20f
        val wide = half * (0.62f + i * 0.19f)
        val tier = Path().apply { moveTo(x, top); lineTo(x + wide, bottom); lineTo(x - wide, bottom); close() }
        drawPath(tier, c)
        if (snow) {
            val cap = Path().apply { moveTo(x, top); lineTo(x + wide * 0.45f, top + (bottom - top) * 0.45f); lineTo(x - wide * 0.45f, top + (bottom - top) * 0.45f); close() }
            drawPath(cap, Color.White.copy(alpha = 0.9f))
        }
    }
}
