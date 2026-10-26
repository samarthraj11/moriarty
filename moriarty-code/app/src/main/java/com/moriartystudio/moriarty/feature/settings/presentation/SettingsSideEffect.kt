package com.moriartystudio.moriarty.feature.settings.presentation

sealed interface SettingsSideEffect { data object GoBack : SettingsSideEffect; data object SignOut : SettingsSideEffect }
