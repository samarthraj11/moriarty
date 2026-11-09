package com.moriartystudio.moriarty.feature.levels.presentation

import com.moriartystudio.moriarty.core.designsystem.GlassIconButton
import com.moriartystudio.moriarty.core.designsystem.MoriartyPage
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.moriartystudio.moriarty.core.designsystem.IconCircle
import com.moriartystudio.moriarty.core.designsystem.TopBar
import com.moriartystudio.moriarty.core.model.Level
import com.moriartystudio.moriarty.core.model.PuzzleCategory
import com.moriartystudio.moriarty.ui.theme.Amber
import com.moriartystudio.moriarty.ui.theme.Indigo
import com.moriartystudio.moriarty.ui.theme.Ink
import com.moriartystudio.moriarty.ui.theme.InkMuted
import com.moriartystudio.moriarty.ui.theme.Ivory
import com.moriartystudio.moriarty.ui.theme.Line
import com.moriartystudio.moriarty.ui.theme.Frost
import com.moriartystudio.moriarty.ui.theme.Paper
import com.moriartystudio.moriarty.ui.theme.SoftFrost
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun LevelsScreen(
    viewModel: LevelsViewModel,
    onBack: () -> Unit,
    onOpenPuzzle: (Int) -> Unit,
) {
    val state by viewModel.collectAsState()
    viewModel.collectSideEffect {
        when (it) {
            LevelsSideEffect.GoBack -> onBack()
            is LevelsSideEffect.OpenPuzzle -> onOpenPuzzle(it.level)
        }
    }
    LevelsContent(
        state = state,
        onBack = viewModel::goBack,
        onCategory = viewModel::selectCategory,
        onLevel = { viewModel.openLevel(it.number, it.isUnlocked) },
    )
}

@Composable
private fun LevelsContent(
    state: LevelsState,
    onBack: () -> Unit,
    onCategory: (PuzzleCategory?) -> Unit,
    onLevel: (Level) -> Unit,
) {
    val current = state.levels.firstOrNull { it.number == state.currentLevelNumber }
    val listState = rememberLazyListState()
    LaunchedEffect(state.currentLevelNumber, state.selectedCategory) {
        val index = state.visibleLevels.indexOfFirst { it.number == state.currentLevelNumber }
        if (index >= 3) listState.scrollToItem(index + 2)
    }

    MoriartyPage(
        title = "All levels",
        subtitle = "Choose what to solve next",
        leading = { GlassIconButton(Icons.AutoMirrored.Outlined.ArrowBack, onBack, "Back") },
        bottomBar = { CurrentLevelBar(current = current, onContinue = { current?.let(onLevel) }) },
        scrollable = false,
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 22.dp,
                bottom = 24.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(11.dp),
        ) {
            item {
                ProgressCard(state.solvedCount, state.levels.size)
            }
            item {
                CategoryFilters(state.selectedCategory, onCategory)
            }
            items(state.visibleLevels, key = Level::number) { level ->
                LevelCard(
                    level = level,
                    isCurrent = level.number == state.currentLevelNumber,
                    onClick = { onLevel(level) },
                )
            }
        }
    }
}

@Composable
private fun ProgressCard(solved: Int, total: Int) {
    val progress = if (total == 0) 0f else solved / total.toFloat()
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Paper,
        border = BorderStroke(1.dp, Line),
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.Bottom) {
                Column(Modifier.weight(1f)) {
                    Text("Your collection", style = MaterialTheme.typography.titleLarge, color = Ink)
                    Text("Each solved card reveals the next.", style = MaterialTheme.typography.bodyMedium, color = InkMuted)
                }
                Text(
                    "$solved / $total",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Indigo,
                )
            }
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(7.dp).clip(CircleShape),
                color = Amber,
                trackColor = Line,
            )
        }
    }
}

