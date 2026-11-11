package com.moriartystudio.moriarty.feature.category.presentation

import com.moriartystudio.moriarty.core.model.Level
import com.moriartystudio.moriarty.core.model.PuzzleCategory

data class CategoryState(
    val category: PuzzleCategory,
    val levels: List<Level> = emptyList(),
    val points: Int = 0,
    /** Share of answered puzzles that were right on the first try, or null before any answer. */
    val firstTryPercent: Int? = null,
    /** First open puzzle in this category, if any. */
    val next: Level? = null,
) {
    val solved: Int get() = levels.count(Level::isSolved)
    val total: Int get() = levels.size
    val percent: Int get() = if (total == 0) 0 else solved * 100 / total
}
