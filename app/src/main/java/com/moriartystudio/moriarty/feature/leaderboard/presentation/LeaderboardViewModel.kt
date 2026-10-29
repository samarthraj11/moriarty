package com.moriartystudio.moriarty.feature.leaderboard.presentation

import androidx.lifecycle.ViewModel
import com.moriartystudio.moriarty.domain.repository.GameRepository
import org.orbitmvi.orbit.OrbitContainerHost
import org.orbitmvi.orbit.viewmodel.orbitContainer

class LeaderboardViewModel(repository: GameRepository) : ViewModel(), OrbitContainerHost<LeaderboardState, LeaderboardState, LeaderboardSideEffect> {
    override val container = orbitContainer<LeaderboardState, LeaderboardSideEffect>(LeaderboardState(repository.leaderboard(), puzzleTitle = repository.puzzle(1).title))
    fun selectPeriod(period: LeaderboardPeriod) = intent { reduce { state.copy(selectedPeriod = period) } }
    fun openPlayer(name: String) = intent { postSideEffect(LeaderboardSideEffect.OpenPlayer(name)) }
}
