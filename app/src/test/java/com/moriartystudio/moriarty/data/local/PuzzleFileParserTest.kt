package com.moriartystudio.moriarty.data.local

import com.moriartystudio.moriarty.core.model.PuzzleAnswerVisual
import com.moriartystudio.moriarty.core.model.PuzzleContent
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class PuzzleFileParserTest {
    private val parser = PuzzleFileParser()
    private val catalogueJson = puzzleFile().readText()

    @Test
    fun `catalogue contains ten sequential valid puzzles`() {
        val puzzles = parser.parse(catalogueJson)

        assertEquals((1..10).toList(), puzzles.map { it.level })
        assertTrue(puzzles.all { it.correctOption in it.options })
        assertTrue(puzzles[0].content is PuzzleContent.ShapeSequence)
        assertTrue(puzzles[7].content is PuzzleContent.RouteGrid)
        assertTrue(puzzles[9].content is PuzzleContent.Matrix)
    }

    @Test
    fun `visual answer metadata is loaded from the catalogue`() {
        val puzzles = parser.parse(catalogueJson)

        assertTrue(puzzles[0].optionVisuals.getValue("Circle") is PuzzleAnswerVisual.Token)
        assertTrue(puzzles[6].optionVisuals.getValue("D") is PuzzleAnswerVisual.TokenGroup)
        assertEquals(4, puzzles[9].optionVisuals.size)
    }

    @Test
    fun `catalogue rejects an answer that is not an option`() {
        val invalid = catalogueJson.replaceFirst(
            "\"correctOption\": \"Circle\"",
            "\"correctOption\": \"Hexagon\"",
        )

        assertThrows(IllegalArgumentException::class.java) { parser.parse(invalid) }
    }

    private fun puzzleFile(): File = listOf(
        File("src/main/assets/puzzles.json"),
        File("app/src/main/assets/puzzles.json"),
    ).first(File::isFile)
}
