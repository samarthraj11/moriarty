package com.moriartystudio.moriarty.feature.progress.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moriartystudio.moriarty.app.navigation.RootDestination
import com.moriartystudio.moriarty.core.designsystem.MoriartyBottomBar
import com.moriartystudio.moriarty.core.designsystem.MoriartyPage
import com.moriartystudio.moriarty.core.designsystem.categoryIcon
import com.moriartystudio.moriarty.core.model.PuzzleCategory
import com.moriartystudio.moriarty.domain.progress.Badge
import com.moriartystudio.moriarty.domain.progress.DayActivity
import com.moriartystudio.moriarty.domain.progress.DetectiveRank
import com.moriartystudio.moriarty.domain.progress.ProgressSummary
import com.moriartystudio.moriarty.domain.progress.RankProgress
import com.moriartystudio.moriarty.domain.progress.SectionProgress
import com.moriartystudio.moriarty.ui.theme.*
import org.orbitmvi.orbit.compose.collectAsState
import java.time.format.TextStyle as DayStyle
import java.util.Locale

// The dossier is a deep ink-indigo so it reads as a separate object from the indigo header above it.
private val DossierInk = Color(0xFF171A45)

@Composable
fun ProgressScreen(viewModel: ProgressViewModel, onRoot: (RootDestination) -> Unit) {
    val state by viewModel.collectAsState()
    LaunchedEffect(Unit) { viewModel.refresh() }
    MoriartyPage(title = "Progress", subtitle = "Your case record", bottomBar = { MoriartyBottomBar(RootDestination.Progress, onRoot) }) {
        ProgressContent(state)
    }
}

@Composable
internal fun ProgressContent(state: ProgressState) {
    Column(Modifier.padding(horizontal = 20.dp, vertical = 20.dp), verticalArrangement = Arrangement.spacedBy(30.dp)) {
        RankDossier(state.rank, state.summary.points)
        CaseTally(state.summary)
        Casebooks(state.summary)
        WeekSection(state.summary)
        Ladder(state.rank.rank)
        Seals(state.unlocked)
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun SectionHeading(title: String, trailing: String? = null) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
        Text(title, style = MaterialTheme.typography.headlineMedium.copy(fontSize = 22.sp), color = Ink, modifier = Modifier.weight(1f))
        if (trailing != null) Text(trailing, style = MaterialTheme.typography.labelLarge, color = InkMuted)
    }
}

// ───────────────────────── Rank dossier ─────────────────────────

@Composable
private fun RankDossier(rank: RankProgress, points: Int) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(DossierInk)
            .drawBehind {
                // Faint evidence-board dot grid.
                val step = 18.dp.toPx()
                var y = step / 2
                while (y < size.height) {
                    var x = step / 2
                    while (x < size.width) { drawCircle(Color.White.copy(alpha = 0.07f), 1.2.dp.toPx(), Offset(x, y)); x += step }
                    y += step
                }
            }
            .padding(start = 22.dp, end = 18.dp, top = 22.dp, bottom = 20.dp),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f)) {
                Text("RANK ${rank.rank.numeral} OF V", style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.6.sp), color = Gold)
                Spacer(Modifier.height(6.dp))
                Text(rank.rank.title, style = MaterialTheme.typography.displaySmall.copy(fontSize = 34.sp, lineHeight = 38.sp), color = Color.White)
                Spacer(Modifier.height(4.dp))
                Text(rank.rank.motto, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.7f))
            }
            RankStamp(rank.rank.numeral, Modifier.padding(top = 2.dp))
        }
        Spacer(Modifier.height(20.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text("$points", style = MaterialTheme.typography.displaySmall.copy(fontSize = 46.sp, lineHeight = 48.sp), color = Color.White)
            Spacer(Modifier.width(8.dp))
            Text("points", style = MaterialTheme.typography.titleMedium, color = Color.White.copy(alpha = 0.65f), modifier = Modifier.padding(bottom = 7.dp))
        }
        Spacer(Modifier.height(14.dp))
        TapeMeasure(points)
        Spacer(Modifier.height(10.dp))
        Text(
            rank.next?.let { "${rank.pointsToNext} points to ${it.title}" } ?: "You've reached the top of the ladder",
            style = MaterialTheme.typography.labelLarge,
            color = GoldLight,
        )
    }
}

