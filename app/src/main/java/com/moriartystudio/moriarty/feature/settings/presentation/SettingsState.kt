package com.moriartystudio.moriarty.feature.settings.presentation

data class SettingsState(
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val timerVisible: Boolean = false,
    val reduceMotion: Boolean = true,
    val largerText: Boolean = true,
    val highContrast: Boolean = false,
    val audioDescriptions: Boolean = false,
    val colourIndependent: Boolean = true,
    val confirmHint: Boolean = true,
)
