package com.moriartystudio.moriarty.feature.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.moriartystudio.moriarty.app.navigation.RootDestination
import com.moriartystudio.moriarty.core.designsystem.*
import com.moriartystudio.moriarty.ui.theme.*
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun HomeScreen(viewModel: HomeViewModel, onOpenPuzzle: (Int) -> Unit, onOpenAllLevels: () -> Unit, onRoot: (RootDestination) -> Unit, onProfile: () -> Unit) {
    val state by viewModel.collectAsState()
    viewModel.collectSideEffect { effect -> when (effect) {
        is HomeSideEffect.OpenPuzzle -> onOpenPuzzle(effect.level)
        HomeSideEffect.OpenAllLevels -> onOpenAllLevels()
        HomeSideEffect.OpenDaily -> onRoot(RootDestination.Daily)
        HomeSideEffect.OpenProfile -> onProfile()
    } }
    HomeContent(state, viewModel::continuePuzzle, viewModel::openAllLevels, viewModel::openDaily, viewModel::openProfile, onRoot)
}

@Composable
internal fun HomeContent(state: HomeState, onContinue: () -> Unit, onAllLevels: () -> Unit, onDaily: () -> Unit, onProfile: () -> Unit, onRoot: (RootDestination) -> Unit) {
    Scaffold(modifier = Modifier.statusBarsPadding(), containerColor = Ivory, bottomBar = { MoriartyBottomBar(RootDestination.Levels, onRoot) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 22.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(Modifier.fillMaxWidth().padding(top = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) { Text("Moriarty", style = MaterialTheme.typography.headlineLarge); Text("See what others miss.", style = MaterialTheme.typography.bodyMedium, color = InkMuted) }
                Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Outlined.LocalFireDepartment, null, tint = Color(0xFFFF9818)); Text("${state.streak} day streak", color = InkMuted, style = MaterialTheme.typography.labelLarge) }
                Spacer(Modifier.width(10.dp)); IconCircle(Icons.Outlined.PersonOutline, onProfile, "Profile")
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Level ${state.currentLevel?.number ?: 7}", style = MaterialTheme.typography.headlineLarge)
                DifficultyPill(state.currentLevel?.difficulty?.name ?: "Easy")
                GeometricMirrorHero(Modifier.fillMaxWidth().height(260.dp))
                Text("${state.solvedCount} of ${state.totalLevels} solved", style = MaterialTheme.typography.labelLarge, color = InkMuted)
                LinearProgressIndicator(progress = { state.solvedCount / state.totalLevels.toFloat() }, modifier = Modifier.fillMaxWidth().height(7.dp).clip(CircleShape), color = Color(0xFFFFA918), trackColor = Line)
                MoriartyPrimaryButton("Continue", onContinue, trailing = { Icon(Icons.AutoMirrored.Outlined.ArrowForward, null) })
            }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionTitle("Your path", "All levels", onAllLevels)
                Box(Modifier.fillMaxWidth()) {
                    Box(Modifier.fillMaxWidth().padding(horizontal = 22.dp).offset(y = 22.dp).height(3.dp).background(Line, CircleShape))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        state.nearbyLevels.forEach { level ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(Modifier.size(44.dp).background(if (level.number == 7) Emerald else if (level.isSolved) Mint else Color(0xFFE9EBE8), CircleShape), contentAlignment = Alignment.Center) {
                                    Text(level.number.toString(), color = if (level.number == 7) Color.White else if (level.isSolved) Emerald else InkMuted, fontWeight = FontWeight.Bold)
                                    if (level.isSolved) Icon(Icons.Outlined.Check, null, tint = Emerald, modifier = Modifier.align(Alignment.TopEnd).size(15.dp).background(Mint, CircleShape))
                                }
                                if (!level.isUnlocked) Icon(Icons.Outlined.Lock, null, tint = InkMuted, modifier = Modifier.size(16.dp).padding(top = 3.dp)) else Spacer(Modifier.height(16.dp))
                            }
                        }
                    }
                }
            }
            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp)).background(Paper).clickable(onClick = onDaily).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                SmallGeometryThumbnail(Modifier.size(56.dp)); Spacer(Modifier.width(14.dp)); Column(Modifier.weight(1f)) { Text("Daily challenge", style = MaterialTheme.typography.titleMedium); Text("One puzzle. One attempt.", style = MaterialTheme.typography.bodyMedium, color = InkMuted) }; Column(horizontalAlignment = Alignment.End) { Text("Resets in", style = MaterialTheme.typography.labelMedium, color = InkMuted); Text(state.dailyReset, style = MaterialTheme.typography.titleMedium) }; Icon(Icons.AutoMirrored.Outlined.ArrowForward, null, tint = Emerald, modifier = Modifier.padding(start = 6.dp))
            }
            Spacer(Modifier.height(6.dp))
        }
    }
}