/** Ink-stamp seal with the rank's roman numeral, tilted like it was pressed onto the page. */
@Composable
private fun RankStamp(numeral: String, modifier: Modifier = Modifier) {
    Box(modifier.size(80.dp).rotate(-12f), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val r = size.minDimension / 2
            drawCircle(Gold, r - 1.5.dp.toPx(), style = Stroke(2.5.dp.toPx()))
            drawCircle(Gold.copy(alpha = 0.7f), r - 8.dp.toPx(), style = Stroke(1.4.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 4.dp.toPx()))))
        }
        Text(numeral, style = MaterialTheme.typography.headlineLarge.copy(fontSize = if (numeral.length > 2) 22.sp else 28.sp), color = Gold)
    }
}

/** A ruler across the whole ladder: a tick per rank, a gold fill to the player's points and a pin on the end. */
@Composable
private fun TapeMeasure(points: Int) {
    val top = DetectiveRank.entries.last().threshold
    val measurer = rememberTextMeasurer()
    val labelStyle = TextStyle(fontFamily = OutfitFamily, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
    Canvas(Modifier.fillMaxWidth().height(44.dp)) {
        val pad = 8.dp.toPx()
        val w = size.width - pad * 2
        fun x(v: Int) = pad + w * (v.coerceAtMost(top).toFloat() / top)
        val lineY = 12.dp.toPx()
        drawLine(Color.White.copy(alpha = 0.22f), Offset(pad, lineY), Offset(pad + w, lineY), 4.dp.toPx(), StrokeCap.Round)
        drawLine(Gold, Offset(pad, lineY), Offset(x(points.coerceAtLeast(0)), lineY), 4.dp.toPx(), StrokeCap.Round)
        var minor = 0
        while (minor <= top) {
            if (DetectiveRank.entries.none { it.threshold == minor }) drawLine(Color.White.copy(alpha = 0.28f), Offset(x(minor), lineY + 6.dp.toPx()), Offset(x(minor), lineY + 10.dp.toPx()), 1.dp.toPx())
            minor += 30
        }
        DetectiveRank.entries.forEach { rank ->
            val tx = x(rank.threshold)
            val reached = points >= rank.threshold
            drawLine(if (reached) Gold else Color.White.copy(alpha = 0.6f), Offset(tx, lineY + 4.dp.toPx()), Offset(tx, lineY + 16.dp.toPx()), 2.dp.toPx(), StrokeCap.Round)
            val label = measurer.measure(rank.numeral, labelStyle)
            val lx = (tx - label.size.width / 2f).coerceIn(0f, size.width - label.size.width)
            drawText(label, if (reached) GoldLight else Color.White.copy(alpha = 0.55f), Offset(lx, lineY + 20.dp.toPx()))
        }
        val pin = Offset(x(points.coerceAtLeast(0)), lineY)
        drawCircle(DossierInk, 9.dp.toPx(), pin)
        drawCircle(Gold, 7.dp.toPx(), pin)
        drawCircle(DossierInk, 2.5.dp.toPx(), pin)
    }
}

// ───────────────────────── Case tally ─────────────────────────

@Composable
private fun CaseTally(summary: ProgressSummary) {
    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min), verticalAlignment = Alignment.CenterVertically) {
        TallyItem("${summary.solved}", "/${summary.totalPuzzles}", "Solved", Modifier.weight(1f))
        TallyDivider()
        TallyItem(if (summary.answered == 0) "—" else "${summary.accuracy}", if (summary.answered == 0) "" else "%", "Accuracy", Modifier.weight(1f))
        TallyDivider()
        TallyItem("${summary.bestCorrectRun}", "", "Best run", Modifier.weight(1f))
    }
}

