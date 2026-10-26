package com.moriartystudio.moriarty.feature.levels.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.moriartystudio.moriarty.core.designsystem.DifficultyPill
import com.moriartystudio.moriarty.core.designsystem.IconCircle
import com.moriartystudio.moriarty.core.designsystem.TopBar
import com.moriartystudio.moriarty.core.model.Difficulty
import com.moriartystudio.moriarty.core.model.Level
import com.moriartystudio.moriarty.core.model.PuzzleCategory
import com.moriartystudio.moriarty.ui.theme.Emerald
import com.moriartystudio.moriarty.ui.theme.Ink
import com.moriartystudio.moriarty.ui.theme.InkMuted
import com.moriartystudio.moriarty.ui.theme.Ivory
import com.moriartystudio.moriarty.ui.theme.Line
import com.moriartystudio.moriarty.ui.theme.Mint
import com.moriartystudio.moriarty.ui.theme.Mist
import com.moriartystudio.moriarty.ui.theme.Paper
import com.moriartystudio.moriarty.ui.theme.SoftAmber
import com.moriartystudio.moriarty.ui.theme.SoftPeach
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun LevelsScreen(viewModel: LevelsViewModel, onBack: () -> Unit, onOpenPuzzle: (Int) -> Unit) {
    val state by viewModel.collectAsState()
    viewModel.collectSideEffect {
        when (it) {
            LevelsSideEffect.GoBack -> onBack()
            is LevelsSideEffect.OpenPuzzle -> onOpenPuzzle(it.level)
        }
    }

    Scaffold(modifier = Modifier.statusBarsPadding(), containerColor = Ivory) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            TopBar(
                title = "All levels",
                subtitle = "${state.solvedCount} of ${state.levels.size} solved",
                leading = { IconCircle(Icons.AutoMirrored.Outlined.ArrowBack, viewModel::goBack, "Back") },
            )
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 18.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FilterChip(
                    selected = state.selectedCategory == null,
                    onClick = { viewModel.selectCategory(null) },
                    label = { Text("All") },
                    colors = levelFilterColors(),
                )
                PuzzleCategory.entries.forEach { category ->
                    FilterChip(
                        selected = state.selectedCategory == category,
                        onClick = { viewModel.selectCategory(category) },
                        label = { Text(category.label) },
                        colors = levelFilterColors(),
                    )
                }
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(state.visibleLevels, key = Level::number) { level ->
                    LevelCard(level, level.number == state.currentLevelNumber) {
                        viewModel.openLevel(level.number, level.isUnlocked)
                    }
                }
            }
        }
    }
}

@Composable
private fun LevelCard(level: Level, isCurrent: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isCurrent) Mint else Paper, RoundedCornerShape(22.dp))
            .clickable(enabled = level.isUnlocked, onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(52.dp).background(
                when {
                    level.isSolved -> Emerald
                    isCurrent -> Emerald
                    else -> Mist
                },
                CircleShape,
            ),
            contentAlignment = Alignment.Center,
        ) {
            if (!level.isUnlocked) Icon(Icons.Outlined.Lock, "Locked", tint = InkMuted)
            else Text(level.number.toString(), color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            if (level.isSolved) {
                Icon(
                    Icons.Outlined.Check,
                    "Solved",
                    tint = Color.White,
                    modifier = Modifier.align(Alignment.TopEnd).size(18.dp).background(Emerald, CircleShape),
                )
            }
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(level.title, style = MaterialTheme.typography.titleMedium, color = if (level.isUnlocked) Ink else InkMuted)
            Text(level.category.label, color = InkMuted, style = MaterialTheme.typography.bodyMedium)
            level.bestTimeSeconds?.let { seconds ->
                Text("Best %02d:%02d".format(seconds / 60, seconds % 60), color = Emerald, style = MaterialTheme.typography.labelMedium)
            }
        }
        DifficultyPill(
            level.difficulty.name,
            when (level.difficulty) {
                Difficulty.Easy -> Mint
                Difficulty.Medium -> SoftAmber
                Difficulty.Hard -> SoftPeach
            },
            Ink,
        )
    }
}

@Composable
private fun levelFilterColors() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = Emerald,
    selectedLabelColor = Color.White,
    containerColor = Paper,
)
