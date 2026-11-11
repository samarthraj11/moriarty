package com.moriartystudio.moriarty.feature.puzzle.presentation

import com.moriartystudio.moriarty.ui.theme.SuccessGreen
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.border
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Replay
import androidx.compose.ui.unit.sp
import kotlin.math.sin
import kotlin.math.cos
import com.moriartystudio.moriarty.ui.theme.categoryColor
import com.moriartystudio.moriarty.ui.theme.Peach
import com.moriartystudio.moriarty.ui.theme.Gold
import com.moriartystudio.moriarty.core.model.PuzzleCategory
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.animation.core.FastOutSlowInEasing
import com.moriartystudio.moriarty.core.designsystem.HeaderPill
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.Animatable
import com.moriartystudio.moriarty.core.designsystem.GlassIconButton
import com.moriartystudio.moriarty.core.designsystem.MoriartyPage
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.moriartystudio.moriarty.core.designsystem.DifficultyPill
import com.moriartystudio.moriarty.core.designsystem.IconCircle
import com.moriartystudio.moriarty.core.designsystem.PuzzleAnswerVisualRenderer
import com.moriartystudio.moriarty.core.designsystem.PuzzleContentRenderer
import com.moriartystudio.moriarty.core.designsystem.TopBar
import com.moriartystudio.moriarty.core.model.PuzzleAnswerVisual
import com.moriartystudio.moriarty.ui.theme.Amber
import com.moriartystudio.moriarty.ui.theme.Indigo
import com.moriartystudio.moriarty.ui.theme.ErrorCoral
import com.moriartystudio.moriarty.ui.theme.Ink
import com.moriartystudio.moriarty.ui.theme.InkMuted
import com.moriartystudio.moriarty.ui.theme.Ivory
import com.moriartystudio.moriarty.ui.theme.Line
import com.moriartystudio.moriarty.ui.theme.Frost
import com.moriartystudio.moriarty.ui.theme.Paper
import com.moriartystudio.moriarty.ui.theme.SoftAmber
import com.moriartystudio.moriarty.ui.theme.SoftPeach
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun PuzzleScreen(
    viewModel: PuzzleViewModel,
    onBack: () -> Unit,
    onNext: (Int) -> Unit,
    onNextSection: (PuzzleCategory) -> Unit,
    onFinish: () -> Unit,
) {
    val state by viewModel.collectAsState()
    viewModel.collectSideEffect {
        when (it) {
            PuzzleSideEffect.GoBack -> onBack()
            is PuzzleSideEffect.NextLevel -> onNext(it.level)
            is PuzzleSideEffect.NextSection -> onNextSection(it.category)
            PuzzleSideEffect.FinishJourney -> onFinish()
        }
    }
    val puzzle = state.puzzle ?: return

    MoriartyPage(
        title = "Level ${state.categoryLevel}",
        subtitle = "${puzzle.category.label}: ${puzzle.title}",
        leading = { GlassIconButton(Icons.AutoMirrored.Outlined.ArrowBack, viewModel::goBack, "Back") },
        bottomBar = { if (state.status == AnswerStatus.Correct) SolvedBar(onRetry = viewModel::retry, onNext = viewModel::next) },
        trailing = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                HeaderPill {
                    Text("\uD83C\uDFC6", fontSize = 15.sp)
                    Spacer(Modifier.width(4.dp))
                    Text(state.totalPoints.toString(), style = MaterialTheme.typography.labelLarge, color = Ink)
                }
                Spacer(Modifier.width(8.dp))
                PuzzleTimer(state.elapsedSeconds)
            }
        },
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 22.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(puzzle.prompt, style = MaterialTheme.typography.headlineMedium, color = Ink)
            PuzzleContentRenderer(
                content = puzzle.content,
                modifier = Modifier.fillMaxWidth().height(260.dp),
            )
            Spacer(Modifier.height(2.dp))
            puzzle.options.chunked(2).forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    row.forEach { answer ->
                        AnswerOption(
                            text = answer,
                            visual = puzzle.optionVisuals[answer],
                            correct = state.correctAnswer == answer,
                            accent = categoryColor(puzzle.category),
                            incorrect = answer in state.wrongOptions,
                            onClick = { viewModel.select(answer) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }
            if (state.status == AnswerStatus.Incorrect) {
                FeedbackCard(state.lastDelta ?: 0)
            }
            if (state.status == AnswerStatus.Correct) {
                CorrectCard(state.lastDelta ?: 0)
            }
            if (state.showHint) {
                HintCard(puzzle.hint)
            }
            if (state.status != AnswerStatus.Correct) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = viewModel::toggleHint) {
                        Icon(Icons.Outlined.Lightbulb, null, tint = Amber, modifier = Modifier.size(19.dp))
                        Spacer(Modifier.width(7.dp))
                        Text(
                            if (state.showHint) "Hide hint" else "Show a hint",
                            color = Indigo,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    TextButton(onClick = viewModel::skip) {
                        Text("Skip", color = InkMuted, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.width(4.dp))
                        Icon(Icons.Outlined.SkipNext, null, tint = InkMuted, modifier = Modifier.size(20.dp))
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PuzzleTimer(seconds: Int) {
    Surface(shape = RoundedCornerShape(50), color = Color.White) {
        Row(
            modifier = Modifier.padding(horizontal = 11.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Outlined.Timer, null, tint = InkMuted, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text(
                "%02d:%02d".format(seconds / 60, seconds % 60),
                style = MaterialTheme.typography.titleMedium,
                color = Ink,
            )
        }
    }
}

@Composable
private fun AnswerOption(
    text: String,
    visual: PuzzleAnswerVisual?,
    correct: Boolean,
    accent: Color,
    incorrect: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor = when {
        incorrect -> ErrorCoral
        correct -> Indigo
        else -> Line
    }
    val containerColor = when {
        incorrect -> SoftPeach
        correct -> Frost
        else -> Paper
    }
    // Motion only answers the tap: squish while pressed, shake when wrong, pop when right.
    val shake = remember { Animatable(0f) }
    val pop = remember { Animatable(1f) }
    LaunchedEffect(incorrect) {
        if (incorrect) {
            shake.animateTo(0f, keyframes { durationMillis = 380; -12f at 60; 12f at 130; -8f at 200; 8f at 260; -3f at 320 })
        }
    }
    LaunchedEffect(correct) {
        if (correct) {
            pop.animateTo(1.07f, tween(110))
            pop.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }
    }
    // A burst of confetti in the section's colours when the answer is right.
    val burst = remember { Animatable(0f) }
    LaunchedEffect(correct) {
        burst.snapTo(0f)
        if (correct) burst.animateTo(1f, tween(1900, easing = FastOutSlowInEasing))
    }
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val pressScale by animateFloatAsState(if (pressed) 0.95f else 1f, label = "press")
    Surface(
        modifier = modifier
            .height(if (visual == null) 72.dp else 96.dp)
            .graphicsLayer {
                translationX = shake.value * density
                scaleX = pressScale * pop.value
                scaleY = pressScale * pop.value
            }
            .drawWithContent {
                drawContent()
                if (burst.value > 0f && burst.value < 1f) drawSuccessBurst(burst.value, listOf(accent, Gold, Indigo, Peach))
            }
            .semantics { contentDescription = "Answer: $text" }
            .clickable(interactionSource = interaction, indication = null, enabled = !correct && !incorrect, onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = containerColor,
        border = BorderStroke(if (correct || incorrect) 1.5.dp else 1.dp, borderColor),
    ) {
        Box(modifier = Modifier.padding(10.dp), contentAlignment = Alignment.Center) {
            if (visual == null) {
                Text(
                    text,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (incorrect) ErrorCoral else Ink,
                )
            } else {
                val visualModifier = when (visual) {
                    is PuzzleAnswerVisual.Token -> Modifier.size(50.dp)
                    is PuzzleAnswerVisual.TokenGroup -> Modifier.width(112.dp).height(48.dp)
                }
                PuzzleAnswerVisualRenderer(visual, visualModifier)
            }
            if (correct) {
                Icon(
                    Icons.Filled.CheckCircle,
                    "Correct",
                    tint = Indigo,
                    modifier = Modifier.align(Alignment.TopEnd).size(23.dp),
                )
            }
        }
    }
}

@Composable
private fun FeedbackCard(delta: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = SoftPeach,
        border = BorderStroke(1.dp, ErrorCoral.copy(alpha = 0.25f)),
    ) {
        Row(Modifier.padding(15.dp), verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f)) {
                Text("That rule does not fit.", style = MaterialTheme.typography.titleMedium, color = ErrorCoral)
                Text(
                    if (delta < 0) "Compare every clue, then choose another card." else "No more points on this puzzle. Keep going to solve it.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkMuted,
                )
            }
            if (delta < 0) Text("\u2212${-delta} pts", style = MaterialTheme.typography.labelLarge, color = ErrorCoral)
        }
    }
}

@Composable
private fun CorrectCard(delta: Int) {
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), color = Frost) {
        Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                if (delta > 0) "Right first time." else "Solved, but no points this time.",
                style = MaterialTheme.typography.titleMedium,
                color = Indigo,
                modifier = Modifier.weight(1f),
            )
            if (delta > 0) Text("+$delta pts", style = MaterialTheme.typography.labelLarge, color = Indigo)
        }
    }
}

