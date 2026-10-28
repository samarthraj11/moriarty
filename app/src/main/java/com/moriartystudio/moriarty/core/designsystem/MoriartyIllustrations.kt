package com.moriartystudio.moriarty.core.designsystem

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.moriartystudio.moriarty.ui.theme.Amber
import com.moriartystudio.moriarty.ui.theme.Emerald
import com.moriartystudio.moriarty.ui.theme.EmeraldDark
import com.moriartystudio.moriarty.ui.theme.Ink
import com.moriartystudio.moriarty.ui.theme.Ivory
import com.moriartystudio.moriarty.ui.theme.Mint
import com.moriartystudio.moriarty.ui.theme.Paper
import com.moriartystudio.moriarty.ui.theme.Peach
import com.moriartystudio.moriarty.ui.theme.SoftAmber
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Resolution-independent artwork for the game. The illustrations are deliberately built from
 * simple geometry so puzzle clues stay crisp on every Android density without shipping large
 * raster files.
 */
@Composable
fun GeometricMirrorHero(modifier: Modifier = Modifier) {
    IllustrationSurface(modifier) {
        val w = size.width
        val h = size.height

        drawCircle(SoftAmber.copy(alpha = .62f), w * .45f, Offset(w * .91f, h * .16f))
        drawCircle(Peach.copy(alpha = .20f), w * .34f, Offset(w * .05f, h * .82f))
        drawOval(Ink.copy(alpha = .11f), Offset(w * .07f, h * .82f), Size(w * .82f, h * .11f))

        drawIsoBlock(w * .10f, h * .52f, w * .26f, h * .32f, w * .055f, EmeraldDark)
        drawIsoBlock(w * .37f, h * .57f, w * .17f, h * .25f, w * .04f, Peach)

        val stairWidth = w * .14f
        repeat(5) { index ->
            val step = index.toFloat()
            drawIsoBlock(
                x = w * .50f + step * w * .075f,
                y = h * .75f - step * h * .065f,
                width = stairWidth,
                height = h * (.10f + step * .011f),
                depth = w * .025f,
                front = if (index % 2 == 0) Emerald else Ivory,
            )
        }

        drawMirror(
            left = w * .65f,
            top = h * .19f,
            right = w * .91f,
            bottom = h * .73f,
            frameWidth = w * .018f,
        )
        drawCircle(
            brush = Brush.radialGradient(
                listOf(Color(0xFFA7D9C4), Emerald, EmeraldDark),
                center = Offset(w * .29f, h * .42f),
                radius = w * .18f,
            ),
            radius = w * .14f,
            center = Offset(w * .30f, h * .43f),
        )
        drawCircle(Color.White.copy(alpha = .32f), w * .025f, Offset(w * .25f, h * .37f))
        drawCircle(
            brush = Brush.radialGradient(listOf(Mint, Color(0xFF5AA58A), EmeraldDark)),
            radius = w * .066f,
            center = Offset(w * .79f, h * .57f),
        )
    }
}

