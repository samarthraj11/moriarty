package com.moriartystudio.moriarty.feature.category.presentation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moriartystudio.moriarty.core.model.Level
import com.moriartystudio.moriarty.core.model.PuzzleCategory
import com.moriartystudio.moriarty.ui.theme.InkMuted
import com.moriartystudio.moriarty.ui.theme.Mist
import com.moriartystudio.moriarty.ui.theme.Night
import com.moriartystudio.moriarty.ui.theme.categoryColor
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

private const val GRID_COLUMNS = 5

@Composable
fun CategoryScreen(viewModel: CategoryViewModel, onBack: () -> Unit, onOpenPuzzle: (Int) -> Unit) {
    val state by viewModel.collectAsState()
    LaunchedEffect(Unit) { viewModel.refresh() }
    viewModel.collectSideEffect {
        when (it) {
            CategorySideEffect.GoBack -> onBack()
            is CategorySideEffect.OpenPuzzle -> onOpenPuzzle(it.level)
        }
    }
    CategoryContent(state, onBack = viewModel::goBack, onLevel = viewModel::openLevel, onPlay = viewModel::play)
}

@Composable
internal fun CategoryContent(
    state: CategoryState,
    onBack: () -> Unit,
    onLevel: (Level) -> Unit,
    onPlay: () -> Unit,
) {
    val color = categoryColor(state.category)
    val onColor = onCategoryColor(state.category)
    Scaffold(
        containerColor = Color.White,
        bottomBar = { PlayBar(state, color, onColor, onPlay) },
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding())
                .verticalScroll(rememberScrollState()),
        ) {
            Header(state, color, onColor, onBack)
            Column(
                Modifier
                    .offset(y = (-28).dp)
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .padding(horizontal = 20.dp)
                    .padding(top = 22.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                Stats(state, color)
                Text("Levels", style = MaterialTheme.typography.titleLarge, color = Night)
                LevelGrid(state, color, onColor, onLevel)
            }
        }
    }
}

// Text on the coloured header: white on indigo, near-black where the colour is light.
private fun onCategoryColor(category: PuzzleCategory): Color =
    if (category == PuzzleCategory.Spatial) Color.White else Night

private fun voice(category: PuzzleCategory): String = when (category) {
    PuzzleCategory.Pattern -> "Everything repeats. Until it doesn't."
    PuzzleCategory.Spatial -> "Turn it over in your head."
    PuzzleCategory.Deduction -> "Cross out what can't be true."
    PuzzleCategory.Logic -> "If this, then only that."
}

@Composable
private fun Header(state: CategoryState, color: Color, onColor: Color, onBack: () -> Unit) {
    // One slow highlight sweeps across the header; it is the only decoration on the screen.
    val sweep = rememberInfiniteTransition(label = "sweep").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3800, easing = FastOutSlowInEasing), RepeatMode.Restart),
        label = "sweep",
    )
    Box(
        Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(lerp(color, Color.Black, 0.10f), lerp(color, Color.White, 0.22f))))
            .drawBehind {
                val w = size.width
                val x = -w * 0.5f + w * 2f * sweep.value
                drawRect(
                    Brush.linearGradient(
                        colors = listOf(Color.Transparent, Color.White.copy(alpha = 0.30f), Color.Transparent),
                        start = Offset(x, 0f),
                        end = Offset(x + w * 0.45f, size.height * 0.7f),
                    ),
                )
            },
    ) {
        Column(
            Modifier.statusBarsPadding().padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 56.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Box(
                Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(onColor.copy(alpha = 0.14f))
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center,
            ) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back", tint = onColor, modifier = Modifier.size(21.dp)) }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(state.category.label, color = onColor, fontSize = 34.sp, fontWeight = FontWeight.Bold, lineHeight = 38.sp)
                    Text(voice(state.category), color = onColor.copy(alpha = 0.85f), style = MaterialTheme.typography.bodyLarge)
                }
                Spacer(Modifier.width(12.dp))
                Ring(state, onColor)
            }
        }
    }
}

