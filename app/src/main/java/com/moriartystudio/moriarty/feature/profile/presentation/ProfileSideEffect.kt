package com.moriartystudio.moriarty.feature.profile.presentation

sealed interface ProfileSideEffect {
    data object OpenSettings : ProfileSideEffect
    data object LoggedOut : ProfileSideEffect
    data object ProgressReset : ProfileSideEffect
}