@Composable
fun MirrorClockIllustration(modifier: Modifier = Modifier) {
    IllustrationSurface(modifier) {
        val w = size.width
        val h = size.height
        val clockCenter = Offset(w * .54f, h * .43f)
        val clockRadius = w * .205f

        drawCircle(SoftAmber.copy(alpha = .78f), w * .38f, Offset(w * .12f, h * .13f))
        drawCircle(Mint.copy(alpha = .70f), w * .30f, Offset(w * .93f, h * .78f))
        drawOval(Ink.copy(alpha = .10f), Offset(w * .16f, h * .79f), Size(w * .68f, h * .10f))

        drawMirror(w * .20f, h * .09f, w * .84f, h * .82f, w * .022f)

        drawCircle(EmeraldDark.copy(alpha = .16f), clockRadius * 1.08f, clockCenter + Offset(w * .014f, h * .014f))
        drawCircle(Ivory, clockRadius, clockCenter)
        drawCircle(EmeraldDark, clockRadius, clockCenter, style = Stroke(w * .017f))
        drawCircle(Amber.copy(alpha = .36f), clockRadius * .83f, clockCenter, style = Stroke(w * .006f))

        repeat(12) { index ->
            val angle = (index * 30.0 - 90.0) * PI / 180.0
            val outer = Offset(
                clockCenter.x + cos(angle).toFloat() * clockRadius * .82f,
                clockCenter.y + sin(angle).toFloat() * clockRadius * .82f,
            )
            val innerScale = if (index % 3 == 0) .67f else .72f
            val inner = Offset(
                clockCenter.x + cos(angle).toFloat() * clockRadius * innerScale,
                clockCenter.y + sin(angle).toFloat() * clockRadius * innerScale,
            )
            drawLine(
                color = if (index % 3 == 0) EmeraldDark else Emerald.copy(alpha = .58f),
                start = inner,
                end = outer,
                strokeWidth = if (index % 3 == 0) w * .011f else w * .006f,
                cap = StrokeCap.Round,
            )
        }

        // The reflected clock displays 08:20. The player must infer the real time: 03:40.
        drawClockHand(clockCenter, clockRadius * .50f, 250f, w * .020f, EmeraldDark)
        drawClockHand(clockCenter, clockRadius * .68f, 120f, w * .012f, Peach)
        drawCircle(EmeraldDark, w * .026f, clockCenter)
        drawCircle(Ivory, w * .009f, clockCenter)

        drawIsoBlock(w * .08f, h * .66f, w * .20f, h * .20f, w * .04f, Emerald)
        drawCircle(
            brush = Brush.radialGradient(listOf(Mint, Color(0xFF62AA90), EmeraldDark)),
            radius = w * .07f,
            center = Offset(w * .18f, h * .63f),
        )
        drawIsoBlock(w * .76f, h * .70f, w * .13f, h * .15f, w * .03f, Peach)
    }
}

@Composable
fun SignalIllustration(modifier: Modifier = Modifier) {
    IllustrationSurface(modifier) {
        val w = size.width
        val h = size.height

        drawCircle(SoftAmber.copy(alpha = .65f), w * .42f, Offset(w * .03f, h * .18f))
        drawCircle(Peach.copy(alpha = .18f), w * .34f, Offset(w * .95f, h * .78f))
        drawOval(Ink.copy(alpha = .10f), Offset(w * .10f, h * .78f), Size(w * .78f, h * .12f))

        val wire = Path().apply {
            moveTo(w * .18f, h * .30f)
            cubicTo(w * .34f, h * .10f, w * .45f, h * .48f, w * .59f, h * .28f)
            cubicTo(w * .70f, h * .12f, w * .78f, h * .39f, w * .86f, h * .25f)
        }
        drawPath(wire, Amber.copy(alpha = .32f), style = Stroke(w * .034f, cap = StrokeCap.Round))
        drawPath(wire, Amber, style = Stroke(w * .012f, cap = StrokeCap.Round))

        listOf(Offset(w * .18f, h * .30f), Offset(w * .59f, h * .28f), Offset(w * .86f, h * .25f)).forEachIndexed { index, center ->
            drawCircle(Amber.copy(alpha = .20f), w * .075f, center)
            drawCircle(if (index == 1) Peach else Amber, w * .043f, center)
            drawCircle(Ivory.copy(alpha = .72f), w * .013f, center - Offset(w * .012f, h * .010f))
        }

        drawIsoBlock(w * .10f, h * .61f, w * .20f, h * .23f, w * .045f, EmeraldDark)
        drawIsoBlock(w * .32f, h * .52f, w * .18f, h * .31f, w * .045f, Peach)
        drawIsoBlock(w * .54f, h * .64f, w * .15f, h * .19f, w * .035f, Ivory)
        drawIsoBlock(w * .72f, h * .55f, w * .17f, h * .28f, w * .04f, Emerald)
        drawCircle(
            brush = Brush.radialGradient(listOf(Mint, Color(0xFF5EA98E), EmeraldDark)),
            radius = w * .085f,
            center = Offset(w * .59f, h * .57f),
        )
    }
}

