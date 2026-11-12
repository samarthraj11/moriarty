package com.moriartystudio.moriarty.feature.home.presentation

import androidx.lifecycle.ViewModel
import com.moriartystudio.moriarty.core.model.PuzzleCategory
import com.moriartystudio.moriarty.domain.repository.GameRepository
import com.moriartystudio.moriarty.domain.progression.ProgressionRules
import org.orbitmvi.orbit.OrbitContainerHost
import org.orbitmvi.orbit.viewmodel.orbitContainer

class HomeViewModel(private val repository: GameRepository) : ViewModel(), OrbitContainerHost<HomeState, HomeState, HomeSideEffect> {
    override val container = orbitContainer<HomeState, HomeSideEffect>(buildState())

    private fun buildState(): HomeState {
        val levels = repository.levels()
        val current = ProgressionRules.currentLevel(levels)
        return HomeState(
            streak = repository.profile().streak,
            currentLevel = current,
            currentPuzzle = current?.let { repository.puzzle(it.number) },
            nearbyLevels = ProgressionRules.nearbyLevels(levels, current),
            solvedCount = levels.count { it.isSolved },
            totalLevels = levels.size,
            points = repository.totalPoints(),
            categoryProgress = PuzzleCategory.entries.map { category ->
                val inCategory = levels.filter { it.category == category }
                CategoryProgress(category, inCategory.count { it.isSolved }, inCategory.size)
            },
        )
    }

    // Home stays on the back stack under a puzzle, so re-read progress when it comes back.
    fun refresh() = intent { reduce { buildState() } }
    fun continuePuzzle() = intent { postSideEffect(HomeSideEffect.OpenPuzzle(state.currentLevel?.number ?: 1)) }
    fun openLevel(level: Int) = intent {
        if (state.nearbyLevels.firstOrNull { it.number == level }?.isUnlocked == true) {
            postSideEffect(HomeSideEffect.OpenPuzzle(level))
        }
    }
    fun openCategory(category: PuzzleCategory) = intent { postSideEffect(HomeSideEffect.OpenCategory(category)) }
    fun openAllLevels() = intent { postSideEffect(HomeSideEffect.OpenAllLevels) }
    fun openDaily() = intent { postSideEffect(HomeSideEffect.OpenDaily) }
    fun openProfile() = intent { postSideEffect(HomeSideEffect.OpenProfile) }
}
