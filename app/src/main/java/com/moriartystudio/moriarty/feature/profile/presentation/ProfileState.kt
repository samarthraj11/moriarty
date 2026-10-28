package com.moriartystudio.moriarty.feature.profile.presentation

import com.moriartystudio.moriarty.core.model.PlayerProfile

data class ProfileState(val profile: PlayerProfile? = null, val recentAchievements: List<String> = listOf("Sharp eye", "Four-day streak", "Reflection master"))