@Composable
fun SmallGeometryThumbnail(modifier: Modifier = Modifier, accent: Color = Peach) {
    Canvas(modifier) {
        drawRoundRect(Color(0xFFF8E9CE), cornerRadius = CornerRadius(18f, 18f))
        drawIsoBlock(size.width * .13f, size.height * .55f, size.width * .29f, size.height * .28f, size.width * .05f, Emerald)
        drawIsoBlock(size.width * .47f, size.height * .37f, size.width * .30f, size.height * .46f, size.width * .05f, accent)
        drawCircle(Mint, size.width * .13f, Offset(size.width * .47f, size.height * .34f))
    }
}

@Composable
private fun IllustrationSurface(
    modifier: Modifier,
    draw: DrawScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(30.dp))
            .background(
                Brush.linearGradient(
                    listOf(Paper, Color(0xFFFFF1D5), Color(0xFFF8E8CC)),
                ),
            ),
    ) {
        Canvas(Modifier.fillMaxSize(), onDraw = draw)
    }
}

private fun DrawScope.drawMirror(
    left: Float,
    top: Float,
    right: Float,
    bottom: Float,
    frameWidth: Float,
) {
    val radius = (right - left) / 2f
    val mirror = Path().apply {
        moveTo(left, bottom)
        lineTo(left, top + radius)
        cubicTo(left, top, right, top, right, top + radius)
        lineTo(right, bottom)
        close()
    }
    drawPath(
        mirror,
        Brush.linearGradient(
            listOf(Color(0xFFE8F0EE), Color(0xFFBDD4D2), Color(0xFFF7EFE2)),
            start = Offset(left, top),
            end = Offset(right, bottom),
        ),
    )
    drawPath(mirror, EmeraldDark, style = Stroke(frameWidth, cap = StrokeCap.Round))
    drawLine(
        Color.White.copy(alpha = .58f),
        Offset(left + radius * .32f, top + radius * .46f),
        Offset(right - radius * .16f, bottom - radius * .45f),
        frameWidth * 1.7f,
        StrokeCap.Round,
    )
    drawLine(
        Color.White.copy(alpha = .34f),
        Offset(left + radius * .20f, top + radius * .85f),
        Offset(right - radius * .38f, bottom - radius * .24f),
        frameWidth * .75f,
        StrokeCap.Round,
    )
}

private fun DrawScope.drawIsoBlock(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    depth: Float,
    front: Color,
) {
    val top = Path().apply {
        moveTo(x, y)
        lineTo(x + depth, y - depth)
        lineTo(x + width + depth, y - depth)
        lineTo(x + width, y)
        close()
    }
    val side = Path().apply {
        moveTo(x + width, y)
        lineTo(x + width + depth, y - depth)
        lineTo(x + width + depth, y + height - depth)
        lineTo(x + width, y + height)
        close()
    }
    drawRect(front, Offset(x, y), Size(width, height))
    drawPath(top, front.lighten(.20f))
    drawPath(side, front.darken(.16f))
}

private fun DrawScope.drawClockHand(
    center: Offset,
    length: Float,
    degrees: Float,
    strokeWidth: Float,
    color: Color,
) {
    val angle = (degrees - 90.0) * PI / 180.0
    val end = Offset(
        center.x + cos(angle).toFloat() * length,
        center.y + sin(angle).toFloat() * length,
    )
    drawLine(color, center, end, strokeWidth, StrokeCap.Round)
}

private fun Color.lighten(amount: Float): Color = Color(
    red = red + (1f - red) * amount,
    green = green + (1f - green) * amount,
    blue = blue + (1f - blue) * amount,
    alpha = alpha,
)

private fun Color.darken(amount: Float): Color = Color(
    red = red * (1f - amount),
    green = green * (1f - amount),
    blue = blue * (1f - amount),
    alpha = alpha,
)
