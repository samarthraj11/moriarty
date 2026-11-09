package com.moriartystudio.moriarty.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Ink = Color(0xFF12142B)
val InkMuted = Color(0xFF6A6F8F)
val Ivory = Color.White
val Paper = Color(0xFFF4F5FD)
val IndigoDark = Color(0xFF3E4BE0)
val SoftFrost = Color(0xFFF4F5FD)
val Peach = Color(0xFFFF9272)
val SoftPeach = Color(0xFFFFE3DA)
val Amber = Color(0xFFF5B52E)
val SoftAmber = Color(0xFFFFF1CF)
val Line = Color(0xFFE3E5F6)
val Mist = Color(0xFFEEF0FB)
val ErrorCoral = Color(0xFFE96952)

private val MoriartyColorScheme = lightColorScheme(
    primary = Indigo, onPrimary = Color.White,
    primaryContainer = Frost, onPrimaryContainer = IndigoDark,
    secondary = Amber, onSecondary = Ink,
    tertiary = Peach, background = Ivory, onBackground = Ink,
    surface = Paper, onSurface = Ink, surfaceVariant = Mist,
    onSurfaceVariant = InkMuted, outline = Line, error = ErrorCoral,
)

private val MoriartyTypography = Typography(
    displaySmall = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 36.sp, lineHeight = 42.sp),
    headlineLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 30.sp, lineHeight = 36.sp),
    headlineMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 30.sp),
    titleLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 26.sp),
    titleMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 22.sp),
    bodyLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    labelLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, lineHeight = 20.sp),
    labelMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, lineHeight = 16.sp),
)

@Composable
fun MoriartyTheme(
    @Suppress("UNUSED_PARAMETER") darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = MoriartyColorScheme,
        typography = MoriartyTypography,
        content = content,
    )
}