@Composable
private fun CategoryFilters(
    selected: PuzzleCategory?,
    onCategory: (PuzzleCategory?) -> Unit,
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            CategoryChip("All", selected == null) { onCategory(null) }
        }
        items(PuzzleCategory.entries) { category ->
            CategoryChip(category.label, selected == category) { onCategory(category) }
        }
    }
}

@Composable
private fun CategoryChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, style = MaterialTheme.typography.labelLarge) },
        shape = RoundedCornerShape(14.dp),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = Paper,
            selectedContainerColor = Frost,
            labelColor = InkMuted,
            selectedLabelColor = Indigo,
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = Line,
            selectedBorderColor = Indigo.copy(alpha = 0.28f),
        ),
    )
}

@Composable
private fun LevelCard(level: Level, isCurrent: Boolean, onClick: () -> Unit) {
    val accent = when (level.category) {
        PuzzleCategory.Pattern -> Amber
        PuzzleCategory.Spatial -> Indigo
        PuzzleCategory.Deduction -> Color(0xFFDB7659)
        PuzzleCategory.Logic -> Color(0xFF587DA8)
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = level.isUnlocked, onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        color = if (isCurrent) SoftFrost else Paper,
        border = BorderStroke(if (isCurrent) 1.5.dp else 1.dp, if (isCurrent) Indigo else Line),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .height(64.dp)
                    .background(if (level.isUnlocked) accent else Line, CircleShape),
            )
            Spacer(Modifier.width(12.dp))
            Surface(
                modifier = Modifier.size(54.dp),
                shape = RoundedCornerShape(17.dp),
                color = if (level.isUnlocked) accent.copy(alpha = 0.13f) else Ivory,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (level.isUnlocked) {
                        Text(
                            level.number.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            color = if (isCurrent) Indigo else Ink,
                            fontWeight = FontWeight.Bold,
                        )
                    } else {
                        Icon(Icons.Outlined.Lock, "Locked", tint = InkMuted, modifier = Modifier.size(20.dp))
                    }
                }
            }
            Spacer(Modifier.width(13.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(
                    level.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (level.isUnlocked) Ink else InkMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(7.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(level.category.label, style = MaterialTheme.typography.labelMedium, color = accent)
                    Box(Modifier.size(3.dp).background(Line, CircleShape))
                    Text("Difficulty ${level.difficultyRating}/10", style = MaterialTheme.typography.labelMedium, color = InkMuted)
                }
                level.bestTimeSeconds?.let {
                    Text("Best  %d:%02d".format(it / 60, it % 60), style = MaterialTheme.typography.labelMedium, color = Indigo)
                }
            }
            when {
                level.isSolved -> Surface(shape = CircleShape, color = Frost) {
                    Icon(
                        Icons.Outlined.Check,
                        "Solved",
                        tint = Indigo,
                        modifier = Modifier.padding(9.dp).size(18.dp),
                    )
                }
                isCurrent -> Surface(shape = CircleShape, color = Indigo) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        "Continue",
                        tint = Color.White,
                        modifier = Modifier.padding(9.dp).size(18.dp),
                    )
                }
                level.isUnlocked -> Text("Open", style = MaterialTheme.typography.labelMedium, color = Indigo)
            }
        }
    }
}

@Composable
private fun CurrentLevelBar(current: Level?, onContinue: () -> Unit) {
    Surface(color = Color.White, shadowElevation = 12.dp, shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)) {
        Column(Modifier.navigationBarsPadding()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        if (current == null) "Journey complete" else "Continue your path",
                        style = MaterialTheme.typography.labelMedium,
                        color = InkMuted,
                    )
                    Text(
                        current?.let { "Level ${it.number} · ${it.title}" } ?: "All levels solved",
                        style = MaterialTheme.typography.titleMedium,
                        color = Ink,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Button(
                    onClick = onContinue,
                    enabled = current != null,
                    modifier = Modifier.height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo),
                ) {
                    Text("Continue", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
