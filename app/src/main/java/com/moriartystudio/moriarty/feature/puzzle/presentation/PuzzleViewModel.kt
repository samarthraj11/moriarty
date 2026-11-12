package com.moriartystudio.moriarty.feature.puzzle.presentation

import java.time.LocalDate
import com.moriartystudio.moriarty.core.model.PuzzleCategory
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moriartystudio.moriarty.domain.repository.GameRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.OrbitContainerHost
import org.orbitmvi.orbit.viewmodel.orbitContainer

class PuzzleViewModel(level: Int, private val repository: GameRepository, private val daily: Boolean = false) : ViewModel(), OrbitContainerHost<PuzzleState, PuzzleState, PuzzleSideEffect> {
    override val container = orbitContainer<PuzzleState, PuzzleSideEffect>(
        PuzzleState(
            puzzle = repository.puzzle(level),
            totalPoints = repository.totalPoints(),
            isDaily = daily,
            categoryLevel = repository.levels().firstOrNull { it.number == level }?.categoryLevel ?: 1,
        ),
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
            val delta = repository.recordFirstAnswer(puzzle.level, correct = true)
            repository.completeLevel(puzzle.level)
            if (daily) repository.recordDailyPlayed(LocalDate.now())
            Log.d(TAG, "Level ${puzzle.level} solved in ${state.elapsedSeconds}s, $delta points")
            // Stay on the solved puzzle; the player moves on with Next (or plays it again with Retry).
            reduce { state.copy(status = AnswerStatus.Correct, correctAnswer = answer, lastDelta = delta, totalPoints = repository.totalPoints()) }
        } else {
            val delta = repository.recordFirstAnswer(puzzle.level, correct = false)
            reduce { state.copy(status = AnswerStatus.Incorrect, wrongOptions = state.wrongOptions + answer, lastDelta = delta, totalPoints = repository.totalPoints()) }
        }
    }
    fun skip() = intent {
        val puzzle = state.puzzle ?: return@intent
        if (state.status == AnswerStatus.Correct || state.status == AnswerStatus.Skipped) return@intent
        Log.d(TAG, "Level ${puzzle.level} skipped after ${state.elapsedSeconds}s")
        repository.skipLevel(puzzle.level)
        reduce { state.copy(status = AnswerStatus.Skipped) }
        postSideEffect(afterPuzzle(puzzle.level))
    }
    fun next() = intent {
        val puzzle = state.puzzle ?: return@intent
        if (state.status == AnswerStatus.Correct) postSideEffect(afterPuzzle(puzzle.level))
    }

    // Play the solved puzzle again: clear the answers, restart the timer. Points only ever count once.
    fun retry() = intent {
        if (state.status != AnswerStatus.Correct) return@intent
        reduce {
            state.copy(
                status = AnswerStatus.Unanswered,
                correctAnswer = null,
                wrongOptions = emptySet(),
                lastDelta = null,
                elapsedSeconds = 0,
                showHint = false,
            )
        }
    }

    fun toggleHint() = intent { reduce { state.copy(showHint = !state.showHint) } }
    fun goBack() = intent { postSideEffect(PuzzleSideEffect.GoBack) }

    // The daily puzzle returns to the calendar; any other puzzle carries on through the levels.
    private fun afterPuzzle(level: Int): PuzzleSideEffect = if (daily) PuzzleSideEffect.GoBack else nextEffect(level)

    // After a puzzle: carry on in the same section, then move to the next section that still has
    // something to solve, and only finish when nothing is left anywhere.
    private fun nextEffect(level: Int): PuzzleSideEffect {
        val levels = repository.levels()
        val category = levels.firstOrNull { it.number == level }?.category ?: return PuzzleSideEffect.FinishJourney
        val remaining = levels.filter { it.category == category && it.number != level && !it.isSolved }
        (remaining.firstOrNull { it.number > level } ?: remaining.firstOrNull())?.let { return PuzzleSideEffect.NextLevel(it.number) }
        val order = PuzzleCategory.entries
        val start = order.indexOf(category)
        val nextSection = (1 until order.size)
            .map { order[(start + it) % order.size] }
            .firstOrNull { candidate -> levels.any { it.category == candidate && !it.isSolved } }
        return if (nextSection != null) PuzzleSideEffect.NextSection(nextSection) else PuzzleSideEffect.FinishJourney
    }

    private companion object {
        const val TAG = "Puzzle"
    }
}
