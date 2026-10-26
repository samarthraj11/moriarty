package com.moriartystudio.moriarty

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.moriartystudio.moriarty.core.model.Difficulty
import com.moriartystudio.moriarty.core.model.Level
import com.moriartystudio.moriarty.core.model.PuzzleCategory
import com.moriartystudio.moriarty.feature.home.presentation.HomeContent
import com.moriartystudio.moriarty.feature.home.presentation.HomeState
import com.moriartystudio.moriarty.ui.theme.MoriartyTheme

@PreviewTest
@Preview(
    name = "Moriarty home",
    widthDp = 390,
    heightDp = 844,
    showBackground = true,
)
@Composable
fun MoriartyHomeScreenshot() {
    val nearby = (5..9).map { number ->
        Level(
            number = number,
            title = "Level $number",
            category = PuzzleCategory.Reflection,
            difficulty = Difficulty.Easy,
            isSolved = number <= 6,
            isUnlocked = number <= 7,
        )
    }
    MoriartyTheme {
        HomeContent(
            state = HomeState(currentLevel = nearby.first { it.number == 7 }, nearbyLevels = nearby),
            onContinue = {},
            onAllLevels = {},
            onDaily = {},
            onProfile = {},
            onRoot = {},
        )
    }
}
