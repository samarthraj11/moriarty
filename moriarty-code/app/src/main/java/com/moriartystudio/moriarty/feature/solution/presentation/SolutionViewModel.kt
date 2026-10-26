package com.moriartystudio.moriarty.feature.solution.presentation

import androidx.lifecycle.ViewModel
import com.moriartystudio.moriarty.domain.repository.GameRepository
import org.orbitmvi.orbit.OrbitContainerHost
import org.orbitmvi.orbit.viewmodel.orbitContainer

class SolutionViewModel(level: Int, seconds: Int, repository: GameRepository) : ViewModel(), OrbitContainerHost<SolutionState, SolutionState, SolutionSideEffect> {
    override val container = orbitContainer<SolutionState, SolutionSideEffect>(SolutionState(level, seconds, repository.puzzle(level).explanation))
    fun next() = intent { postSideEffect(SolutionSideEffect.NextLevel(state.level + 1)) }
    fun leaderboard() = intent { postSideEffect(SolutionSideEffect.OpenLeaderboard) }
    fun share() = intent { postSideEffect(SolutionSideEffect.Share) }
}
