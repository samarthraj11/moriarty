package com.moriartystudio.moriarty.feature.profile.presentation

import androidx.lifecycle.ViewModel
import com.moriartystudio.moriarty.domain.repository.GameRepository
import org.orbitmvi.orbit.OrbitContainerHost
import org.orbitmvi.orbit.viewmodel.orbitContainer

class ProfileViewModel(repository: GameRepository) : ViewModel(), OrbitContainerHost<ProfileState, ProfileState, ProfileSideEffect> {
    private val profile = repository.profile()
    override val container = orbitContainer<ProfileState, ProfileSideEffect>(ProfileState(
        profile = profile,
        recentAchievements = if (profile.solved > 0) listOf("First insight") else emptyList(),
    ))
    fun settings() = intent { postSideEffect(ProfileSideEffect.OpenSettings) }
}
