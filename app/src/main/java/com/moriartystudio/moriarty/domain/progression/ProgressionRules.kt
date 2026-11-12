package com.moriartystudio.moriarty.domain.progression

import com.moriartystudio.moriarty.core.model.Level

object ProgressionRules {
    /** Each category unlocks on its own: a level opens once the one before it in the same category is solved or skipped. */
    fun isUnlockedInCategory(previousInCategory: Int?, passedLevels: Set<Int>): Boolean =
        previousInCategory == null || previousInCategory in passedLevels

    fun currentLevel(levels: List<Level>): Level? = levels.firstOrNull { it.isUnlocked && !it.isSolved && !it.isSkipped }
        ?: levels.lastOrNull()

    fun nearbyLevels(levels: List<Level>, currentLevel: Level?, windowSize: Int = 5): List<Level> {
        if (levels.isEmpty()) return emptyList()
        val currentIndex = currentLevel?.let(levels::indexOf)?.coerceAtLeast(0) ?: 0
        val start = (currentIndex - windowSize / 2).coerceIn(0, (levels.size - windowSize).coerceAtLeast(0))
        return levels.drop(start).take(windowSize)
    }
}
