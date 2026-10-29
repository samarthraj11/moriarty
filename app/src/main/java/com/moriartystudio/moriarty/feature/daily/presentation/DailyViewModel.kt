package com.moriartystudio.moriarty.feature.daily.presentation

import androidx.lifecycle.ViewModel
import com.moriartystudio.moriarty.domain.repository.GameRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import org.orbitmvi.orbit.OrbitContainerHost
import org.orbitmvi.orbit.viewmodel.orbitContainer

class DailyViewModel(repository: GameRepository) : ViewModel(), OrbitContainerHost<DailyState, DailyState, DailySideEffect> {
    override val container = orbitContainer<DailyState, DailySideEffect>(DailyState(
        streak = repository.profile().streak,
        dateLabel = LocalDate.now().format(DateTimeFormatter.ofPattern("d MMMM")),
        puzzle = repository.puzzle(1),
    ))
    fun start() = intent { postSideEffect(DailySideEffect.StartChallenge) }
}
