package com.moriartystudio.moriarty.feature.home.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ViewInAr
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moriartystudio.moriarty.app.navigation.RootDestination
import com.moriartystudio.moriarty.core.designsystem.CoinStack
import com.moriartystudio.moriarty.core.designsystem.MoriartyBottomBar
import com.moriartystudio.moriarty.core.designsystem.SmallGeometryThumbnail
import com.moriartystudio.moriarty.core.model.Level
import com.moriartystudio.moriarty.core.model.PuzzleCategory
import com.moriartystudio.moriarty.ui.theme.Gold
import com.moriartystudio.moriarty.ui.theme.Frost
import com.moriartystudio.moriarty.ui.theme.Indigo
import com.moriartystudio.moriarty.ui.theme.IndigoGradient
import com.moriartystudio.moriarty.ui.theme.IndigoLight
import com.moriartystudio.moriarty.ui.theme.Lavender
import com.moriartystudio.moriarty.ui.theme.Night
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
                CategoryGrid(onAllLevels)
                state.currentPuzzle?.let { UpNextCard(it.level, it.title, it.prompt, onContinue) }
                RecentSolves(state.nearbyLevels, onLevel, onAllLevels)
                DailyCard(state.dailyReset, onDaily)
            }
            Spacer(Modifier.height(0.dp))
        }
    }
}

@Composable
private fun HomeHeader(state: HomeState, onProfile: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier.size(46.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.25f)).clickable(onClick = onProfile),
            contentAlignment = Alignment.Center,
        ) { Text("M", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 19.sp) }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text("Welcome back!", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodyMedium)
            Text("Moriarty", color = Color.White, style = MaterialTheme.typography.titleLarge)
        }
        StatPill {
            Icon(Icons.Outlined.LocalFireDepartment, null, tint = Color(0xFFE77C22), modifier = Modifier.size(17.dp))
            Spacer(Modifier.width(4.dp))
            Text(state.streak.toString(), style = MaterialTheme.typography.labelLarge, color = Night)
        }
        Spacer(Modifier.width(8.dp))
        StatPill {
            Box(Modifier.size(14.dp).background(Gold, CircleShape))
            Spacer(Modifier.width(5.dp))
            Text(state.solvedCount.toString(), style = MaterialTheme.typography.labelLarge, color = Night)
        }
    }
}

@Composable
private fun StatPill(content: @Composable () -> Unit) {
    Row(
        Modifier.background(Color.White, RoundedCornerShape(50)).padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) { content() }
}

/** Frosted-glass card: translucent white over the gradient with a soft border. */
@Composable
private fun HeroCard(state: HomeState, onContinue: () -> Unit) {
    val next = state.currentLevel?.number ?: 1
    val progress = if (state.totalLevels == 0) 0f else state.solvedCount / state.totalLevels.toFloat()
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.linearGradient(listOf(Color.White.copy(alpha = 0.30f), Color.White.copy(alpha = 0.10f))))
            .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(28.dp)),
    ) {
        Box(Modifier.align(Alignment.CenterEnd).padding(end = 6.dp).size(width = 150.dp, height = 140.dp)) { CoinStack() }
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Puzzles solved", color = Color.White.copy(alpha = 0.85f), style = MaterialTheme.typography.titleMedium)
            Text(state.solvedCount.toString(), color = Color.White, fontSize = 48.sp, fontWeight = FontWeight.Bold, lineHeight = 54.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(14.dp).background(Gold, CircleShape))
                Spacer(Modifier.width(7.dp))
                Text("of ${state.totalLevels} levels", color = Color.White.copy(alpha = 0.9f), style = MaterialTheme.typography.bodyMedium)
            }
            Box(Modifier.width(150.dp).height(6.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.25f))) {
                Box(Modifier.fillMaxWidth(progress).height(6.dp).background(Gold, CircleShape))
            }
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier
                    .background(Color.White, RoundedCornerShape(50))
                    .clip(RoundedCornerShape(50))
                    .clickable(onClick = onContinue)
                    .padding(horizontal = 18.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Solve level $next", color = Indigo, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Outlined.ArrowForward, null, tint = Indigo, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, action: String? = null, onAction: (() -> Unit)? = null) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(title, style = MaterialTheme.typography.titleLarge, color = Night, modifier = Modifier.weight(1f))
        if (action != null && onAction != null) {
            Text(
                action,
                style = MaterialTheme.typography.labelLarge,
                color = Indigo,
                modifier = Modifier.clip(RoundedCornerShape(10.dp)).clickable(onClick = onAction).padding(8.dp),
            )
        }
    }
}

