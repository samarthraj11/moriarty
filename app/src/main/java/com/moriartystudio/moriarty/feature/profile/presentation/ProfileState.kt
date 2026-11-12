package com.moriartystudio.moriarty.feature.profile.presentation

import com.moriartystudio.moriarty.core.model.PlayerProfile

data class ProfileState(
    val profile: PlayerProfile? = null,
    val confirmReset: Boolean = false,
    val confirmLogout: Boolean = false,
)
