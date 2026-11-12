package com.moriartystudio.moriarty.feature.category.presentation

import androidx.lifecycle.ViewModel
import com.moriartystudio.moriarty.core.model.Level
import com.moriartystudio.moriarty.core.model.PuzzleCategory
import com.moriartystudio.moriarty.domain.repository.GameRepository
import com.moriartystudio.moriarty.domain.scoring.Scoring
import org.orbitmvi.orbit.OrbitContainerHost
import org.orbitmvi.orbit.viewmodel.orbitContainer

class CategoryViewModel(
    private val category: PuzzleCategory,
    private val repository: GameRepository,
) : ViewModel(), OrbitContainerHost<CategoryState, CategoryState, CategorySideEffect> {
    override val container = orbitContainer<CategoryState, CategorySideEffect>(buildState())

    private fun buildState(shimmerPending: Boolean = true): CategoryState {
        val all = repository.levels()
        val levels = all.filter { it.category == category }
        val answered = levels.filter { it.points != null }
        val firstTry = answered.count { it.points == Scoring.CORRECT_POINTS }
        return CategoryState(
            category = category,
            levels = levels,
            points = levels.sumOf { it.points ?: 0 },
            firstTryPercent = if (answered.isEmpty()) null else firstTry * 100 / answered.size,
            next = levels.firstOrNull { it.isUnlocked && !it.isSolved && !it.isSkipped }
                ?: levels.firstOrNull { it.isUnlocked && !it.isSolved },
            shimmerPending = shimmerPending,
        )
    }

    // The screen stays on the back stack under a puzzle, so re-read progress when it returns.
    fun refresh() = intent { reduce { buildState(state.shimmerPending) } }
    fun shimmerPlayed() = intent { reduce { state.copy(shimmerPending = false) } }
    fun openLevel(level: Level) = intent { if (level.isUnlocked) postSideEffect(CategorySideEffect.OpenPuzzle(level.number)) }
    fun play() = intent { state.next?.let { postSideEffect(CategorySideEffect.OpenPuzzle(it.number)) } }
    fun goBack() = intent { postSideEffect(CategorySideEffect.GoBack) }
}
