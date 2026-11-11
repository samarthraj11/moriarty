package com.moriartystudio.moriarty.feature.puzzle.presentation

import com.moriartystudio.moriarty.core.model.PuzzleCategory

sealed interface PuzzleSideEffect { data class NextLevel(val level: Int) : PuzzleSideEffect; data class NextSection(val category: PuzzleCategory) : PuzzleSideEffect; data object FinishJourney : PuzzleSideEffect; data object GoBack : PuzzleSideEffect }
