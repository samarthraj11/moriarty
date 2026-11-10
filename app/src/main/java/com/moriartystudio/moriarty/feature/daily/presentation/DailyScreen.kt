package com.moriartystudio.moriarty.feature.daily.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.LocalFireDepartment
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
fun DailyScreen(viewModel: DailyViewModel, onStart: () -> Unit, onRoot: (RootDestination) -> Unit) {
    val state by viewModel.collectAsState(); viewModel.collectSideEffect { onStart() }
    val puzzle = state.puzzle ?: return
    MoriartyPage(
        title = "Daily challenge",
        subtitle = state.dateLabel,
        bottomBar = { MoriartyBottomBar(RootDestination.Daily, onRoot) },
        trailing = { HeaderPill { Icon(Icons.Outlined.LocalFireDepartment, null, tint = Color(0xFFE77C22), modifier = Modifier.size(17.dp)); Spacer(Modifier.width(4.dp)); Text(if (state.streak == 0) "New streak" else "${state.streak} days", style = MaterialTheme.typography.labelLarge, color = Ink) } },
    ) {
        Column(Modifier.padding(horizontal = 22.dp, vertical = 22.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            PuzzleContentRenderer(puzzle.content, Modifier.fillMaxWidth().height(240.dp))
            Text(puzzle.title, style = MaterialTheme.typography.headlineMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { DifficultyPill(puzzle.difficulty.name); DifficultyPill(puzzle.category.label, SoftPeach, Ink); DifficultyPill("One attempt", Mist, Ink) }
            Text("Only your first answer scores.", style = MaterialTheme.typography.bodyLarge, color = InkMuted)
            Column(Modifier.fillMaxWidth().background(Paper, RoundedCornerShape(22.dp)).padding(horizontal = 16.dp)) {
                PenaltyRow(Icons.Outlined.Check, "Right first time", "+10 pts", Frost, Indigo)
                HorizontalDivider(color = Line)
                PenaltyRow(Icons.Outlined.Close, "Wrong first answer", "\u22125 pts", SoftPeach, ErrorCoral)
                HorizontalDivider(color = Line)
                PenaltyRow(Icons.Outlined.Groups, "Any answer after that", "0 pts", Frost, Indigo)
            }
            MoriartyPrimaryButton("Start challenge", viewModel::start, trailing = { Icon(Icons.AutoMirrored.Outlined.ArrowForward, null) })
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                listOf("Mon" to "✓", "Tue" to "✓", "Wed" to "23", "Thu" to "×", "Fri" to "", "Sat" to "", "Sun" to "").forEach { (day, value) -> Column(horizontalAlignment = Alignment.CenterHorizontally) { Text(day, style = MaterialTheme.typography.labelMedium, color = InkMuted); Spacer(Modifier.height(6.dp)); Box(Modifier.size(38.dp).background(if (day == "Wed") Indigo else if (value.isNotEmpty()) Frost else Color.Transparent, CircleShape), contentAlignment = Alignment.Center) { Text(value, color = if (day == "Wed") Color.White else Indigo, fontWeight = FontWeight.Bold) } } }
            }
        }
    }
}

@Composable
private fun PenaltyRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, value: String, iconBackground: Color, iconTint: Color) {
    Row(Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(38.dp).background(iconBackground, CircleShape), contentAlignment = Alignment.Center) { Icon(icon, null, tint = iconTint) }; Spacer(Modifier.width(12.dp)); Text(title, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge); Text(value, color = InkMuted, style = MaterialTheme.typography.bodyLarge) }
}
