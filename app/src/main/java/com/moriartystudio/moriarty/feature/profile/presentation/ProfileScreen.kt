package com.moriartystudio.moriarty.feature.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.moriartystudio.moriarty.app.navigation.RootDestination
import com.moriartystudio.moriarty.core.designsystem.*
import com.moriartystudio.moriarty.ui.theme.*
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun ProfileScreen(viewModel: ProfileViewModel, onSettings: () -> Unit, onRoot: (RootDestination) -> Unit) {
    val state by viewModel.collectAsState(); viewModel.collectSideEffect { onSettings() }
    val profile = state.profile ?: return
    Scaffold(modifier = Modifier.statusBarsPadding(), containerColor = Ivory, bottomBar = { MoriartyBottomBar(RootDestination.Profile, onRoot) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 22.dp, vertical = 14.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Text("Profile", style = MaterialTheme.typography.headlineLarge)
            Row(verticalAlignment = Alignment.CenterVertically) { SmallGeometryThumbnail(Modifier.size(92.dp)); Spacer(Modifier.width(18.dp)); Column { Text(profile.name, style = MaterialTheme.typography.headlineMedium); Text(profile.handle, color = InkMuted, style = MaterialTheme.typography.bodyLarge); Text("Playing anonymously", color = InkMuted, style = MaterialTheme.typography.bodyMedium) } }
            HorizontalDivider(color = Line)
            Row(verticalAlignment = Alignment.Top) { Box(Modifier.size(46.dp).background(Mint, CircleShape), contentAlignment = Alignment.Center) { Icon(Icons.Outlined.Cloud, null, tint = Emerald) }; Spacer(Modifier.width(14.dp)); Column(Modifier.weight(1f)) { Text("Account", style = MaterialTheme.typography.titleLarge); Text("Keep your progress across devices.", color = InkMuted); Spacer(Modifier.height(10.dp)); MoriartySecondaryButton("Continue with Google", {}) } }
            HorizontalDivider(color = Line)
            SectionTitle("Your stats")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Metric(profile.solved.toString(), "puzzles solved"); Metric(profile.streak.toString(), "day streak"); Metric("${profile.accuracy}%", "accuracy"); Metric(if (profile.averageSeconds == 0) "—" else "%02d:%02d".format(profile.averageSeconds / 60, profile.averageSeconds % 60), "average time") }
            HorizontalDivider(color = Line)
            SectionTitle("Your reasoning activity")
            listOf("Patterns", "Spatial", "Deduction", "Logic").forEachIndexed { index, label -> Row(verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(34.dp).background(listOf(Mint, SoftPeach, SoftAmber, Mint)[index], CircleShape)); Spacer(Modifier.width(12.dp)); Text(label, modifier = Modifier.width(104.dp)); LinearProgressIndicator(progress = { if (profile.solved == 0) 0f else (profile.solved / 10f).coerceIn(0f, 1f) }, modifier = Modifier.weight(1f).height(9.dp), color = listOf(Emerald, Peach, Amber, Color(0xFF86CDB0))[index], trackColor = Line) } }
            HorizontalDivider(color = Line)
            SectionTitle("Achievements", "See all", {})
            if (state.recentAchievements.isEmpty()) {
                Text("Solve your first puzzle to earn First insight.", color = InkMuted, style = MaterialTheme.typography.bodyMedium)
            } else {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) { state.recentAchievements.forEachIndexed { index, label -> Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(96.dp)) { Box(Modifier.size(48.dp).background(listOf(Emerald, Peach, Amber)[index % 3], CircleShape)); Spacer(Modifier.height(7.dp)); Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold) } } }
            }
            HorizontalDivider(color = Line)
            Row(Modifier.fillMaxWidth().clickable(onClick = viewModel::settings).padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Outlined.Settings, null, tint = Emerald); Spacer(Modifier.width(12.dp)); Text("Settings", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f)); Icon(Icons.Outlined.ChevronRight, null, tint = InkMuted) }
        }
    }
}
