package com.moriartystudio.moriarty.domain.repository

import com.moriartystudio.moriarty.core.model.LeaderboardEntry
import com.moriartystudio.moriarty.core.model.Level
import com.moriartystudio.moriarty.core.model.PlayerProfile
import com.moriartystudio.moriarty.core.model.Puzzle

interface GameRepository {
    fun levels(): List<Level>
    fun puzzle(level: Int): Puzzle
    fun leaderboard(): List<LeaderboardEntry>
    fun profile(): PlayerProfile
}
