package com.moriartystudio.moriarty.feature.leaderboard.presentation

sealed interface LeaderboardSideEffect { data class OpenPlayer(val name: String) : LeaderboardSideEffect }
