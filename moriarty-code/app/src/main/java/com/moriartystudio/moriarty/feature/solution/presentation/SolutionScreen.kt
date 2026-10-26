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
fun SolutionScreen(viewModel: SolutionViewModel, onNext: (Int) -> Unit, onLeaderboard: () -> Unit) {
    val state by viewModel.collectAsState(); val context = LocalContext.current
    viewModel.collectSideEffect { effect -> when (effect) { is SolutionSideEffect.NextLevel -> onNext(effect.level); SolutionSideEffect.OpenLeaderboard -> onLeaderboard(); SolutionSideEffect.Share -> context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply { type = "text/plain"; putExtra(Intent.EXTRA_TEXT, "I solved Moriarty level ${state.level} in ${state.seconds} seconds. Can you spot the answer?") }, "Share result")) } }
    Scaffold(modifier = Modifier.statusBarsPadding(), containerColor = Ivory) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 22.dp, vertical = 24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(52.dp).background(Emerald, CircleShape), contentAlignment = Alignment.Center) { Icon(Icons.Outlined.Check, null, tint = Color.White, modifier = Modifier.size(30.dp)) }; Spacer(Modifier.width(12.dp)); Text("Solved", style = MaterialTheme.typography.headlineMedium, color = Emerald) }
            Text("Through the Looking Glass", style = MaterialTheme.typography.headlineLarge, modifier = Modifier.fillMaxWidth())
            MirrorClockIllustration(Modifier.fillMaxWidth().height(280.dp))
            Text("12:00 − 08:20 = 03:40", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.fillMaxWidth().background(Paper, RoundedCornerShape(18.dp)).padding(16.dp), textAlign = TextAlign.Center)
            Text("The mirror reverses the hand positions across the vertical axis. The actual time is 03:40.", style = MaterialTheme.typography.bodyLarge, color = InkMuted, textAlign = TextAlign.Center)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) { MetricCard("Solving time", "01:34", Modifier.weight(1f)); MetricCard("Wrong answers", "1", Modifier.weight(1f)) }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) { MetricCard("Penalty", "+10 sec", Modifier.weight(1f)); MetricCard("Ranked time", "01:44", Modifier.weight(1f)) }
            MetricCard("Global rank", "#428", Modifier.fillMaxWidth())
            MoriartyPrimaryButton("Next level", viewModel::next, trailing = { Icon(Icons.AutoMirrored.Outlined.ArrowForward, null) })
            MoriartySecondaryButton("View leaderboard", viewModel::leaderboard)
            TextButton(onClick = viewModel::share) { Icon(Icons.Outlined.Share, null, tint = Emerald); Spacer(Modifier.width(7.dp)); Text("Share result", color = Emerald) }
        }
    }
}

@Composable
private fun MetricCard(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier.background(Paper, RoundedCornerShape(20.dp)).padding(16.dp)) { Text(label, color = InkMuted, style = MaterialTheme.typography.bodyMedium); Text(value, style = MaterialTheme.typography.titleLarge) }
}
