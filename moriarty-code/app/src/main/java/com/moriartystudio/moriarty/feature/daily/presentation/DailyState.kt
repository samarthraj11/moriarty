package com.moriartystudio.moriarty.feature.daily.presentation

data class DailyState(val streak: Int = 4, val resetTime: String = "08:24", val playersToday: String = "12,481", val isAttempted: Boolean = false)
