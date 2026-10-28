package com.moriartystudio.moriarty.domain.progression

import com.moriartystudio.moriarty.core.model.Level

object ProgressionRules {
    fun isUnlocked(level: Int, solvedLevels: Set<Int>): Boolean = level == 1 || level - 1 in solvedLevels

    fun currentLevel(levels: List<Level>): Level? = levels.firstOrNull { it.isUnlocked && !it.isSolved }
        ?: levels.lastOrNull()

    fun nearbyLevels(levels: List<Level>, currentLevel: Level?, windowSize: Int = 5): List<Level> {
        if (levels.isEmpty()) return emptyList()
        val currentIndex = currentLevel?.let(levels::indexOf)?.coerceAtLeast(0) ?: 0
        val start = (currentIndex - windowSize / 2).coerceIn(0, (levels.size - windowSize).coerceAtLeast(0))
        return levels.drop(start).take(windowSize)
    }
}