@Composable
private fun HintCard(hint: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = SoftAmber,
    ) {
        Row(Modifier.padding(15.dp), verticalAlignment = Alignment.Top) {
            Icon(Icons.Outlined.Lightbulb, null, tint = Color(0xFF9B6517), modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(10.dp))
            Column {
                Text("A different angle", style = MaterialTheme.typography.labelLarge, color = Ink)
                Text(hint, style = MaterialTheme.typography.bodyMedium, color = Ink)
            }
        }
    }
}

/** Confetti radiating from the centre of the tile, easing out and fading as [progress] runs 0 to 1. */
internal fun DrawScope.drawSuccessBurst(progress: Float, colors: List<Color>) {
    val eased = 1f - (1f - progress) * (1f - progress)
    val fade = (1f - progress * progress).coerceIn(0f, 1f)
    val count = 18
    repeat(count) { i ->
        val angle = Math.toRadians(i * 360.0 / count + (i % 3) * 8.0)
        val distance = (62 + (i * 37) % 52).dp.toPx() * eased
        val position = Offset(center.x + (cos(angle) * distance).toFloat(), center.y + (sin(angle) * distance).toFloat())
        val color = colors[i % colors.size].copy(alpha = fade)
        val size = (5 + (i % 3) * 2).dp.toPx()
        if (i % 2 == 0) {
            drawCircle(color, size / 2, position)
        } else {
            rotate(progress * 220f + i * 20f, pivot = position) {
                drawRoundRect(color, Offset(position.x - size / 2, position.y - size), Size(size, size * 2), CornerRadius(size / 3))
            }
        }
    }
}

/** Docked once a puzzle is solved: play it again, or carry on. Nothing moves until a button is tapped. */
@Composable
internal fun SolvedBar(onRetry: () -> Unit, onNext: () -> Unit) {
    Surface(color = Color.White, shadowElevation = 12.dp, shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)) {
        Row(
            Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                Modifier
                    .weight(1f)
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .border(2.dp, Indigo, RoundedCornerShape(28.dp))
                    .clickable(onClick = onRetry),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Icon(Icons.Outlined.Replay, null, tint = Indigo, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(8.dp))
                Text("Retry", color = Indigo, style = MaterialTheme.typography.titleMedium)
            }
            Row(
                Modifier
                    .weight(1f)
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(SuccessGreen)
                    .clickable(onClick = onNext),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text("Next", color = Color.White, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Outlined.ArrowForward, null, tint = Color.White, modifier = Modifier.size(22.dp))
            }
        }
    }
}
