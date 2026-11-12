package com.moriartystudio.moriarty.feature.profile.presentation

import androidx.lifecycle.ViewModel
import com.moriartystudio.moriarty.domain.repository.GameRepository
import org.orbitmvi.orbit.OrbitContainerHost
import org.orbitmvi.orbit.viewmodel.orbitContainer

class ProfileViewModel(private val repository: GameRepository) : ViewModel(), OrbitContainerHost<ProfileState, ProfileState, ProfileSideEffect> {
    override val container = orbitContainer<ProfileState, ProfileSideEffect>(ProfileState(profile = repository.profile()))

    fun refresh() = intent { reduce { state.copy(profile = repository.profile()) } }
    fun settings() = intent { postSideEffect(ProfileSideEffect.OpenSettings) }

    fun askReset() = intent { reduce { state.copy(confirmReset = true) } }
    fun dismissReset() = intent { reduce { state.copy(confirmReset = false) } }
    fun reset() = intent {
        repository.resetProgress()
        reduce { state.copy(profile = repository.profile(), confirmReset = false) }
        postSideEffect(ProfileSideEffect.ProgressReset)
    }

    // There is no account system yet, so logging out has nothing to sign out of; progress stays on the device.
    fun askLogout() = intent { reduce { state.copy(confirmLogout = true) } }
    fun dismissLogout() = intent { reduce { state.copy(confirmLogout = false) } }
    fun logout() = intent {
        reduce { state.copy(confirmLogout = false) }
        postSideEffect(ProfileSideEffect.LoggedOut)
    }
}
