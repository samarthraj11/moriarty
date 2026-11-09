package com.moriartystudio.moriarty.feature.home.presentation

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.moriartystudio.moriarty.app.navigation.RootDestination
import com.moriartystudio.moriarty.core.designsystem.DifficultyPill
import com.moriartystudio.moriarty.core.designsystem.IconCircle
import com.moriartystudio.moriarty.core.designsystem.MoriartyBottomBar
import com.moriartystudio.moriarty.core.designsystem.MoriartyPrimaryButton
import com.moriartystudio.moriarty.core.designsystem.PuzzleContentRenderer
import com.moriartystudio.moriarty.core.designsystem.SectionTitle
import com.moriartystudio.moriarty.core.designsystem.SmallGeometryThumbnail
import com.moriartystudio.moriarty.core.model.Level
import com.moriartystudio.moriarty.ui.theme.Amber
import com.moriartystudio.moriarty.ui.theme.Emerald
import com.moriartystudio.moriarty.ui.theme.Ink
import com.moriartystudio.moriarty.ui.theme.InkMuted
import com.moriartystudio.moriarty.ui.theme.Ivory
import com.moriartystudio.moriarty.ui.theme.Line
import com.moriartystudio.moriarty.ui.theme.Mint
import com.moriartystudio.moriarty.ui.theme.Paper
import com.moriartystudio.moriarty.ui.theme.SoftAmber
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onOpenPuzzle: (Int) -> Unit,
    onOpenAllLevels: () -> Unit,
    onRoot: (RootDestination) -> Unit,
    onProfile: () -> Unit,
) {
    val state by viewModel.collectAsState()
    viewModel.collectSideEffect { effect ->
        when (effect) {
            is HomeSideEffect.OpenPuzzle -> onOpenPuzzle(effect.level)
            HomeSideEffect.OpenAllLevels -> onOpenAllLevels()
            HomeSideEffect.OpenDaily -> onRoot(RootDestination.Daily)
            HomeSideEffect.OpenProfile -> onProfile()
        }
    }
    HomeContent(
        state = state,
        onContinue = viewModel::continuePuzzle,
        onLevel = viewModel::openLevel,
        onAllLevels = viewModel::openAllLevels,
        onDaily = viewModel::openDaily,
        onProfile = viewModel::openProfile,
        onRoot = onRoot,
    )
}

@Composable
internal fun HomeContent(
    state: HomeState,
    onContinue: () -> Unit,
    onLevel: (Int) -> Unit,
    onAllLevels: () -> Unit,
    onDaily: () -> Unit,
    onProfile: () -> Unit,
    onRoot: (RootDestination) -> Unit,
) {
    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        containerColor = Ivory,
        bottomBar = { MoriartyBottomBar(RootDestination.Levels, onRoot) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            HomeHeader(state, onProfile)
            ContinueCard(state, onContinue)
            JourneyCard(state.nearbyLevels, state.currentLevel?.number, onLevel, onAllLevels)
            DailyCard(state.dailyReset, onDaily)
            Spacer(Modifier.height(4.dp))
        }
    }
}

@Composable
private fun HomeHeader(state: HomeState, onProfile: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text("Moriarty", style = MaterialTheme.typography.headlineLarge, color = Ink)
            Text("See what others miss.", style = MaterialTheme.typography.bodyMedium, color = InkMuted)
        }
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = SoftAmber,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 11.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Outlined.LocalFireDepartment, null, tint = Color(0xFFE77C22), modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(5.dp))
                Text(if (state.streak == 0) "New streak" else "${state.streak} days", style = MaterialTheme.typography.labelMedium)
            }
        }
        Spacer(Modifier.width(9.dp))
        IconCircle(Icons.Outlined.PersonOutline, onProfile, "Profile")
    }
}

