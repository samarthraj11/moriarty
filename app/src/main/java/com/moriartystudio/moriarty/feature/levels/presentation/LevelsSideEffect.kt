package com.moriartystudio.moriarty.feature.levels.presentation

sealed interface LevelsSideEffect { data class OpenPuzzle(val level: Int) : LevelsSideEffect; data object GoBack : LevelsSideEffect }
