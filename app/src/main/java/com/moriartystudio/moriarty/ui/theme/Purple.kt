package com.moriartystudio.moriarty.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Indigo palette from the purple redesign.
val Indigo = Color(0xFF5262FB)
val IndigoLight = Color(0xFF7985FF)
val Lavender = Color(0xFF9DA6FB)
val Frost = Color(0xFFE9EBFA)
val Night = Color(0xFF0F0F0F)

val GoldLight = Color(0xFFFFE08A)
val Gold = Color(0xFFF5B52E)
val GoldDark = Color(0xFFC98512)

val IndigoGradient = Brush.linearGradient(listOf(Indigo, IndigoLight))

// One colour per puzzle category, used for tiles, progress and accents everywhere.
val CategoryPattern = Color(0xFFF5B52E)
val CategorySpatial = Color(0xFF5262FB)
val CategoryDeduction = Color(0xFFFF7A5C)
val CategoryLogic = Color(0xFF1BB3A0)

fun categoryColor(category: com.moriartystudio.moriarty.core.model.PuzzleCategory): Color = when (category) {
    com.moriartystudio.moriarty.core.model.PuzzleCategory.Pattern -> CategoryPattern
    com.moriartystudio.moriarty.core.model.PuzzleCategory.Spatial -> CategorySpatial
    com.moriartystudio.moriarty.core.model.PuzzleCategory.Deduction -> CategoryDeduction
    com.moriartystudio.moriarty.core.model.PuzzleCategory.Logic -> CategoryLogic
}

// Confirming / moving-on actions after a success.
val SuccessGreen = Color(0xFF2EAD62)
