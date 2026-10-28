package com.moriartystudio.moriarty.feature.puzzle.presentation

import androidx.lifecycle.ViewModel
import com.moriartystudio.moriarty.domain.repository.GameRepository
import org.orbitmvi.orbit.OrbitContainerHost
import org.orbitmvi.orbit.viewmodel.orbitContainer

class PuzzleViewModel(level: Int, repository: GameRepository) : ViewModel(), OrbitContainerHost<PuzzleState, PuzzleState, PuzzleSideEffect> {
    override val container = orbitContainer<PuzzleState, PuzzleSideEffect>(PuzzleState(puzzle = repository.puzzle(level)))
    fun select(answer: String) = intent { reduce { state.copy(selectedAnswer = answer, status = AnswerStatus.Selected) } }
    fun check() = intent {
        val puzzle = state.puzzle ?: return@intent
        if (state.selectedAnswer == puzzle.correctOption) postSideEffect(PuzzleSideEffect.OpenSolution(puzzle.level, state.elapsedSeconds))
        else reduce { state.copy(status = AnswerStatus.Incorrect, elapsedSeconds = state.elapsedSeconds + 10) }
    }
    fun toggleHint() = intent { reduce { state.copy(showHint = !state.showHint) } }
    fun goBack() = intent { postSideEffect(PuzzleSideEffect.GoBack) }
}
