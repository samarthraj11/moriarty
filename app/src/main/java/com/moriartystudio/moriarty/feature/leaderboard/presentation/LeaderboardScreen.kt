package com.moriartystudio.moriarty.feature.leaderboard.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.moriartystudio.moriarty.app.navigation.RootDestination
import com.moriartystudio.moriarty.core.designsystem.MoriartyBottomBar
import com.moriartystudio.moriarty.core.designsystem.TopBar
import com.moriartystudio.moriarty.ui.theme.*
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun LeaderboardScreen(viewModel: LeaderboardViewModel, onRoot: (RootDestination) -> Unit) {
    val state by viewModel.collectAsState(); viewModel.collectSideEffect { }
    Scaffold(modifier = Modifier.statusBarsPadding(), containerColor = Ivory, bottomBar = { MoriartyBottomBar(RootDestination.Leaderboard, onRoot) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            TopBar("Leaderboard")
            Text("Level 1⌄", modifier = Modifier.padding(horizontal = 22.dp).background(Mint, RoundedCornerShape(18.dp)).padding(horizontal = 22.dp, vertical = 12.dp), style = MaterialTheme.typography.titleMedium)
            Text(state.puzzleTitle, modifier = Modifier.padding(horizontal = 22.dp, vertical = 18.dp), style = MaterialTheme.typography.headlineMedium)
            Row(Modifier.fillMaxWidth().padding(horizontal = 22.dp).background(Paper, RoundedCornerShape(18.dp)).padding(4.dp)) {
                LeaderboardPeriod.entries.forEach { period -> val selected = period == state.selectedPeriod; Text(when (period) { LeaderboardPeriod.Today -> "Global"; LeaderboardPeriod.Week -> "Country"; LeaderboardPeriod.AllTime -> "Friends" }, modifier = Modifier.weight(1f).background(if (selected) Emerald else Color.Transparent, RoundedCornerShape(14.dp)).clickable { viewModel.selectPeriod(period) }.padding(vertical = 11.dp), color = if (selected) Color.White else InkMuted, textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontWeight = FontWeight.SemiBold) }
            }
            LazyColumn(contentPadding = PaddingValues(horizontal = 22.dp, vertical = 4.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.entries.filterNot { it.isCurrentUser }, key = { it.rank }) { entry ->
                    Row(Modifier.fillMaxWidth().background(if (entry.isCurrentUser) Mint else Paper, RoundedCornerShape(18.dp)).clickable { viewModel.openPlayer(entry.name) }.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("${entry.rank}", style = MaterialTheme.typography.titleMedium, color = InkMuted, modifier = Modifier.width(34.dp)); Box(Modifier.size(42.dp).background(listOf(Mint, SoftPeach, SoftAmber)[entry.rank % 3], CircleShape), contentAlignment = Alignment.Center) { Text(entry.name.take(1), color = Ink, fontWeight = FontWeight.Bold) }; Spacer(Modifier.width(12.dp)); Text(entry.name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f)); Text("%02d:%02d".format(entry.totalSeconds / 60, entry.totalSeconds % 60), style = MaterialTheme.typography.titleMedium)
                    }
                }
                item { Text("•••", modifier = Modifier.fillMaxWidth().padding(10.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center, color = InkMuted) }
                item { state.entries.firstOrNull { it.isCurrentUser }?.let { entry -> Row(Modifier.fillMaxWidth().background(Mint, RoundedCornerShape(18.dp)).padding(14.dp), verticalAlignment = Alignment.CenterVertically) { Text("${entry.rank}", modifier = Modifier.width(45.dp)); Box(Modifier.size(42.dp).background(Emerald, CircleShape), contentAlignment = Alignment.Center) { Text("Y", color = Color.White) }; Spacer(Modifier.width(12.dp)); Text("You", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f)); Text("01:44", style = MaterialTheme.typography.titleMedium) } } }
                item { Text("Ranked time includes hint and wrong-answer penalties.", modifier = Modifier.fillMaxWidth().padding(12.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center, color = InkMuted, style = MaterialTheme.typography.bodyMedium) }
            }
        }
    }
}