@Composable
private fun Ring(state: CategoryState, onColor: Color) {
    Box(Modifier.size(68.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val stroke = 6.dp.toPx()
            val topLeft = Offset(stroke / 2, stroke / 2)
            val arc = Size(size.width - stroke, size.height - stroke)
            drawArc(onColor.copy(alpha = 0.2f), 0f, 360f, false, topLeft, arc, style = Stroke(stroke))
            drawArc(onColor, -90f, 360f * state.percent / 100f, false, topLeft, arc, style = Stroke(stroke, cap = StrokeCap.Round))
        }
        Text("${state.percent}%", color = onColor, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun Stats(state: CategoryState, color: Color) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Stat("Points", state.points.toString(), color, Modifier.weight(1f))
        Stat("First try", state.firstTryPercent?.let { "$it%" } ?: "–", color, Modifier.weight(1f))
        Stat("Solved", "${state.solved}/${state.total}", color, Modifier.weight(1f))
    }
}

@Composable
private fun Stat(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier.background(color.copy(alpha = 0.12f), RoundedCornerShape(16.dp)).padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = InkMuted)
        Text(value, style = MaterialTheme.typography.titleLarge, color = Night)
    }
}

@Composable
private fun LevelGrid(state: CategoryState, color: Color, onColor: Color, onLevel: (Level) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        state.levels.chunked(GRID_COLUMNS).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { level ->
                    LevelCell(level, level.number == state.next?.number, color, onColor, { onLevel(level) }, Modifier.weight(1f))
                }
                repeat(GRID_COLUMNS - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun LevelCell(level: Level, isNext: Boolean, color: Color, onColor: Color, onClick: () -> Unit, modifier: Modifier = Modifier) {
    // The next level breathes gently so the eye lands there first.
    val breathe = rememberInfiniteTransition(label = "breathe").animateFloat(
        initialValue = 1f,
        targetValue = if (isNext) 1.07f else 1f,
        animationSpec = infiniteRepeatable(tween(1300), RepeatMode.Reverse),
        label = "breathe",
    )
    val shape = RoundedCornerShape(16.dp)
    val background = when {
        level.isSolved -> color
        isNext -> color.copy(alpha = 0.16f)
        level.isUnlocked -> color.copy(alpha = 0.08f)
        else -> Mist
    }
    Box(
        modifier
            .aspectRatio(1f)
            .scale(breathe.value)
            .semantics { contentDescription = "Level ${level.number}" }
            .clip(shape)
            .background(background)
            .then(if (isNext) Modifier.border(2.5.dp, color, shape) else Modifier)
            .clickable(enabled = level.isUnlocked, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        when {
            level.isSolved -> Icon(Icons.Outlined.Check, null, tint = onColor, modifier = Modifier.size(22.dp))
            !level.isUnlocked -> Icon(Icons.Outlined.Lock, null, tint = InkMuted.copy(alpha = 0.6f), modifier = Modifier.size(18.dp))
            else -> Text(level.number.toString(), style = MaterialTheme.typography.titleMedium, color = Night, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun PlayBar(state: CategoryState, color: Color, onColor: Color, onPlay: () -> Unit) {
    val target = state.next ?: state.fallback
    Surface(color = Color.White, shadowElevation = 12.dp, shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)) {
        Row(
            Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    when {
                        state.next != null -> "Up next"
                        target != null -> "Nothing open here yet"
                        else -> "All done"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = InkMuted,
                )
                Text(
                    when {
                        target != null -> "Level ${target.number}"
                        else -> "You found everything"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = Night,
                )
            }
            if (target != null) {
                Row(
                    Modifier.clip(RoundedCornerShape(50)).background(color).clickable(onClick = onPlay).padding(horizontal = 22.dp, vertical = 13.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Outlined.PlayArrow, null, tint = onColor, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Play", color = onColor, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}
