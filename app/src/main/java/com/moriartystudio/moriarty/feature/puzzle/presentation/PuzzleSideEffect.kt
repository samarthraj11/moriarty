package com.moriartystudio.moriarty.feature.puzzle.presentation

sealed interface PuzzleSideEffect { data class NextLevel(val level: Int) : PuzzleSideEffect; data object FinishJourney : PuzzleSideEffect; data object GoBack : PuzzleSideEffect }
