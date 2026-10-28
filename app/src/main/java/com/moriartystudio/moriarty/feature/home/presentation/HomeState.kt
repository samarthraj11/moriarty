package com.moriartystudio.moriarty.feature.home.presentation

import com.moriartystudio.moriarty.core.model.Level
import com.moriartystudio.moriarty.core.model.Puzzle

data class HomeState(
    val streak: Int = 0,
    val currentLevel: Level? = null,
    val currentPuzzle: Puzzle? = null,
    val nearbyLevels: List<Level> = emptyList(),
    val solvedCount: Int = 0,
    val totalLevels: Int = 10,
    val dailyReset: String = "08:24",
)
