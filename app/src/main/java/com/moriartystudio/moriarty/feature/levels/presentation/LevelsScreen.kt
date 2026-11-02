package com.moriartystudio.moriarty.feature.levels.presentation

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moriartystudio.moriarty.core.designsystem.IconCircle
import com.moriartystudio.moriarty.core.designsystem.TopBar
import com.moriartystudio.moriarty.core.model.Level
import com.moriartystudio.moriarty.core.model.PuzzleCategory
import com.moriartystudio.moriarty.ui.theme.Amber
import com.moriartystudio.moriarty.ui.theme.Emerald
import com.moriartystudio.moriarty.ui.theme.EmeraldDark
import com.moriartystudio.moriarty.ui.theme.SoftMint
import com.moriartystudio.moriarty.ui.theme.Ink
import com.moriartystudio.moriarty.ui.theme.InkMuted
import com.moriartystudio.moriarty.ui.theme.Ivory
import com.moriartystudio.moriarty.ui.theme.Line
import com.moriartystudio.moriarty.ui.theme.Mint
import com.moriartystudio.moriarty.ui.theme.Mist
import com.moriartystudio.moriarty.ui.theme.Peach
import com.moriartystudio.moriarty.ui.theme.SoftAmber
import com.moriartystudio.moriarty.ui.theme.SoftPeach
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

private val RowHeight = 140.dp
private val NodeSize = 76.dp
private val NodeCenterY = 52.dp
private val ObjectSize = 64.dp

@Composable
fun LevelsScreen(viewModel: LevelsViewModel, onBack: () -> Unit, onOpenPuzzle: (Int) -> Unit) {
    val state by viewModel.collectAsState()
    viewModel.collectSideEffect {
        when (it) {
            LevelsSideEffect.GoBack -> onBack()
            is LevelsSideEffect.OpenPuzzle -> onOpenPuzzle(it.level)
        }
    }

    val levels = state.levels
    val currentNumber = state.currentLevelNumber
    val current = levels.firstOrNull { it.number == currentNumber }
    val scrollState = rememberScrollState()
    val density = LocalDensity.current
    LaunchedEffect(currentNumber, levels.size) {
        // Open near the current level, as a game map should, rather than always at the top.
        val index = levels.indexOfFirst { it.number == currentNumber }
        val reversedIndex = if (index < 0) 0 else levels.lastIndex - index
        val target = with(density) { (RowHeight * reversedIndex - 240.dp).roundToPx() }
        scrollState.scrollTo(target.coerceAtLeast(0))
    }

    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        containerColor = Ivory,
        topBar = {
            TopBar(
                title = "All puzzles",
                subtitle = "${state.solvedCount} of ${levels.size} solved",
                leading = { IconCircle(Icons.AutoMirrored.Outlined.ArrowBack, viewModel::goBack, "Back") },
            )
        },
        bottomBar = {
            Surface(color = Ivory, shadowElevation = 10.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(if (current != null) "UP NEXT" else "ALL SOLVED", fontSize = 10.sp, letterSpacing = 1.5.sp, color = InkMuted)
                        Text(current?.let { "Level ${it.number}" } ?: "Journey complete", fontWeight = FontWeight.Bold, color = Ink, fontSize = 19.sp)
                    }
                    Button(
                        onClick = { current?.let { viewModel.openLevel(it.number, it.isUnlocked) } },
                        enabled = current != null,
                        modifier = Modifier.height(52.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald),
                    ) {
                        Text("Continue", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
                    }
                }
            }
        },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(scrollState)) {
            MapIntro()
            // Reversed: locked future levels at the top, conquered levels below.
            PuzzleJourney(
                levels = levels.asReversed(),
                currentNumber = currentNumber,
                onOpenLevel = { viewModel.openLevel(it.number, it.isUnlocked) },
            )
            Spacer(Modifier.height(28.dp))
        }
    }
}