@Composable
private fun TallyDivider() = Box(Modifier.width(1.dp).fillMaxHeight().background(Line))

@Composable
private fun TallyItem(value: String, suffix: String, label: String, modifier: Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, style = MaterialTheme.typography.displaySmall.copy(fontSize = 32.sp, lineHeight = 36.sp), color = Ink)
            if (suffix.isNotEmpty()) Text(suffix, style = MaterialTheme.typography.titleMedium, color = InkMuted, modifier = Modifier.padding(bottom = 5.dp, start = 1.dp))
        }
        Text(label, style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 0.8.sp), color = InkMuted)
    }
}

// ───────────────────────── Casebooks ─────────────────────────

@Composable
private fun Casebooks(summary: ProgressSummary) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionHeading("Casebooks", "solved · accuracy")
        summary.sections.forEach { Casebook(it) }
        summary.weakestSection?.let { weak ->
            Row(
                Modifier.fillMaxWidth().background(Paper, RoundedCornerShape(16.dp)).padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.size(8.dp).background(categoryColor(weak.category), CircleShape))
                Spacer(Modifier.width(10.dp))
                Text("${weak.category.label} is your toughest casebook. Worth another look.", style = MaterialTheme.typography.bodyMedium, color = Ink)
            }
        }
    }
}

// Gold is too pale to read as text on white.
private fun textColor(category: PuzzleCategory): Color = if (category == PuzzleCategory.Pattern) GoldDark else categoryColor(category)

@Composable
private fun Casebook(section: SectionProgress) {
    val color = categoryColor(section.category)
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(46.dp).background(color.copy(alpha = 0.16f), RoundedCornerShape(15.dp)), contentAlignment = Alignment.Center) {
            Icon(categoryIcon(section.category), null, tint = textColor(section.category), modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(section.category.label, style = MaterialTheme.typography.headlineMedium.copy(fontSize = 18.sp, lineHeight = 22.sp), color = Ink, modifier = Modifier.weight(1f))
                Text("${section.solved}/${section.total}", style = MaterialTheme.typography.labelLarge, color = InkMuted)
                Text(" · ", color = InkMuted)
                Text(section.accuracy?.let { "$it%" } ?: "—", style = MaterialTheme.typography.titleMedium, color = textColor(section.category))
            }
            Spacer(Modifier.height(8.dp))
            SegmentTrack(section.total, section.solved, color)
        }
    }
}

/** One little segment per puzzle: a full row reads as a finished casebook. */
@Composable
private fun SegmentTrack(total: Int, filled: Int, color: Color) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        repeat(total.coerceAtLeast(1)) { index ->
            Box(Modifier.weight(1f).height(9.dp).background(if (index < filled) color else color.copy(alpha = 0.16f), RoundedCornerShape(3.dp)))
        }
    }
}

// ───────────────────────── This week ─────────────────────────

@Composable
private fun WeekSection(summary: ProgressSummary) {
    val goal = ProgressSummary.WEEKLY_GOAL
    val played = summary.playedThisWeek
    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
        SectionHeading("This week")
        Row(verticalAlignment = Alignment.CenterVertically) {
            GoalRing(played, goal)
            Spacer(Modifier.width(16.dp))
            Column {
                Text("Play $goal puzzles", style = MaterialTheme.typography.titleLarge, color = Ink)
                Text(if (played >= goal) "Goal met. Nicely done." else "${goal - played} to go · resets Monday", style = MaterialTheme.typography.bodyMedium, color = InkMuted)
            }
        }
        DayBars(summary.lastSevenDays)
    }
}

