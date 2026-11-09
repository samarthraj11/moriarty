package com.moriartystudio.moriarty.feature.puzzle.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moriartystudio.moriarty.core.model.PuzzleProgress
import com.moriartystudio.moriarty.domain.repository.GameRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.OrbitContainerHost
import org.orbitmvi.orbit.viewmodel.orbitContainer

class PuzzleViewModel(level: Int, private val repository: GameRepository) : ViewModel(), OrbitContainerHost<PuzzleState, PuzzleState, PuzzleSideEffect> {
    override val container = orbitContainer<PuzzleState, PuzzleSideEffect>(
        repository.puzzleProgress(level).let { saved ->
            PuzzleState(puzzle = repository.puzzle(level), elapsedSeconds = saved.elapsedSeconds, hintUsed = saved.hintUsed)
        },
    )

    init {
        viewModelScope.launch {
            while (true) {
                delay(1_000)
                intent {
                    if (state.status != AnswerStatus.Correct && state.status != AnswerStatus.Skipped) reduce { state.copy(elapsedSeconds = state.elapsedSeconds + 1) }
                }
            }
        }
    }

    fun select(answer: String) = intent {
        val puzzle = state.puzzle ?: return@intent
        if (state.status == AnswerStatus.Correct || answer in state.wrongOptions) return@intent
        if (answer == puzzle.correctOption) {
            val seconds = state.elapsedSeconds
            Log.d(TAG, "Level ${puzzle.level} solved in ${seconds}s")
            repository.completeLevel(puzzle.level, seconds)
            repository.clearPuzzleProgress(puzzle.level)
            reduce { state.copy(status = AnswerStatus.Correct, correctAnswer = answer) }
            delay(SUCCESS_DELAY_MS)
            postSideEffect(if (puzzle.level < repository.levels().size) PuzzleSideEffect.NextLevel(puzzle.level + 1) else PuzzleSideEffect.FinishJourney)
        } else {
            reduce { state.copy(status = AnswerStatus.Incorrect, elapsedSeconds = state.elapsedSeconds + 10, wrongOptions = state.wrongOptions + answer) }
        }
    }
    fun skip() = intent {
        val puzzle = state.puzzle ?: return@intent
        if (state.status == AnswerStatus.Correct || state.status == AnswerStatus.Skipped) return@intent
        Log.d(TAG, "Level ${puzzle.level} skipped after ${state.elapsedSeconds}s")
        repository.skipLevel(puzzle.level)
        reduce { state.copy(status = AnswerStatus.Skipped) }
        postSideEffect(if (puzzle.level < repository.levels().size) PuzzleSideEffect.NextLevel(puzzle.level + 1) else PuzzleSideEffect.FinishJourney)
    }
    fun toggleHint() = intent {
        val firstUse = !state.showHint && !state.hintUsed
        reduce { state.copy(showHint = !state.showHint, hintUsed = state.hintUsed || firstUse, elapsedSeconds = state.elapsedSeconds + if (firstUse) 20 else 0) }
    }
    fun goBack() = intent { postSideEffect(PuzzleSideEffect.GoBack) }

    // Leaving mid-puzzle keeps the clock where it was; a solved puzzle starts fresh next time.
    override fun onCleared() {
        val current = container.stateFlow.value
        val level = current.puzzle?.level
        if (level != null && current.status != AnswerStatus.Correct && current.status != AnswerStatus.Skipped) {
            repository.savePuzzleProgress(level, PuzzleProgress(current.elapsedSeconds, current.hintUsed))
        }
        super.onCleared()
    }

    private companion object {
        const val TAG = "Puzzle"
        const val SUCCESS_DELAY_MS = 800L
    }
}
