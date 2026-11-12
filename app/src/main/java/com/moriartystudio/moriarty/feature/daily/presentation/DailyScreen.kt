package com.moriartystudio.moriarty.feature.daily.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.EventBusy
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.moriartystudio.moriarty.app.navigation.RootDestination
import com.moriartystudio.moriarty.core.designsystem.MoriartyBottomBar
import com.moriartystudio.moriarty.core.designsystem.MoriartyPage
import com.moriartystudio.moriarty.core.designsystem.categoryIcon
import com.moriartystudio.moriarty.core.designsystem.rememberDailyCountdown
import com.moriartystudio.moriarty.domain.daily.DailyClock
import com.moriartystudio.moriarty.ui.theme.ErrorCoral
import com.moriartystudio.moriarty.ui.theme.Frost
import com.moriartystudio.moriarty.ui.theme.Gold
import com.moriartystudio.moriarty.ui.theme.Indigo
import com.moriartystudio.moriarty.ui.theme.InkMuted
import com.moriartystudio.moriarty.ui.theme.Night
import com.moriartystudio.moriarty.ui.theme.Paper
import com.moriartystudio.moriarty.ui.theme.SoftPeach
import com.moriartystudio.moriarty.ui.theme.categoryColor
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.delay
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

private val WeekdayLabels = listOf("M", "T", "W", "T", "F", "S", "S")

@Composable
fun DailyScreen(viewModel: DailyViewModel, onStart: (Int) -> Unit, onRoot: (RootDestination) -> Unit) {
    val state by viewModel.collectAsState()
    // Re-read whenever the screen is entered, and again just after midnight when the day rolls over.
    LaunchedEffect(Unit) {
        while (true) {
            viewModel.refresh()
            delay((DailyClock.secondsUntilReset(ZonedDateTime.now()) + 1) * 1_000)
        }
    }
    viewModel.collectSideEffect { if (it is DailySideEffect.StartChallenge) onStart(it.level) }
    DailyContent(state, onStart = viewModel::start, onRoot = onRoot)
}

@Composable
internal fun DailyContent(state: DailyState, onStart: () -> Unit, onRoot: (RootDestination) -> Unit) {
    MoriartyPage(
        title = if (state.streak == 0) "No streak yet" else "${state.streak}-day streak",
        subtitle = if (state.bestStreak > 0) "Best: ${state.bestStreak} ${if (state.bestStreak == 1) "day" else "days"}" else "Play today to start one",
        leading = { Icon(Icons.Outlined.LocalFireDepartment, null, tint = Gold, modifier = Modifier.size(38.dp)) },
        bottomBar = { MoriartyBottomBar(RootDestination.Daily, onRoot) },
    ) {
        Column(Modifier.padding(horizontal = 22.dp, vertical = 22.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            ResetPill(Modifier.align(Alignment.CenterHorizontally))
            if (state.lapsedYesterday) LapsedBanner(state)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(state.month.format(DateTimeFormatter.ofPattern("MMMM yyyy")), style = MaterialTheme.typography.titleMedium, color = Night, modifier = Modifier.weight(1f))
                Text("${state.playedThisMonth} of ${state.month.lengthOfMonth()} days played", style = MaterialTheme.typography.bodyMedium, color = InkMuted)
            }
            Calendar(state)
            Legend(state)
            TodayCard(state, onStart)
        }
    }
}

/** Time left until local midnight, when the daily resets. */
@Composable
private fun ResetPill(modifier: Modifier = Modifier) {
    val countdown = rememberDailyCountdown()
    Row(
        modifier.background(Frost.copy(alpha = 0.7f), RoundedCornerShape(50)).padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Outlined.Schedule, null, tint = Indigo, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text("Resets in $countdown", style = MaterialTheme.typography.labelLarge, color = Indigo)
    }
}