@Composable
private fun GoalRing(played: Int, goal: Int) {
    val measurer = rememberTextMeasurer()
    val style = TextStyle(fontFamily = BitterFamily, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Ink)
    Canvas(Modifier.size(64.dp)) {
        val stroke = 7.dp.toPx()
        val inset = stroke / 2
        val arc = Size(size.width - stroke, size.height - stroke)
        drawArc(Frost, 0f, 360f, false, Offset(inset, inset), arc, style = Stroke(stroke))
        drawArc(Indigo, -90f, 360f * (played.toFloat() / goal).coerceIn(0f, 1f), false, Offset(inset, inset), arc, style = Stroke(stroke, cap = StrokeCap.Round))
        val text = measurer.measure("$played", style)
        drawText(text, topLeft = Offset((size.width - text.size.width) / 2, (size.height - text.size.height) / 2))
    }
}

@Composable
private fun DayBars(days: List<DayActivity>) {
    val maxPoints = days.maxOfOrNull { it.points }?.coerceAtLeast(10) ?: 10
    Row(Modifier.fillMaxWidth().height(124.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Bottom) {
        days.forEachIndexed { index, day ->
            val isToday = index == days.lastIndex
            val barHeight = if (day.points <= 0) 6.dp else (14 + 56f * day.points / maxPoints).dp
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {
                if (day.points > 0) Text("${day.points}", style = MaterialTheme.typography.labelMedium, color = if (isToday) Indigo else InkMuted)
                Spacer(Modifier.height(4.dp))
                Box(Modifier.fillMaxWidth().height(barHeight).background(if (day.points <= 0) Line else if (isToday) Indigo else Lavender, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 3.dp, bottomEnd = 3.dp)))
                Spacer(Modifier.height(6.dp))
                Text(day.date.dayOfWeek.getDisplayName(DayStyle.NARROW, Locale.getDefault()), style = MaterialTheme.typography.labelMedium, color = if (isToday) Indigo else InkMuted, fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium)
            }
        }
    }
}

// ───────────────────────── Ladder ─────────────────────────

@Composable
private fun Ladder(current: DetectiveRank) {
    Column {
        SectionHeading("The ladder")
        Spacer(Modifier.height(10.dp))
        DetectiveRank.entries.forEach { rank ->
            val isCurrent = rank == current
            val reached = rank.ordinal <= current.ordinal
            val last = rank.ordinal == DetectiveRank.entries.lastIndex
            Row(
                Modifier.fillMaxWidth().height(IntrinsicSize.Min).then(if (isCurrent) Modifier.background(Frost, RoundedCornerShape(18.dp)) else Modifier),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LadderNode(rank, reached, isCurrent, last, current)
                Column(Modifier.weight(1f).padding(vertical = 12.dp)) {
                    Text(rank.title, style = MaterialTheme.typography.headlineMedium.copy(fontSize = 18.sp, lineHeight = 22.sp), color = if (reached) Ink else InkMuted)
                    Text(rank.motto, style = MaterialTheme.typography.bodyMedium, color = InkMuted)
                }
                Text("${rank.threshold}", style = MaterialTheme.typography.titleMedium, color = if (isCurrent) Indigo else InkMuted, modifier = Modifier.padding(end = 14.dp))
            }
        }
    }
}

