package com.moriartystudio.moriarty.feature.levels.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.moriartystudio.moriarty.core.designsystem.*
import com.moriartystudio.moriarty.core.model.PuzzleCategory
import com.moriartystudio.moriarty.ui.theme.*
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun LevelsScreen(viewModel: LevelsViewModel, onBack: () -> Unit, onOpenPuzzle: (Int) -> Unit) {
    val state by viewModel.collectAsState()
    viewModel.collectSideEffect { when (it) { LevelsSideEffect.GoBack -> onBack(); is LevelsSideEffect.OpenPuzzle -> onOpenPuzzle(it.level) } }
    Scaffold(modifier = Modifier.statusBarsPadding(), containerColor = Ivory) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            TopBar("All levels", "6 of 50 solved", leading = { IconCircle(Icons.AutoMirrored.Outlined.ArrowBack, viewModel::goBack, "Back") })
            Row(Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 18.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(state.selectedCategory == null, { viewModel.selectCategory(null) }, { Text("All") }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Emerald, selectedLabelColor = Color.White))
                listOf(PuzzleCategory.Reflection, PuzzleCategory.Sequence, PuzzleCategory.Rotation, PuzzleCategory.Deduction).forEach { category ->
                    FilterChip(state.selectedCategory == category, { viewModel.selectCategory(category) }, { Text(if (category == PuzzleCategory.Sequence) "Patterns" else if (category == PuzzleCategory.Rotation) "Spatial" else category.label) }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Emerald, selectedLabelColor = Color.White))
                }
            }
            Box(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                Canvas(Modifier.fillMaxWidth().height(930.dp)) {
                    val p = Path().apply { moveTo(size.width * .22f, 0f); cubicTo(size.width * .10f, 120f, size.width * .34f, 220f, size.width * .23f, 340f); cubicTo(size.width * .12f, 470f, size.width * .33f, 590f, size.width * .20f, 730f); cubicTo(size.width * .10f, 820f, size.width * .32f, 900f, size.width * .25f, size.height) }
                    drawPath(p, Mint, style = Stroke(13f))
                }
                Column(Modifier.padding(horizontal = 20.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    state.visibleLevels.filter { it.number in 4..9 }.forEach { level ->
                        val current = level.number == 7
                        Row(Modifier.fillMaxWidth().height(if (current) 238.dp else 118.dp).clickable(enabled = level.isUnlocked) { viewModel.openLevel(level.number, level.isUnlocked) }, verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(if (current) 72.dp else 58.dp).background(if (current || level.isSolved) Emerald else Mist, CircleShape), contentAlignment = Alignment.Center) {
                                if (!level.isUnlocked) Icon(Icons.Outlined.Lock, null, tint = InkMuted) else Text(level.number.toString(), color = Color.White, style = MaterialTheme.typography.headlineMedium)
                                if (level.isSolved) Icon(Icons.Outlined.Check, null, tint = Color.White, modifier = Modifier.align(Alignment.TopEnd).size(22.dp).background(Emerald, CircleShape))
                            }
                            Spacer(Modifier.width(28.dp))
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(level.title, style = if (current) MaterialTheme.typography.titleLarge else MaterialTheme.typography.titleMedium)
                                Row(verticalAlignment = Alignment.CenterVertically) { Text(level.category.label, color = InkMuted, modifier = Modifier.weight(1f)); DifficultyPill(level.difficulty.name, if (level.difficulty.name == "Hard") SoftPeach else if (level.difficulty.name == "Medium") SoftAmber else Mint, Ink) }
                                if (current) GeometricMirrorHero(Modifier.fillMaxWidth().height(150.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