@Composable
private fun CategoryGrid(onClick: () -> Unit) {
    val tiles = listOf(
        PuzzleCategory.Pattern to Icons.Outlined.GridView,
        PuzzleCategory.Spatial to Icons.Outlined.ViewInAr,
        PuzzleCategory.Deduction to Icons.Outlined.Search,
        PuzzleCategory.Logic to Icons.Outlined.Extension,
    )
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader("Explore")
        tiles.chunked(2).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { (category, icon) -> CategoryTile(category.label, icon, onClick, Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun CategoryTile(label: String, icon: ImageVector, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.shadow(6.dp, RoundedCornerShape(22.dp), ambientColor = Indigo.copy(alpha = 0.25f), spotColor = Indigo.copy(alpha = 0.25f)).clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Frost),
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(42.dp).background(Frost, CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = Indigo, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Text(label, style = MaterialTheme.typography.titleMedium, color = Night)
        }
    }
}

@Composable
private fun UpNextCard(level: Int, title: String, prompt: String, onClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader("Up next")
        Surface(
            modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
            shape = RoundedCornerShape(24.dp),
            color = Frost.copy(alpha = 0.55f),
        ) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Level $level · $title", style = MaterialTheme.typography.titleMedium, color = Night)
                    Text(prompt, style = MaterialTheme.typography.bodyMedium, color = Night.copy(alpha = 0.6f))
                }
                Spacer(Modifier.width(12.dp))
                Box(Modifier.size(44.dp).background(IndigoGradient, CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowForward, "Start", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
private fun RecentSolves(levels: List<Level>, onLevel: (Int) -> Unit, onAllLevels: () -> Unit) {
    val solved = levels.filter { it.isSolved }.sortedByDescending { it.number }
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        SectionHeader("Recent solves", "View all", onAllLevels)
        if (solved.isEmpty()) {
            Text("Solve your first puzzle and it will show up here.", style = MaterialTheme.typography.bodyMedium, color = Night.copy(alpha = 0.55f), modifier = Modifier.padding(vertical = 6.dp))
        }
        solved.forEach { level ->
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).clickable { onLevel(level.number) }.padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.size(40.dp).background(Frost, CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.Check, null, tint = Indigo, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("Level ${level.number} · ${level.title}", style = MaterialTheme.typography.titleMedium, color = Night, maxLines = 1)
                    Text(level.category.label, style = MaterialTheme.typography.bodyMedium, color = Night.copy(alpha = 0.5f))
                }
                level.bestTimeSeconds?.let {
                    Text("%d:%02d".format(it / 60, it % 60), style = MaterialTheme.typography.titleMedium, color = Indigo)
                }
            }
        }
    }
}

@Composable
private fun DailyCard(reset: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        color = Frost.copy(alpha = 0.55f),
    ) {
        Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
            SmallGeometryThumbnail(Modifier.size(54.dp), accent = Lavender)
            Spacer(Modifier.width(13.dp))
            Column(Modifier.weight(1f)) {
                Text("Daily challenge", style = MaterialTheme.typography.titleMedium, color = Night)
                Text("One puzzle. One attempt.", style = MaterialTheme.typography.bodyMedium, color = Night.copy(alpha = 0.55f))
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Resets in", style = MaterialTheme.typography.labelMedium, color = Night.copy(alpha = 0.55f))
                Text(reset, style = MaterialTheme.typography.titleMedium, color = Indigo)
            }
            Icon(Icons.AutoMirrored.Outlined.ArrowForward, null, tint = Indigo, modifier = Modifier.padding(start = 8.dp).size(20.dp))
        }
    }
}