@Composable
private fun LadderNode(rank: DetectiveRank, reached: Boolean, isCurrent: Boolean, last: Boolean, current: DetectiveRank) {
    val first = rank.ordinal == 0
    Box(
        Modifier.width(48.dp).fillMaxHeight().drawBehind {
            val cx = size.width / 2
            fun segment(from: Float, to: Float, solid: Boolean) = drawLine(
                if (solid) Indigo else Lavender.copy(alpha = 0.6f), Offset(cx, from), Offset(cx, to), 3.dp.toPx(),
                pathEffect = if (solid) null else PathEffect.dashPathEffect(floatArrayOf(3.dp.toPx(), 5.dp.toPx())),
            )
            if (!first) segment(0f, size.height / 2, reached)
            if (!last) segment(size.height / 2, size.height, rank.ordinal < current.ordinal)
        },
        contentAlignment = Alignment.Center,
    ) {
        when {
            isCurrent -> Box(Modifier.size(30.dp).background(Color.White, CircleShape).border(3.dp, Gold, CircleShape).padding(5.dp).background(Indigo, CircleShape))
            reached -> Box(Modifier.size(22.dp).background(Indigo, CircleShape), contentAlignment = Alignment.Center) { Icon(Icons.Outlined.Check, null, tint = Color.White, modifier = Modifier.size(14.dp)) }
            else -> Box(Modifier.size(22.dp).background(Color.White, CircleShape).border(2.dp, Lavender, CircleShape), contentAlignment = Alignment.Center) { Icon(Icons.Outlined.Lock, null, tint = Lavender, modifier = Modifier.size(12.dp)) }
        }
    }
}

// ───────────────────────── Seals ─────────────────────────

private fun badgeIcon(badge: Badge): ImageVector = when (badge) {
    Badge.FirstCase -> Icons.Outlined.Flag
    Badge.SharpEye -> Icons.Outlined.Visibility
    Badge.OnARoll -> Icons.Outlined.Bolt
    Badge.Regular -> Icons.Outlined.CalendarMonth
    Badge.Devoted -> Icons.Outlined.LocalFireDepartment
    Badge.DailyFive -> Icons.Outlined.EventAvailable
    Badge.PatternMaster -> categoryIcon(PuzzleCategory.Pattern)
    Badge.SpatialMaster -> categoryIcon(PuzzleCategory.Spatial)
    Badge.DeductionMaster -> categoryIcon(PuzzleCategory.Deduction)
    Badge.LogicMaster -> categoryIcon(PuzzleCategory.Logic)
    Badge.Flawless -> Icons.Outlined.Diamond
    Badge.Century -> Icons.Outlined.Star
}

@Composable
private fun Seals(unlocked: Set<Badge>) {
    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
        SectionHeading("Seals", "${unlocked.size} of ${Badge.entries.size}")
        Badge.entries.chunked(3).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Top) {
                row.forEach { badge -> Seal(badge, badge in unlocked, Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun Seal(badge: Badge, earned: Boolean, modifier: Modifier) {
    val color = badge.category?.let(::categoryColor) ?: Gold
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(74.dp), contentAlignment = Alignment.Center) {
            Canvas(Modifier.fillMaxSize()) {
                val r = size.minDimension / 2
                val c = center
                if (earned) {
                    // Scalloped wax-seal edge: a ring of bumps around a solid disc.
                    val bump = 7.dp.toPx()
                    repeat(14) { i ->
                        val a = Math.toRadians(i * 360.0 / 14)
                        drawCircle(color, bump, Offset(c.x + ((r - bump) * Math.cos(a)).toFloat(), c.y + ((r - bump) * Math.sin(a)).toFloat()))
                    }
                    drawCircle(color, r - bump, c)
                    drawCircle(Color.White.copy(alpha = 0.5f), r - 14.dp.toPx(), c, style = Stroke(1.5.dp.toPx()))
                } else {
                    drawCircle(Lavender.copy(alpha = 0.7f), r - 3.dp.toPx(), c, style = Stroke(1.6.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(5.dp.toPx(), 5.dp.toPx()))))
                }
            }
            Icon(
                badgeIcon(badge), null,
                tint = when { !earned -> Lavender; badge.category == null -> Color(0xFF5B3A00); else -> Color.White },
                modifier = Modifier.size(28.dp),
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(badge.title, style = MaterialTheme.typography.labelLarge, color = if (earned) Ink else InkMuted, textAlign = TextAlign.Center)
        Text(badge.hint, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Normal, lineHeight = 14.sp), color = InkMuted.copy(alpha = if (earned) 0.8f else 0.65f), textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
    }
}
