package com.moriartystudio.moriarty.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val MoriartyColorScheme = lightColorScheme()

@Composable
fun MoriartyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MoriartyColorScheme,
        content = content,
    )
}
