package com.moriartystudio.moriarty.feature.category.presentation

sealed interface CategorySideEffect {
    data class OpenPuzzle(val level: Int) : CategorySideEffect
    data object GoBack : CategorySideEffect
}
