package com.moriartystudio.moriarty.feature.home.presentation

import com.moriartystudio.moriarty.core.model.PuzzleCategory

sealed interface HomeSideEffect {
    data class OpenPuzzle(val level: Int) : HomeSideEffect
    data object OpenAllLevels : HomeSideEffect
    data class OpenCategory(val category: PuzzleCategory) : HomeSideEffect
    data object OpenDaily : HomeSideEffect
    data object OpenProfile : HomeSideEffect
}
