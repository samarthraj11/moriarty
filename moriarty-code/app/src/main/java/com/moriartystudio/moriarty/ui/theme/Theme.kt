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

val Ink = Color(0xFF132238)
val InkMuted = Color(0xFF587083)
val Ivory = Color(0xFFFFFAF0)
val Paper = Color(0xFFFFFDF8)
val Emerald = Color(0xFF0D6B56)
val EmeraldDark = Color(0xFF095443)
val Mint = Color(0xFFDCEFE5)
val SoftMint = Color(0xFFEDF7F0)
val Peach = Color(0xFFFF9272)
val SoftPeach = Color(0xFFFFE1D4)
val Amber = Color(0xFFFFC45C)
val SoftAmber = Color(0xFFFFE8BA)
val Line = Color(0xFFE5E7E2)
val Mist = Color(0xFFF2F2ED)
val ErrorCoral = Color(0xFFE96952)

private val MoriartyColorScheme = lightColorScheme(
    primary = Emerald, onPrimary = Color.White,
    primaryContainer = Mint, onPrimaryContainer = EmeraldDark,
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
