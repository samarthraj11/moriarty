package com.moriartystudio.moriarty.feature.home.presentation

sealed interface HomeSideEffect {
    data class OpenPuzzle(val level: Int) : HomeSideEffect
    data object OpenAllLevels : HomeSideEffect
    data object OpenDaily : HomeSideEffect
    data object OpenProfile : HomeSideEffect
}
