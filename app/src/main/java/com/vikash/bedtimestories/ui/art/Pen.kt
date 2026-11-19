package com.vikash.bedtimestories.ui.art

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

internal val Ink = Color(0xFF2B2440)
internal val Blush = Color(0xFFFF8FA8)
internal val Cream = Color(0xFFFFF3D6)
internal val Sun = Color(0xFFFFD66B)

/**
 * Draws one character in its own small coordinate system: the origin is the
 * spot on the ground under the character, x runs right, y runs UP, and 1.0 is
 * roughly the character's height. [flip] mirrors it to face the other way.
 */
internal class Pen(val d: DrawScope, val cx: Float, val gy: Float, val u: Float, val flip: Boolean) {
    fun px(x: Float) = cx + (if (flip) -x else x) * u
    fun py(y: Float) = gy - y * u

    fun oval(x: Float, y: Float, rx: Float, ry: Float, c: Color, a: Float = 1f) =
        d.drawOval(c, Offset(px(x) - rx * u, py(y) - ry * u), Size(rx * 2 * u, ry * 2 * u), alpha = a)

    fun dot(x: Float, y: Float, r: Float, c: Color, a: Float = 1f) =
        d.drawCircle(c, r * u, Offset(px(x), py(y)), alpha = a)

    fun line(x1: Float, y1: Float, x2: Float, y2: Float, w: Float, c: Color, a: Float = 1f) =
        d.drawLine(c, Offset(px(x1), py(y1)), Offset(px(x2), py(y2)), w * u, StrokeCap.Round, alpha = a)

    /** A soft light that fades out to nothing at radius [r]. */
    fun glow(x: Float, y: Float, r: Float, c: Color, a: Float = 0.5f) {
        val centre = Offset(px(x), py(y))
        d.drawCircle(Brush.radialGradient(listOf(c.copy(alpha = a), Color.Transparent), centre, r * u), r * u, centre)
    }

    /** A filled shape through the given x,y pairs. */
    fun poly(c: Color, vararg p: Float, a: Float = 1f) {
        val path = Path()
        path.moveTo(px(p[0]), py(p[1]))
        var i = 2
        while (i < p.size) {
            path.lineTo(px(p[i]), py(p[i + 1]))
            i += 2
        }
        path.close()
        d.drawPath(path, c, alpha = a)
    }

    /** A rounded box centred on x,y. */
    fun box(x: Float, y: Float, w: Float, h: Float, r: Float, c: Color, a: Float = 1f) =
        d.drawRoundRect(c, Offset(px(x) - w / 2 * u, py(y) - h / 2 * u), Size(w * u, h * u), CornerRadius(r * u), alpha = a)

    /** Part of a circle outline. Angles are in screen degrees: 0 is right, 90 is down. */
    fun arc(x: Float, y: Float, r: Float, start: Float, sweep: Float, w: Float, c: Color, a: Float = 1f) =
        d.drawArc(
            c, start, sweep, false,
            Offset(px(x) - r * u, py(y) - r * u), Size(r * 2 * u, r * 2 * u),
            alpha = a, style = Stroke(w * u, cap = StrokeCap.Round),
        )

    fun eyes(y: Float, dx: Float, closed: Boolean, r: Float = 0.03f, x: Float = 0f) {
        for (s in intArrayOf(-1, 1)) {
            val ex = x + s * dx
            if (closed) arc(ex, y + 0.012f, r * 1.25f, 20f, 140f, 0.016f, Ink)
            else {
                dot(ex, y, r, Ink)
                dot(ex + 0.01f, y + 0.011f, r * 0.36f, Color.White)
            }
        }
    }

    fun smile(x: Float, y: Float, r: Float = 0.045f, w: Float = 0.016f) = arc(x, y, r, 25f, 130f, w, Ink)

    fun blush(y: Float, dx: Float, r: Float = 0.042f) {
        dot(-dx, y, r, Blush, 0.5f)
        dot(dx, y, r, Blush, 0.5f)
    }
}
