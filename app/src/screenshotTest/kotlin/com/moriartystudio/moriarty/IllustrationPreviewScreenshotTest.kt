package com.moriartystudio.moriarty

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.moriartystudio.moriarty.core.designsystem.GeometricMirrorHero
import com.moriartystudio.moriarty.core.designsystem.MirrorClockIllustration
import com.moriartystudio.moriarty.core.designsystem.SignalIllustration
import com.moriartystudio.moriarty.ui.theme.MoriartyTheme

@PreviewTest
@Preview(name = "Geometric mirror", widthDp = 390, heightDp = 260)
@Composable
fun GeometricMirrorScreenshot() {
    MoriartyTheme { GeometricMirrorHero(Modifier.fillMaxSize()) }
}

@PreviewTest
@Preview(name = "Mirror clock", widthDp = 390, heightDp = 320)
@Composable
fun MirrorClockScreenshot() {
    MoriartyTheme { MirrorClockIllustration(Modifier.fillMaxSize()) }
}

@PreviewTest
@Preview(name = "Daily signal", widthDp = 390, heightDp = 260)
@Composable
fun DailySignalScreenshot() {
    MoriartyTheme { SignalIllustration(Modifier.fillMaxSize()) }
}
