package com.moriartystudio.moriarty.feature.puzzle.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moriartystudio.moriarty.domain.repository.GameRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.OrbitContainerHost
import org.orbitmvi.orbit.viewmodel.orbitContainer

class PuzzleViewModel(level: Int, private val repository: GameRepository) : ViewModel(), OrbitContainerHost<PuzzleState, PuzzleState, PuzzleSideEffect> {
    override val container = orbitContainer<PuzzleState, PuzzleSideEffect>(PuzzleState(puzzle = repository.puzzle(level)))

    init {
        viewModelScope.launch {
            while (true) {
                delay(1_000)
                intent {
                    if (state.status != AnswerStatus.Correct) reduce { state.copy(elapsedSeconds = state.elapsedSeconds + 1) }
                }
            }
        }
    }

    fun select(answer: String) = intent { reduce { state.copy(selectedAnswer = answer, status = AnswerStatus.Selected) } }
    fun check() = intent {
        val puzzle = state.puzzle ?: return@intent
        if (state.selectedAnswer == puzzle.correctOption) {
            repository.completeLevel(puzzle.level, state.elapsedSeconds)
            reduce { state.copy(status = AnswerStatus.Correct) }
            postSideEffect(PuzzleSideEffect.OpenSolution(puzzle.level, state.elapsedSeconds))
        } else {
            reduce { state.copy(status = AnswerStatus.Incorrect, elapsedSeconds = state.elapsedSeconds + 10, wrongAnswers = state.wrongAnswers + 1) }
        }
    }
    fun toggleHint() = intent {
        val firstUse = !state.showHint && !state.hintUsed
        reduce { state.copy(showHint = !state.showHint, hintUsed = state.hintUsed || firstUse, elapsedSeconds = state.elapsedSeconds + if (firstUse) 20 else 0) }
    }
    fun goBack() = intent { postSideEffect(PuzzleSideEffect.GoBack) }
}
