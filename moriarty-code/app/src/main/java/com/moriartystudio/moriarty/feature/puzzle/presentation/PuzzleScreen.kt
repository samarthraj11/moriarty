package com.moriartystudio.moriarty.feature.puzzle.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.moriartystudio.moriarty.core.designsystem.*
import com.moriartystudio.moriarty.ui.theme.*
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun PuzzleScreen(viewModel: PuzzleViewModel, onBack: () -> Unit, onSolved: (Int, Int) -> Unit) {
    val state by viewModel.collectAsState()
    viewModel.collectSideEffect { when (it) { PuzzleSideEffect.GoBack -> onBack(); is PuzzleSideEffect.OpenSolution -> onSolved(it.level, it.seconds) } }
    val puzzle = state.puzzle ?: return
    Scaffold(modifier = Modifier.statusBarsPadding(), containerColor = Ivory, topBar = { TopBar("Level ${puzzle.level}", null, leading = { IconCircle(Icons.AutoMirrored.Outlined.ArrowBack, viewModel::goBack, "Back") }, trailing = { Row(Modifier.background(if (state.status == AnswerStatus.Incorrect) SoftAmber else Mint, RoundedCornerShape(50)).padding(horizontal = 14.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Outlined.Timer, null, tint = Ink, modifier = Modifier.size(20.dp)); Spacer(Modifier.width(7.dp)); Text("%02d:%02d".format(state.elapsedSeconds / 60, state.elapsedSeconds % 60), style = MaterialTheme.typography.titleMedium) } }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 22.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DifficultyPill(puzzle.category.label)
                DifficultyPill(puzzle.difficulty.name, if (puzzle.difficulty.name == "Hard") SoftPeach else if (puzzle.difficulty.name == "Medium") SoftAmber else Mint, Ink)
            }
            PuzzleContentRenderer(puzzle.content, Modifier.fillMaxWidth().height(280.dp))
            Text(puzzle.prompt, style = MaterialTheme.typography.headlineLarge)
            puzzle.options.chunked(2).forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEach { answer -> AnswerOption(answer, state.selectedAnswer == answer, state.status == AnswerStatus.Incorrect && state.selectedAnswer == answer, { viewModel.select(answer) }, Modifier.weight(1f)) }
                }
            }
            if (state.status == AnswerStatus.Incorrect) {
                Row(Modifier.fillMaxWidth().background(SoftPeach, RoundedCornerShape(18.dp)).padding(15.dp), verticalAlignment = Alignment.Top) {
                    Text("Not quite", style = MaterialTheme.typography.titleMedium, color = ErrorCoral, modifier = Modifier.weight(1f)); Text("+10 sec", style = MaterialTheme.typography.labelLarge, color = ErrorCoral)
                }
                Text("Look again at which side of the clock each hand appears on.", style = MaterialTheme.typography.bodyMedium, color = InkMuted)
            }
            if (state.showHint) Text(puzzle.hint, modifier = Modifier.fillMaxWidth().background(SoftAmber, RoundedCornerShape(18.dp)).padding(16.dp), style = MaterialTheme.typography.bodyMedium, color = Ink)
            MoriartyPrimaryButton("Check answer", viewModel::check, enabled = state.selectedAnswer != null)
            TextButton(onClick = viewModel::toggleHint, modifier = Modifier.align(Alignment.CenterHorizontally)) { Icon(Icons.Outlined.Lightbulb, null, tint = Amber); Spacer(Modifier.width(6.dp)); Text(if (state.showHint) "Hide hint" else "Need a hint?", color = Emerald, fontWeight = FontWeight.SemiBold) }
            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
private fun AnswerOption(text: String, selected: Boolean, incorrect: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val border = when { incorrect -> ErrorCoral; selected -> Emerald; else -> Line }
    val background = when { incorrect -> SoftPeach; selected -> Mint; else -> Paper }
    Surface(modifier = modifier.height(62.dp).clickable(onClick = onClick), shape = RoundedCornerShape(18.dp), color = background, border = BorderStroke(if (selected || incorrect) 2.dp else 1.dp, border)) {
        Box(contentAlignment = Alignment.Center) { Text(text, style = MaterialTheme.typography.titleMedium, color = if (incorrect) ErrorCoral else Ink) }
    }
}
