package com.moriartystudio.moriarty.feature.profile.presentation

import androidx.lifecycle.ViewModel
import com.moriartystudio.moriarty.domain.repository.GameRepository
import org.orbitmvi.orbit.OrbitContainerHost
import org.orbitmvi.orbit.viewmodel.orbitContainer

class ProfileViewModel(repository: GameRepository) : ViewModel(), OrbitContainerHost<ProfileState, ProfileState, ProfileSideEffect> {
    override val container = orbitContainer<ProfileState, ProfileSideEffect>(ProfileState(repository.profile()))
    fun settings() = intent { postSideEffect(ProfileSideEffect.OpenSettings) }
}
