package com.moriartystudio.moriarty.feature.home.presentation

import com.moriartystudio.moriarty.core.designsystem.categoryIcon
import com.moriartystudio.moriarty.core.designsystem.rememberDailyCountdown
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.ViewInAr
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.moriartystudio.moriarty.app.navigation.RootDestination
import com.moriartystudio.moriarty.core.designsystem.MoriartyBottomBar
import com.moriartystudio.moriarty.core.designsystem.MoriartyMark
import com.moriartystudio.moriarty.core.designsystem.PuzzleContentRenderer
import com.moriartystudio.moriarty.core.designsystem.SmallGeometryThumbnail
import com.moriartystudio.moriarty.core.model.Level
import com.moriartystudio.moriarty.core.model.PuzzleCategory
import com.moriartystudio.moriarty.domain.daily.DailyClock
import com.moriartystudio.moriarty.ui.theme.Frost
import com.moriartystudio.moriarty.ui.theme.Gold
import com.moriartystudio.moriarty.ui.theme.Indigo
import com.moriartystudio.moriarty.ui.theme.IndigoLight
import com.moriartystudio.moriarty.ui.theme.Lavender
import com.moriartystudio.moriarty.ui.theme.Night
import com.moriartystudio.moriarty.ui.theme.categoryColor
import kotlinx.coroutines.delay
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import java.time.ZonedDateTime

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onOpenPuzzle: (Int) -> Unit,
    onOpenAllLevels: () -> Unit,
    onRoot: (RootDestination) -> Unit,
    onProfile: () -> Unit,
    onOpenCategory: (PuzzleCategory) -> Unit,
) {
    val state by viewModel.collectAsState()
    LaunchedEffect(Unit) { viewModel.refresh() }
    viewModel.collectSideEffect { effect ->
        when (effect) {
            is HomeSideEffect.OpenPuzzle -> onOpenPuzzle(effect.level)
            HomeSideEffect.OpenAllLevels -> onOpenAllLevels()
            is HomeSideEffect.OpenCategory -> onOpenCategory(effect.category)
            HomeSideEffect.OpenDaily -> onRoot(RootDestination.Daily)
            HomeSideEffect.OpenProfile -> onProfile()
        }
    }
    HomeContent(
        state = state,
        onContinue = viewModel::continuePuzzle,
        onLevel = viewModel::openLevel,
        onAllLevels = viewModel::openAllLevels,
        onCategory = viewModel::openCategory,
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
    onCategory: (PuzzleCategory) -> Unit = {},
) {
    Scaffold(
        containerColor = Color.White,
        bottomBar = { MoriartyBottomBar(RootDestination.Levels, onRoot) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding())
                .verticalScroll(rememberScrollState()),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Indigo, IndigoLight)))
                    .statusBarsPadding()
                    .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 52.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                HomeHeader(state, onProfile)
                HeroCard(state, onContinue)
            }
            // The white sheet rides up over the gradient, like the reference design.
            Column(
                modifier = Modifier
                    .offset(y = (-30).dp)
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .padding(horizontal = 20.dp)
                    .padding(top = 24.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(22.dp),
            ) {
                ExploreSection(state, onCategory)
                DailyCard(onDaily)
                RecentSolves(state.nearbyLevels, onLevel, onAllLevels)
            }
            Spacer(Modifier.height(0.dp))
        }
    }
}

@Composable
private fun HomeHeader(state: HomeState, onProfile: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        MoriartyMark(
            Modifier
                .size(46.dp)
                .clip(CircleShape)
                .clickable(onClick = onProfile),
            contentDescription = "Profile"
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                "Welcome back!",
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodyMedium
            )
            Text("Moriarty", color = Color.White, style = MaterialTheme.typography.titleLarge)
        }
        StatPill {
            Icon(
                Icons.Outlined.LocalFireDepartment,
                null,
                tint = Color(0xFFE77C22),
                modifier = Modifier.size(17.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                state.streak.toString(),
                style = MaterialTheme.typography.labelLarge,
                color = Night
            )
        }
        Spacer(Modifier.width(8.dp))
        StatPill {
            Text("\uD83C\uDFC6", fontSize = 15.sp)
            Spacer(Modifier.width(4.dp))
            Text(
                state.points.toString(),
                style = MaterialTheme.typography.labelLarge,
                color = Night
            )
        }
    }
}

@Composable
private fun StatPill(content: @Composable () -> Unit) {
    Row(
        Modifier
            .background(Color.White, RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) { content() }
}

/** Frosted-glass card holding the next puzzle itself, so one tap starts playing. */
@Composable
private fun HeroCard(state: HomeState, onContinue: () -> Unit) {
    val next = state.currentLevel?.categoryLevel ?: 1
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color.White.copy(alpha = 0.30f),
                        Color.White.copy(alpha = 0.10f)
                    )
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(28.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Level $next",
                    color = Color.White.copy(alpha = 0.85f),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                state.currentLevel?.category?.let { SectionTag(it) }
            }
            Text(
                state.currentPuzzle?.title ?: "Start playing",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium
            )
        }
        state.currentPuzzle?.let { puzzle ->
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .clickable(onClick = onContinue),
            ) {
                PuzzleContentRenderer(
                    content = puzzle.content,
                    compact = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp),
                )
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(RoundedCornerShape(50))
                .background(Color.White)
                .clickable(onClick = onContinue),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(Icons.Outlined.PlayArrow, null, tint = Indigo, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(4.dp))
            Text(
                "Play",
                color = Indigo,
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}


