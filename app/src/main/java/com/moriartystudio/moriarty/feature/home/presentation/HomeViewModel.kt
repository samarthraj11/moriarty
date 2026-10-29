package com.moriartystudio.moriarty.feature.home.presentation

import androidx.lifecycle.ViewModel
import com.moriartystudio.moriarty.domain.repository.GameRepository
import com.moriartystudio.moriarty.domain.progression.ProgressionRules
import org.orbitmvi.orbit.OrbitContainerHost
import org.orbitmvi.orbit.viewmodel.orbitContainer

class HomeViewModel(repository: GameRepository) : ViewModel(), OrbitContainerHost<HomeState, HomeState, HomeSideEffect> {
    private val initial = repository.levels()
    private val current = ProgressionRules.currentLevel(initial)
    override val container = orbitContainer<HomeState, HomeSideEffect>(HomeState(
        streak = repository.profile().streak,
        currentLevel = current,
        currentPuzzle = current?.let { repository.puzzle(it.number) },
        nearbyLevels = ProgressionRules.nearbyLevels(initial, current),
        solvedCount = initial.count { it.isSolved },
        totalLevels = initial.size,
    ))

    fun continuePuzzle() = intent { postSideEffect(HomeSideEffect.OpenPuzzle(state.currentLevel?.number ?: 1)) }
    fun openLevel(level: Int) = intent {
        if (state.nearbyLevels.firstOrNull { it.number == level }?.isUnlocked == true) {
            postSideEffect(HomeSideEffect.OpenPuzzle(level))
        }
    }
    fun openAllLevels() = intent { postSideEffect(HomeSideEffect.OpenAllLevels) }
    fun openDaily() = intent { postSideEffect(HomeSideEffect.OpenDaily) }
    fun openProfile() = intent { postSideEffect(HomeSideEffect.OpenProfile) }
}
