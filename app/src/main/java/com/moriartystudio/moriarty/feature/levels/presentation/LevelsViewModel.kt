package com.moriartystudio.moriarty.feature.levels.presentation

import androidx.lifecycle.ViewModel
import com.moriartystudio.moriarty.core.model.PuzzleCategory
import com.moriartystudio.moriarty.domain.repository.GameRepository
import org.orbitmvi.orbit.OrbitContainerHost
import org.orbitmvi.orbit.viewmodel.orbitContainer

class LevelsViewModel(repository: GameRepository) : ViewModel(), OrbitContainerHost<LevelsState, LevelsState, LevelsSideEffect> {
    override val container = orbitContainer<LevelsState, LevelsSideEffect>(LevelsState(repository.levels()))
    fun selectCategory(category: PuzzleCategory?) = intent { reduce { state.copy(selectedCategory = category) } }
    fun openLevel(level: Int, unlocked: Boolean) = intent { if (unlocked) postSideEffect(LevelsSideEffect.OpenPuzzle(level)) }
    fun goBack() = intent { postSideEffect(LevelsSideEffect.GoBack) }
}
