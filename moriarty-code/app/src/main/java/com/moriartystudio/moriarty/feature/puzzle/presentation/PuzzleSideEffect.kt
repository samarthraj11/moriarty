package com.moriartystudio.moriarty.feature.puzzle.presentation

sealed interface PuzzleSideEffect { data class OpenSolution(val level: Int, val seconds: Int) : PuzzleSideEffect; data object GoBack : PuzzleSideEffect }
