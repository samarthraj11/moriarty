package com.moriartystudio.moriarty.core.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.moriartystudio.moriarty.domain.daily.DailyClock
import java.time.ZonedDateTime
import kotlinx.coroutines.delay

/** HH:MM:SS left until local midnight, when the daily challenge resets; ticks every second. */
@Composable
fun rememberDailyCountdown(): String {
    var secondsLeft by remember { mutableLongStateOf(DailyClock.secondsUntilReset(ZonedDateTime.now())) }
    LaunchedEffect(Unit) {
        while (true) {
            secondsLeft = DailyClock.secondsUntilReset(ZonedDateTime.now())
            delay(1_000)
        }
    }
    return DailyClock.format(secondsLeft)
}
