package com.moriartystudio.moriarty.feature.daily.presentation

sealed interface DailySideEffect { data class StartChallenge(val level: Int) : DailySideEffect }
