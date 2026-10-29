package com.moriartystudio.moriarty.feature.leaderboard.presentation

import com.moriartystudio.moriarty.core.model.LeaderboardEntry

enum class LeaderboardPeriod { Today, Week, AllTime }
data class LeaderboardState(
    val entries: List<LeaderboardEntry> = emptyList(),
    val selectedPeriod: LeaderboardPeriod = LeaderboardPeriod.Today,
    val puzzleTitle: String = "Alternating shapes",
)
