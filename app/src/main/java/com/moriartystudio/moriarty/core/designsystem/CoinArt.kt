package com.moriartystudio.moriarty.core.designsystem

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.moriartystudio.moriarty.ui.theme.Gold
import com.moriartystudio.moriarty.ui.theme.GoldDark
import com.moriartystudio.moriarty.ui.theme.GoldLight
import kotlin.math.cos
import kotlin.math.sin

/** Vector stand-in for the 3D coin pile: a stack of coins with a few floating ones. */
@Composable
fun CoinStack(modifier: Modifier = Modifier) {
    Canvas(modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        // Floating coins first so the stack overlaps them.
        coin(Offset(w * 0.22f, h * 0.40f), w * 0.11f, tilt = 0.8f, thickness = w * 0.025f)
        coin(Offset(w * 0.84f, h * 0.16f), w * 0.12f, tilt = 0.9f, thickness = w * 0.025f)
        coin(Offset(w * 0.50f, h * 0.10f), w * 0.075f, tilt = 1f, thickness = w * 0.02f)
        coin(Offset(w * 0.90f, h * 0.55f), w * 0.09f, tilt = 0.7f, thickness = w * 0.022f)
        val thickness = h * 0.075f
        repeat(5) { k ->
            val dx = if (k % 2 == 0) 0f else w * 0.015f
            coin(Offset(w * 0.58f + dx, h * 0.86f - k * thickness), w * 0.27f, tilt = 0.42f, thickness = thickness)
        }
        coin(Offset(w * 0.30f, h * 0.88f), w * 0.15f, tilt = 0.42f, thickness = h * 0.055f)
    }
}

private fun DrawScope.coin(center: Offset, radius: Float, tilt: Float, thickness: Float) {
    val ry = radius * tilt
    // Rim: a darker body offset below the face gives the coin its thickness.
    drawRect(GoldDark, Offset(center.x - radius, center.y), Size(radius * 2, thickness))
    drawOval(GoldDark, Offset(center.x - radius, center.y + thickness - ry), Size(radius * 2, ry * 2))
    drawOval(
        Brush.linearGradient(listOf(GoldLight, Gold), Offset(center.x - radius, center.y - ry), Offset(center.x + radius, center.y + ry)),
        Offset(center.x - radius, center.y - ry),
        Size(radius * 2, ry * 2),
    )
    drawOval(
        GoldDark.copy(alpha = 0.45f),
        Offset(center.x - radius * 0.74f, center.y - ry * 0.74f),
        Size(radius * 1.48f, ry * 1.48f),
        style = Stroke(radius * 0.07f),
    )
    if (tilt > 0.6f) {
        val star = Path()
        for (i in 0 until 10) {
            val angle = Math.toRadians(-90.0 + i * 36.0)
            val r = radius * 0.42f * if (i % 2 == 0) 1f else 0.45f
            val p = Offset(center.x + (r * cos(angle)).toFloat(), center.y + (r * sin(angle)).toFloat() * tilt)
            if (i == 0) star.moveTo(p.x, p.y) else star.lineTo(p.x, p.y)
        }
        star.close()
        drawPath(star, GoldDark.copy(alpha = 0.6f))
    }
}