/** Shown when yesterday was missed and today isn't played yet: what lapsed, and what to do about it. */
@Composable
private fun LapsedBanner(state: DailyState) {
    val missed = state.today.minusDays(1)
    Row(
        Modifier.fillMaxWidth().background(SoftPeach, RoundedCornerShape(20.dp)).padding(16.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(Icons.Outlined.EventBusy, null, tint = ErrorCoral, modifier = Modifier.size(26.dp))
        Spacer(Modifier.width(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text("Streak lapsed", style = MaterialTheme.typography.titleMedium, color = ErrorCoral)
            Text(
                if (state.endedStreak > 0) {
                    "You missed ${missed.format(DateTimeFormatter.ofPattern("d MMMM"))}, which ended your ${state.endedStreak}-day streak. Play today to start a new one."
                } else {
                    "You missed ${missed.format(DateTimeFormatter.ofPattern("d MMMM"))}. Play today to start a streak."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = Night,
            )
        }
    }
}

@Composable
private fun Legend(state: DailyState) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
        LegendItem("Played", Modifier.background(Indigo, RoundedCornerShape(4.dp)))
        LegendItem(
            if (state.lapsedThisMonth > 0) "Lapsed (${state.lapsedThisMonth})" else "Lapsed",
            Modifier.background(SoftPeach, RoundedCornerShape(4.dp)).border(1.dp, ErrorCoral.copy(alpha = 0.5f), RoundedCornerShape(4.dp)),
        )
        LegendItem("Today", Modifier.border(2.dp, Indigo, RoundedCornerShape(4.dp)))
    }
}

@Composable
private fun LegendItem(label: String, swatch: Modifier) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(swatch.size(14.dp))
        Spacer(Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.labelMedium, color = InkMuted)
    }
}

@Composable
private fun Calendar(state: DailyState) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            WeekdayLabels.forEach {
                Text(it, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, style = MaterialTheme.typography.labelMedium, color = InkMuted)
            }
        }
        val leading = state.month.atDay(1).dayOfWeek.value - 1
        val cells: List<Int?> = List(leading) { null } + (1..state.month.lengthOfMonth()).toList()
        cells.chunked(7).forEach { week ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                week.forEach { day -> DayCell(state, day, Modifier.weight(1f)) }
                repeat(7 - week.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun DayCell(state: DailyState, day: Int?, modifier: Modifier) {
    if (day == null) {
        Spacer(modifier.aspectRatio(1f))
        return
    }
    val date = state.month.atDay(day)
    val played = date in state.playedDays
    val isToday = date == state.today
    val missed = !played && date < state.today
    val shape = RoundedCornerShape(12.dp)
    val base = modifier.aspectRatio(1f).clip(shape)
    when {
        played -> Box(base.background(Indigo), contentAlignment = Alignment.Center) { Icon(Icons.Outlined.Check, "Played", tint = Color.White, modifier = Modifier.size(18.dp)) }
        isToday -> Box(base.border(2.dp, Indigo, shape), contentAlignment = Alignment.Center) { DayNumber(day, Indigo, FontWeight.Bold) }
        missed -> Box(base.background(SoftPeach), contentAlignment = Alignment.Center) { DayNumber(day, ErrorCoral, FontWeight.Medium) }
        else -> Box(base.background(Paper), contentAlignment = Alignment.Center) { DayNumber(day, InkMuted.copy(alpha = 0.6f), FontWeight.Normal) }
    }
}

@Composable
private fun DayNumber(day: Int, color: Color, weight: FontWeight) {
    Text(day.toString(), style = MaterialTheme.typography.labelLarge, color = color, fontWeight = weight)
}

@Composable
private fun TodayCard(state: DailyState, onStart: () -> Unit) {
    val color = categoryColor(state.category)
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Today's puzzle", style = MaterialTheme.typography.titleMedium, color = Night, modifier = Modifier.weight(1f))
            Row(
                Modifier.background(color.copy(alpha = 0.12f), RoundedCornerShape(50)).padding(horizontal = 11.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(categoryIcon(state.category), null, tint = color, modifier = Modifier.size(15.dp))
                Spacer(Modifier.width(5.dp))
                Text(state.category.label, style = MaterialTheme.typography.labelMedium, color = color)
            }
        }
        if (state.playedToday) {
            Row(
                Modifier.fillMaxWidth().background(Frost.copy(alpha = 0.7f), RoundedCornerShape(20.dp)).padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.size(36.dp).background(Indigo, CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.Check, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("You've played today", style = MaterialTheme.typography.titleMedium, color = Night)
                    Text("Come back after midnight for a new one.", style = MaterialTheme.typography.bodyMedium, color = InkMuted)
                }
            }
        } else {
            Row(
                Modifier.fillMaxWidth().height(56.dp).clip(RoundedCornerShape(28.dp)).background(Indigo).clickable(onClick = onStart),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text("Start today's puzzle", color = Color.White, style = MaterialTheme.typography.titleMedium)
            }
            Text(
                "First answer scores: +10 right, −5 wrong",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = InkMuted,
            )
        }
    }
}
