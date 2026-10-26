package com.moriartystudio.moriarty.feature.settings.presentation

import androidx.lifecycle.ViewModel
import org.orbitmvi.orbit.OrbitContainerHost
import org.orbitmvi.orbit.viewmodel.orbitContainer

class SettingsViewModel : ViewModel(), OrbitContainerHost<SettingsState, SettingsState, SettingsSideEffect> {
    override val container = orbitContainer<SettingsState, SettingsSideEffect>(SettingsState())
    fun sound(value: Boolean) = intent { reduce { state.copy(soundEnabled = value) } }
    fun haptics(value: Boolean) = intent { reduce { state.copy(hapticsEnabled = value) } }
    fun timer(value: Boolean) = intent { reduce { state.copy(timerVisible = value) } }
    fun motion(value: Boolean) = intent { reduce { state.copy(reduceMotion = value) } }
    fun largerText(value: Boolean) = intent { reduce { state.copy(largerText = value) } }
    fun contrast(value: Boolean) = intent { reduce { state.copy(highContrast = value) } }
    fun descriptions(value: Boolean) = intent { reduce { state.copy(audioDescriptions = value) } }
    fun colourClues(value: Boolean) = intent { reduce { state.copy(colourIndependent = value) } }
    fun confirmHint(value: Boolean) = intent { reduce { state.copy(confirmHint = value) } }
    fun back() = intent { postSideEffect(SettingsSideEffect.GoBack) }
    fun signOut() = intent { postSideEffect(SettingsSideEffect.SignOut) }
}