@Composable
private fun MapIntro() {
    Column(Modifier.padding(horizontal = 24.dp, vertical = 14.dp)) {
        Text("THE JOURNEY", fontSize = 11.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold, color = Amber)
        Spacer(Modifier.height(6.dp))
        Text("Patterns are everywhere.", color = Ink, fontSize = 23.sp, fontWeight = FontWeight.Bold)
        Text("Follow the path. Discover what others miss.", color = InkMuted, fontSize = 13.sp)
    }
}

@Composable
private fun PuzzleJourney(levels: List<Level>, currentNumber: Int?, onOpenLevel: (Level) -> Unit) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val trackWidth = maxWidth
        // Gentle left/right weave; decorations always sit on the opposite side of the node.
        val xFractions = levels.indices.map { if (it % 2 == 0) 0.28f else 0.72f }
        Canvas(Modifier.fillMaxWidth().height(RowHeight * levels.size)) {
            val rowPx = RowHeight.toPx()
            val centers = xFractions.mapIndexed { index, x ->
                Offset(NodeSize.toPx() / 2 + x * (size.width - NodeSize.toPx()), index * rowPx + NodeCenterY.toPx())
            }
            val stroke = 10.dp.toPx()
            centers.zipWithNext().forEachIndexed { index, (a, b) ->
                val midY = (a.y + b.y) / 2f
                val segment = Path().apply { moveTo(a.x, a.y); cubicTo(a.x, midY, b.x, midY, b.x, b.y) }
                // levels[] is top-down (highest first), so the lower level closes the segment.
                if (levels[index + 1].isSolved) {
                    drawPath(segment, Emerald.copy(alpha = 0.35f), style = Stroke(stroke, cap = StrokeCap.Round))
                } else {
                    drawPath(segment, Line, style = Stroke(stroke / 1.6f, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2f, stroke * 1.9f))))
                }
            }
        }
        Column(Modifier.fillMaxWidth()) {
            levels.forEachIndexed { index, level ->
                val xFraction = xFractions[index]
                val isCurrent = level.number == currentNumber
                val nodeX = (trackWidth - NodeSize) * xFraction
                Box(Modifier.fillMaxWidth().height(RowHeight)) {
                    PuzzleObject(
                        category = level.category,
                        faded = !level.isUnlocked,
                        modifier = Modifier
                            .offset(x = (trackWidth - ObjectSize) * (if (xFraction < 0.5f) 0.88f else 0.12f), y = NodeCenterY - ObjectSize / 2)
                            .size(ObjectSize),
                    )
                    LevelNode(level, isCurrent, { onOpenLevel(level) }, Modifier.offset(x = nodeX, y = NodeCenterY - NodeSize / 2).size(NodeSize))
                    Column(
                        modifier = Modifier.offset(x = nodeX + NodeSize / 2 - 55.dp, y = NodeCenterY + NodeSize / 2 + 4.dp).width(110.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            level.title,
                            fontSize = 12.sp,
                            color = if (isCurrent) Emerald else if (level.isUnlocked) Ink else InkMuted,
                            fontWeight = if (isCurrent || level.isSolved) FontWeight.SemiBold else FontWeight.Normal,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            modifier = Modifier.background(Ivory.copy(alpha = 0.9f), RoundedCornerShape(8.dp)).padding(horizontal = 6.dp, vertical = 1.dp),
                        )
                        level.bestTimeSeconds?.let {
                            Text("Best %d:%02d".format(it / 60, it % 60), fontSize = 10.sp, color = Emerald)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LevelNode(level: Level, isCurrent: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val pulse by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 1f,
        targetValue = if (isCurrent) 1.28f else 1f,
        animationSpec = infiniteRepeatable(tween(1400), RepeatMode.Reverse),
        label = "pulse",
    )
    Box(modifier.semantics { contentDescription = "Level ${level.number}" }, contentAlignment = Alignment.Center) {
        if (isCurrent) {
            Box(Modifier.fillMaxSize().scale(pulse).background(Amber.copy(alpha = 0.28f), CircleShape))
        }
        Box(
            Modifier
                .fillMaxSize()
                .padding(if (isCurrent) 0.dp else 4.dp)
                .shadow(if (level.isUnlocked) 6.dp else 0.dp, CircleShape, clip = false)
                .clip(CircleShape)
                .background(
                    when {
                        isCurrent -> Brush.verticalGradient(listOf(Emerald, EmeraldDark))
                        level.isSolved -> Brush.verticalGradient(listOf(Mint, SoftMint))
                        else -> Brush.verticalGradient(listOf(Mist, Mist))
                    },
                )
                .border(if (isCurrent) 4.dp else 2.dp, if (isCurrent) Amber else if (level.isSolved) Emerald.copy(alpha = 0.45f) else Line, CircleShape)
                .clickable(enabled = level.isUnlocked, onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            if (!level.isUnlocked) {
                Icon(Icons.Outlined.Lock, "Locked", tint = InkMuted.copy(alpha = 0.7f), modifier = Modifier.size(24.dp))
            } else {
                Text(
                    level.number.toString(),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isCurrent) Color.White else Ink,
                )
            }
        }
        if (level.isSolved) {
            Box(
                Modifier.align(Alignment.TopEnd).offset(x = (-1).dp, y = 1.dp).size(24.dp)
                    .background(Emerald, CircleShape).border(2.dp, Ivory, CircleShape),
                contentAlignment = Alignment.Center,
            ) { Icon(Icons.Outlined.Check, "Solved", tint = Color.White, modifier = Modifier.size(14.dp)) }
        }
    }
}

/** Soft category tile with a small vector pictogram; purely decorative. */
@Composable
private fun PuzzleObject(category: PuzzleCategory, faded: Boolean, modifier: Modifier = Modifier) {
    val (tile, accent) = when (category) {
        PuzzleCategory.Pattern -> SoftAmber to Amber
        PuzzleCategory.Spatial -> Mint to Emerald
        PuzzleCategory.Deduction -> SoftPeach to Peach
        PuzzleCategory.Logic -> SoftMint to EmeraldDark
    }
    Canvas(modifier.alpha(if (faded) 0.45f else 1f)) {
        val w = size.width
        val h = size.height
        drawRoundRect(tile, cornerRadius = CornerRadius(w * 0.3f))
        when (category) {
            PuzzleCategory.Pattern -> {
                drawCircle(accent, w * 0.16f, Offset(w * 0.34f, h * 0.5f))
                drawCircle(accent.copy(alpha = 0.55f), w * 0.16f, Offset(w * 0.66f, h * 0.5f))
            }
            PuzzleCategory.Spatial -> {
                drawRoundRect(accent, Offset(w * 0.28f, h * 0.28f), Size(w * 0.28f, w * 0.28f), CornerRadius(w * 0.05f))
                drawRoundRect(accent.copy(alpha = 0.5f), Offset(w * 0.44f, h * 0.44f), Size(w * 0.28f, w * 0.28f), CornerRadius(w * 0.05f))
            }
            PuzzleCategory.Deduction -> {
                drawCircle(accent, w * 0.17f, Offset(w * 0.46f, h * 0.46f), style = Stroke(w * 0.07f))
                drawLine(accent, Offset(w * 0.58f, h * 0.58f), Offset(w * 0.72f, h * 0.72f), w * 0.07f, StrokeCap.Round)
            }
            PuzzleCategory.Logic -> {
                val diamond = Path().apply {
                    moveTo(w * 0.5f, h * 0.24f); lineTo(w * 0.76f, h * 0.5f); lineTo(w * 0.5f, h * 0.76f); lineTo(w * 0.24f, h * 0.5f); close()
                }
                drawPath(diamond, accent, style = Stroke(w * 0.06f))
            }
        }
    }
}
