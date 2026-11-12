package com.moriartystudio.moriarty.feature.daily.presentation

import androidx.lifecycle.ViewModel
import com.moriartystudio.moriarty.domain.daily.DailyStreak
import com.moriartystudio.moriarty.domain.repository.GameRepository
import java.time.LocalDate
import org.orbitmvi.orbit.OrbitContainerHost
import org.orbitmvi.orbit.viewmodel.orbitContainer

class DailyViewModel(private val repository: GameRepository) : ViewModel(), OrbitContainerHost<DailyState, DailyState, DailySideEffect> {
    override val container = orbitContainer<DailyState, DailySideEffect>(buildState())

    private fun buildState(): DailyState {
        val today = LocalDate.now()
        val played = repository.dailyPlayedDays()
        val puzzle = repository.dailyPuzzle(today)
        return DailyState(
            today = today,
            streak = DailyStreak.current(played, today),
            bestStreak = DailyStreak.best(played),
            playedDays = played,
            startDate = repository.dailyStartDate(today),
            puzzleLevel = puzzle.level,
            category = puzzle.category,
        )
    }

    // Re-read when the screen comes back (after playing) and after midnight.
    fun refresh() = intent { reduce { buildState() } }

    fun start() = intent {
        if (!state.playedToday) postSideEffect(DailySideEffect.StartChallenge(state.puzzleLevel))
    }
}
