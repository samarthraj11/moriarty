package com.moriartystudio.moriarty.feature.home.presentation

import com.moriartystudio.moriarty.core.model.Level

data class HomeState(
    val streak: Int = 4,
    val currentLevel: Level? = null,
    val nearbyLevels: List<Level> = emptyList(),
    val solvedCount: Int = 6,
    val totalLevels: Int = 100,
    val dailyReset: String = "08:24",
)
