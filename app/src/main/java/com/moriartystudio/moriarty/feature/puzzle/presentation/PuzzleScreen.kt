package com.moriartystudio.moriarty.feature.puzzle.presentation

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
import com.moriartystudio.moriarty.ui.theme.Emerald
import com.moriartystudio.moriarty.ui.theme.ErrorCoral
import com.moriartystudio.moriarty.ui.theme.Ink
import com.moriartystudio.moriarty.ui.theme.InkMuted
import com.moriartystudio.moriarty.ui.theme.Ivory
import com.moriartystudio.moriarty.ui.theme.Line
import com.moriartystudio.moriarty.ui.theme.Mint
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
    onFinish: () -> Unit,
) {
    val state by viewModel.collectAsState()
    viewModel.collectSideEffect {
        when (it) {
            PuzzleSideEffect.GoBack -> onBack()
            is PuzzleSideEffect.NextLevel -> onNext(it.level)
            PuzzleSideEffect.FinishJourney -> onFinish()
        }
    }
    val puzzle = state.puzzle ?: return

    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        containerColor = Ivory,
        topBar = {
            TopBar(
                title = "Level ${puzzle.level}",
                subtitle = puzzle.title,
                leading = {
                    IconCircle(Icons.AutoMirrored.Outlined.ArrowBack, viewModel::goBack, "Back")
                },
                trailing = { PuzzleTimer(state.elapsedSeconds, state.status == AnswerStatus.Incorrect) },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PuzzleCard(
                prompt = puzzle.prompt,
                category = puzzle.category.label,
                difficultyRating = puzzle.difficultyRating,
                content = {
                    PuzzleContentRenderer(
                        content = puzzle.content,
                        modifier = Modifier.fillMaxWidth().height(260.dp),
                    )
                },
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Choose an answer", style = MaterialTheme.typography.titleLarge, color = Ink)
                Spacer(Modifier.weight(1f))
                Text("One option fits", style = MaterialTheme.typography.bodyMedium, color = InkMuted)
            }
            puzzle.options.chunked(2).forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    row.forEach { answer ->
                        AnswerOption(
                            text = answer,
                            visual = puzzle.optionVisuals[answer],
                            correct = state.correctAnswer == answer,
                            incorrect = answer in state.wrongOptions,
                            onClick = { viewModel.select(answer) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }
            if (state.status == AnswerStatus.Incorrect) {
                FeedbackCard()
            }
            if (state.showHint) {
                HintCard(puzzle.hint)
            }
            TextButton(
                onClick = viewModel::toggleHint,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                Icon(Icons.Outlined.Lightbulb, null, tint = Amber, modifier = Modifier.size(19.dp))
                Spacer(Modifier.width(7.dp))
                Text(
                    if (state.showHint) "Hide hint" else "Show a hint  +20 sec",
                    color = Emerald,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PuzzleTimer(seconds: Int, penalized: Boolean) {
    Surface(
        shape = RoundedCornerShape(15.dp),
        color = if (penalized) SoftAmber else Paper,
        border = BorderStroke(1.dp, if (penalized) Amber.copy(alpha = 0.5f) else Line),
    ) {
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
private fun PuzzleCard(
    prompt: String,
    category: String,
    difficultyRating: Int,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = Paper,
        border = BorderStroke(1.dp, Line),
        shadowElevation = 2.dp,
    ) {
        Column {
            Box(Modifier.fillMaxWidth().height(5.dp).background(Emerald))
            Column(
                modifier = Modifier.padding(17.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DifficultyPill(category)
                    DifficultyPill(
                        text = "Difficulty $difficultyRating/10",
                        color = SoftAmber,
                        contentColor = Ink,
                    )
                }
                Text(prompt, style = MaterialTheme.typography.headlineMedium, color = Ink)
                content()
            }
        }
    }
}

@Composable
private fun AnswerOption(
    text: String,
    visual: PuzzleAnswerVisual?,
    correct: Boolean,
    incorrect: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor = when {
        incorrect -> ErrorCoral
        correct -> Emerald
        else -> Line
    }
    val containerColor = when {
        incorrect -> SoftPeach
        correct -> Mint
        else -> Paper
    }
    Surface(
        modifier = modifier
            .height(if (visual == null) 72.dp else 96.dp)
            .semantics { contentDescription = "Answer: $text" }
            .clickable(enabled = !correct && !incorrect, onClick = onClick),
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
                    tint = Emerald,
                    modifier = Modifier.align(Alignment.TopEnd).size(23.dp),
                )
            }
        }
    }
}

@Composable
private fun FeedbackCard() {
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
                    "Compare every clue, then choose another card.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkMuted,
                )
            }
            Text("+10 sec", style = MaterialTheme.typography.labelLarge, color = ErrorCoral)
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
