package com.moriartystudio.moriarty

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.moriartystudio.moriarty.core.model.Difficulty
import com.moriartystudio.moriarty.core.model.Level
import com.moriartystudio.moriarty.core.model.Puzzle
import com.moriartystudio.moriarty.core.model.PuzzleCategory
import com.moriartystudio.moriarty.core.model.PuzzleContent
import com.moriartystudio.moriarty.core.model.ShapeKind
import com.moriartystudio.moriarty.core.model.VisualToken
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
    val nearby = (1..5).map { number ->
        Level(
            number = number,
            title = "Level $number",
            category = PuzzleCategory.Pattern,
            difficulty = Difficulty.Easy,
            isSolved = false,
            isUnlocked = number == 1,
        )
    }
    val puzzle = Puzzle(
        1,
        "Alternating shapes",
        "Which shape comes next?",
        PuzzleCategory.Pattern,
        Difficulty.Easy,
        PuzzleContent.ShapeSequence(listOf(VisualToken(ShapeKind.Circle), VisualToken(ShapeKind.Triangle, accentIndex = 1), VisualToken(ShapeKind.Circle), VisualToken(ShapeKind.Triangle, accentIndex = 1), null)),
        listOf("Circle", "Triangle", "Square", "Diamond"),
        "Circle",
        "Compare odd positions.",
        "The sequence alternates.",
    )
    MoriartyTheme {
        HomeContent(
            state = HomeState(currentLevel = nearby.first(), currentPuzzle = puzzle, nearbyLevels = nearby),
            onContinue = {},
            onLevel = {},
            onAllLevels = {},
            onDaily = {},
            onProfile = {},
            onRoot = {},
        )
    }
}
