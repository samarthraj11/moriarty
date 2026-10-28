package com.moriartystudio.moriarty.feature.solution.presentation

sealed interface SolutionSideEffect {
    data class NextLevel(val level: Int) : SolutionSideEffect
    data object FinishJourney : SolutionSideEffect
    data object OpenLeaderboard : SolutionSideEffect
    data object Share : SolutionSideEffect
}
