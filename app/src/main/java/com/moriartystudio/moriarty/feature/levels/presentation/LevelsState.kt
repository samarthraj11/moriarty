package com.moriartystudio.moriarty.feature.levels.presentation

import com.moriartystudio.moriarty.core.model.Level
import com.moriartystudio.moriarty.core.model.PuzzleCategory

data class LevelsState(val levels: List<Level> = emptyList(), val selectedCategory: PuzzleCategory? = null) {
    val visibleLevels get() = selectedCategory?.let { category -> levels.filter { it.category == category } } ?: levels
    val solvedCount get() = levels.count(Level::isSolved)
    val currentLevelNumber get() = levels.firstOrNull { it.isUnlocked && !it.isSolved }?.number
}
