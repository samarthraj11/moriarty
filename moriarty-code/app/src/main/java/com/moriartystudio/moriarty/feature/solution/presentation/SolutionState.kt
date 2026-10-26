package com.moriartystudio.moriarty.feature.solution.presentation

import com.moriartystudio.moriarty.core.model.Puzzle

data class SolutionState(
    val level: Int = 1,
    val seconds: Int = 0,
    val puzzle: Puzzle? = null,
    val hasNextLevel: Boolean = true,
)