/** Which of the four sections the previewed puzzle belongs to: its icon and name in that section's colour. */
@Composable
private fun SectionTag(category: PuzzleCategory) {
    Row(
        Modifier
            .background(Color.White, RoundedCornerShape(50))
            .padding(horizontal = 11.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            categoryIcon(category),
            null,
            tint = categoryColor(category),
            modifier = Modifier.size(15.dp)
        )
        Spacer(Modifier.width(2.dp))
        Text(
            category.label,
            style = MaterialTheme.typography.labelMedium,
            color = categoryColor(category)
        )
    }
}

@Composable
private fun SectionHeader(title: String, action: String? = null, onAction: (() -> Unit)? = null) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            title,
            style = MaterialTheme.typography.titleLarge,
            color = Night,
            modifier = Modifier.weight(1f)
        )
        if (action != null && onAction != null) {
            Text(
                action,
                style = MaterialTheme.typography.labelLarge,
                color = Indigo,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(onClick = onAction)
                    .padding(8.dp),
            )
        }
    }
}

@Composable
private fun ExploreSection(state: HomeState, onCategory: (PuzzleCategory) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Explore",
                style = MaterialTheme.typography.titleLarge,
                color = Night,
                modifier = Modifier.weight(1f)
            )
            ProgressRing(state.solvedCount, state.totalLevels)
        }
        val progress = PuzzleCategory.entries.map { category ->
            state.categoryProgress.firstOrNull { it.category == category } ?: CategoryProgress(
                category,
                0,
                0
            )
        }
        progress.chunked(2).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { item ->
                    CategoryTile(
                        item,
                        { onCategory(item.category) },
                        Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

/** Circle around "solved/total", filled clockwise to the overall progress. */
@Composable
private fun ProgressRing(solved: Int, total: Int) {
    val fraction = if (total == 0) 0f else solved / total.toFloat()
    Box(Modifier.size(58.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val stroke = 5.dp.toPx()
            val inset = stroke / 2
            val arcSize = Size(size.width - stroke, size.height - stroke)
            drawArc(Frost, 0f, 360f, false, Offset(inset, inset), arcSize, style = Stroke(stroke))
            drawArc(
                Indigo,
                -90f,
                360f * fraction,
                false,
                Offset(inset, inset),
                arcSize,
                style = Stroke(stroke, cap = StrokeCap.Round)
            )
        }
        Text(
            "$solved/$total",
            style = MaterialTheme.typography.labelMedium,
            color = Night,
            fontWeight = FontWeight.Bold
        )
    }
}

/** Tile washed in the category colour and filled left to right up to that category's progress. */
@Composable
private fun CategoryTile(
    progress: CategoryProgress,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = categoryColor(progress.category)
    val icon = categoryIcon(progress.category)
    Box(
        modifier
            .clip(RoundedCornerShape(22.dp))
            .background(color.copy(alpha = 0.10f))
            .clickable(onClick = onClick),
    ) {
        Box(Modifier.matchParentSize()) {
            Box(
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress.fraction)
                    .background(color.copy(alpha = 0.28f))
            )
        }
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(42.dp)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    progress.category.label,
                    style = MaterialTheme.typography.titleMedium,
                    color = Night
                )
                Text(
                    "${progress.solved} of ${progress.total}",
                    style = MaterialTheme.typography.labelMedium,
                    color = Night.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun RecentSolves(levels: List<Level>, onLevel: (Int) -> Unit, onAllLevels: () -> Unit) {
    val solved = levels.filter { it.isSolved }.sortedByDescending { it.number }.take(3)
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        SectionHeader("Recent solves", "View all", onAllLevels)
        if (solved.isEmpty()) {
            Text(
                "Solve your first puzzle and it will show up here.",
                style = MaterialTheme.typography.bodyMedium,
                color = Night.copy(alpha = 0.55f),
                modifier = Modifier.padding(vertical = 6.dp)
            )
        }
        solved.forEach { level ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onLevel(level.number) }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier
                        .size(40.dp)
                        .background(Frost, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Check, null, tint = Indigo, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        "Level ${level.categoryLevel} · ${level.title}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Night,
                        maxLines = 1
                    )
                    Text(
                        level.category.label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Night.copy(alpha = 0.5f)
                    )
                }
                level.points?.let {
                    Text(
                        if (it > 0) "+$it" else if (it < 0) "\u2212${-it}" else "0",
                        style = MaterialTheme.typography.titleMedium,
                        color = Indigo
                    )
                }
            }
        }
    }
}

@Composable
private fun DailyCard(onClick: () -> Unit) {
    val reset = rememberDailyCountdown()
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        color = Frost.copy(alpha = 0.55f),
    ) {
        Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
            SmallGeometryThumbnail(Modifier.size(54.dp), accent = Lavender)
            Spacer(Modifier.width(13.dp))
            Column(Modifier.weight(1f)) {
                Text("Daily challenge", style = MaterialTheme.typography.titleMedium, color = Night)
                Text(
                    "One puzzle. One attempt.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Night.copy(alpha = 0.55f)
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "Resets in",
                    style = MaterialTheme.typography.labelMedium,
                    color = Night.copy(alpha = 0.55f)
                )
                Text(reset, style = MaterialTheme.typography.titleMedium, color = Indigo)
            }
            Icon(
                Icons.AutoMirrored.Outlined.ArrowForward,
                null,
                tint = Indigo,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(20.dp)
            )
        }
    }
}