@Composable
private fun ContinueCard(state: HomeState, onContinue: () -> Unit) {
    val progress = if (state.totalLevels == 0) 0f else state.solvedCount / state.totalLevels.toFloat()
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = Paper,
        border = BorderStroke(1.dp, Line),
        shadowElevation = 3.dp,
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Continue", style = MaterialTheme.typography.labelLarge, color = Emerald)
                    Text(
                        "Level ${state.currentLevel?.number ?: 1}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Ink,
                    )
                }
                DifficultyPill("Difficulty ${state.currentLevel?.difficultyRating ?: 1}/10")
            }
            state.currentPuzzle?.let { puzzle ->
                PuzzleContentRenderer(
                    content = puzzle.content,
                    modifier = Modifier.fillMaxWidth().height(220.dp),
                    compact = true,
                )
                Text(puzzle.prompt, style = MaterialTheme.typography.titleMedium, color = Ink)
            }
            HorizontalDivider(color = Line)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("${state.solvedCount} solved", style = MaterialTheme.typography.labelMedium, color = InkMuted)
                Spacer(Modifier.weight(1f))
                Text("${state.totalLevels} levels", style = MaterialTheme.typography.labelMedium, color = InkMuted)
            }
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                color = Amber,
                trackColor = Line,
            )
            MoriartyPrimaryButton(
                text = "Solve level ${state.currentLevel?.number ?: 1}",
                onClick = onContinue,
                trailing = { Icon(Icons.AutoMirrored.Outlined.ArrowForward, null) },
            )
        }
    }
}

@Composable
private fun JourneyCard(
    levels: List<Level>,
    currentLevel: Int?,
    onLevel: (Int) -> Unit,
    onAllLevels: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionTitle("Your path", "View all", onAllLevels)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = Paper,
            border = BorderStroke(1.dp, Line),
        ) {
            Column(Modifier.padding(horizontal = 16.dp, vertical = 17.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    levels.forEachIndexed { index, level ->
                        if (index > 0) {
                            Box(Modifier.weight(1f).height(2.dp).background(if (level.isUnlocked) Mint else Line))
                        }
                        LevelStep(level, level.number == currentLevel) { onLevel(level.number) }
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    levels.firstOrNull { it.number == currentLevel }?.title ?: "Start with level 1",
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkMuted,
                )
            }
        }
    }
}

@Composable
private fun LevelStep(level: Level, isCurrent: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.size(43.dp).clickable(enabled = level.isUnlocked, onClick = onClick),
        shape = CircleShape,
        color = when {
            isCurrent -> Emerald
            level.isSolved -> Mint
            else -> Ivory
        },
        border = BorderStroke(1.dp, if (isCurrent) Emerald else Line),
    ) {
        Box(contentAlignment = Alignment.Center) {
            when {
                level.isSolved -> Icon(Icons.Outlined.Check, "Solved", tint = Emerald, modifier = Modifier.size(19.dp))
                !level.isUnlocked -> Icon(Icons.Outlined.Lock, "Locked", tint = InkMuted, modifier = Modifier.size(16.dp))
                else -> Text(
                    level.number.toString(),
                    color = if (isCurrent) Color.White else Ink,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun DailyCard(reset: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        color = Paper,
        border = BorderStroke(1.dp, Line),
    ) {
        Row(
            modifier = Modifier.padding(15.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SmallGeometryThumbnail(Modifier.size(54.dp))
            Spacer(Modifier.width(13.dp))
            Column(Modifier.weight(1f)) {
                Text("Daily challenge", style = MaterialTheme.typography.titleMedium, color = Ink)
                Text("One puzzle. One attempt.", style = MaterialTheme.typography.bodyMedium, color = InkMuted)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Resets in", style = MaterialTheme.typography.labelMedium, color = InkMuted)
                Text(reset, style = MaterialTheme.typography.titleMedium, color = Ink)
            }
            Icon(
                Icons.AutoMirrored.Outlined.ArrowForward,
                null,
                tint = Emerald,
                modifier = Modifier.padding(start = 8.dp).size(20.dp),
            )
        }
    }
}
