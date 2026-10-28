package com.moriartystudio.moriarty.feature.solution.presentation

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.moriartystudio.moriarty.core.designsystem.*
import com.moriartystudio.moriarty.ui.theme.*
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun SolutionScreen(viewModel: SolutionViewModel, onNext: (Int) -> Unit, onComplete: () -> Unit, onLeaderboard: () -> Unit) {
    val state by viewModel.collectAsState(); val context = LocalContext.current
    viewModel.collectSideEffect { effect -> when (effect) { is SolutionSideEffect.NextLevel -> onNext(effect.level); SolutionSideEffect.FinishJourney -> onComplete(); SolutionSideEffect.OpenLeaderboard -> onLeaderboard(); SolutionSideEffect.Share -> context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply { type = "text/plain"; putExtra(Intent.EXTRA_TEXT, "I solved Moriarty level ${state.level} in ${state.seconds} seconds. Can you spot the answer?") }, "Share result")) } }
    val puzzle = state.puzzle ?: return
    Scaffold(modifier = Modifier.statusBarsPadding(), containerColor = Ivory) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 22.dp, vertical = 24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(52.dp).background(Emerald, CircleShape), contentAlignment = Alignment.Center) { Icon(Icons.Outlined.Check, null, tint = Color.White, modifier = Modifier.size(30.dp)) }; Spacer(Modifier.width(12.dp)); Text("Solved", style = MaterialTheme.typography.headlineMedium, color = Emerald) }
            Text(puzzle.title, style = MaterialTheme.typography.headlineLarge, modifier = Modifier.fillMaxWidth())
            PuzzleContentRenderer(puzzle.content, Modifier.fillMaxWidth().height(220.dp), compact = true)
            Column(Modifier.fillMaxWidth().background(Mint, RoundedCornerShape(18.dp)).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Correct answer", color = InkMuted, style = MaterialTheme.typography.labelLarge)
                Text(puzzle.correctOption, style = MaterialTheme.typography.headlineMedium, color = Emerald)
            }
            Text("Why it works", style = MaterialTheme.typography.titleLarge, modifier = Modifier.fillMaxWidth())
            Text(puzzle.explanation, style = MaterialTheme.typography.bodyLarge, color = InkMuted, modifier = Modifier.fillMaxWidth())
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) { MetricCard("Level", state.level.toString(), Modifier.weight(1f)); MetricCard("Ranked time", "%02d:%02d".format(state.seconds / 60, state.seconds % 60), Modifier.weight(1f)) }
            MoriartyPrimaryButton(if (state.hasNextLevel) "Next level" else "Back to levels", viewModel::next, trailing = { Icon(Icons.AutoMirrored.Outlined.ArrowForward, null) })
            MoriartySecondaryButton("View leaderboard", viewModel::leaderboard)
            TextButton(onClick = viewModel::share) { Icon(Icons.Outlined.Share, null, tint = Emerald); Spacer(Modifier.width(7.dp)); Text("Share result", color = Emerald) }
        }
    }
}

@Composable
private fun MetricCard(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier.background(Paper, RoundedCornerShape(20.dp)).padding(16.dp)) { Text(label, color = InkMuted, style = MaterialTheme.typography.bodyMedium); Text(value, style = MaterialTheme.typography.titleLarge) }
}
