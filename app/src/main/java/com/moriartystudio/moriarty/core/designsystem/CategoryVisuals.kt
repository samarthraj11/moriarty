package com.moriartystudio.moriarty.core.designsystem

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ViewInAr
import androidx.compose.ui.graphics.vector.ImageVector
import com.moriartystudio.moriarty.core.model.PuzzleCategory

fun categoryIcon(category: PuzzleCategory): ImageVector = when (category) {
    PuzzleCategory.Pattern -> Icons.Outlined.GridView
    PuzzleCategory.Spatial -> Icons.Outlined.ViewInAr
    PuzzleCategory.Deduction -> Icons.Outlined.Search
    PuzzleCategory.Logic -> Icons.Outlined.Extension
}
