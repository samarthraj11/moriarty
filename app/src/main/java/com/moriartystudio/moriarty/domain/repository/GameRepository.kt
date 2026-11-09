package com.moriartystudio.moriarty.domain.repository

import com.moriartystudio.moriarty.core.model.LeaderboardEntry
import com.moriartystudio.moriarty.core.model.Level
import com.moriartystudio.moriarty.core.model.PlayerProfile
import com.moriartystudio.moriarty.core.model.Puzzle
import com.moriartystudio.moriarty.core.model.PuzzleProgress

interface GameRepository {
    fun levels(): List<Level>
    fun puzzle(level: Int): Puzzle
    fun completeLevel(level: Int, rankedSeconds: Int)
    fun skipLevel(level: Int)
    fun puzzleProgress(level: Int): PuzzleProgress
    fun savePuzzleProgress(level: Int, progress: PuzzleProgress)
    fun clearPuzzleProgress(level: Int)
    fun leaderboard(): List<LeaderboardEntry>
    fun profile(): PlayerProfile
}
