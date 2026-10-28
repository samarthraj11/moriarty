package com.moriartystudio.moriarty.feature.daily.presentation

import com.moriartystudio.moriarty.core.model.Puzzle

data class DailyState(
    val streak: Int = 0,
    val dateLabel: String = "Today",
    val puzzle: Puzzle? = null,
    val resetTime: String = "08:24",
    val playersToday: String = "12,481",
    val isAttempted: Boolean = false,
)
