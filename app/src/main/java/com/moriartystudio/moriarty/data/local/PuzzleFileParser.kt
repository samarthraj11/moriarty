package com.moriartystudio.moriarty.data.local

import com.moriartystudio.moriarty.core.model.Difficulty
import com.moriartystudio.moriarty.core.model.Puzzle
import com.moriartystudio.moriarty.core.model.PuzzleAnswerVisual
import com.moriartystudio.moriarty.core.model.PuzzleCategory
import com.moriartystudio.moriarty.core.model.PuzzleContent
import com.moriartystudio.moriarty.core.model.ShapeKind
import com.moriartystudio.moriarty.core.model.VisualToken
import org.json.JSONArray
import org.json.JSONObject

class PuzzleFileParser {
    fun parse(json: String): List<Puzzle> {
        val root = JSONObject(json)
        require(root.getInt("schemaVersion") == SCHEMA_VERSION) {
            "Unsupported puzzle schema version: ${root.getInt("schemaVersion")}"
        }

        val puzzles = root.getJSONArray("puzzles").mapObjects(::parsePuzzle)
        require(puzzles.isNotEmpty()) { "The puzzle catalogue cannot be empty." }
        require(puzzles.map(Puzzle::level) == (1..puzzles.size).toList()) {
            "Puzzle levels must be unique, sequential, and ordered from 1."
        }
        return puzzles
    }

    private fun parsePuzzle(json: JSONObject): Puzzle {
        val options = json.getJSONArray("options").strings()
        val correctOption = json.getString("correctOption")
        val optionVisuals = json.optJSONObject("optionVisuals")
            ?.keys()
            ?.asSequence()
            ?.associateWith { option -> parseAnswerVisual(json.getJSONObject("optionVisuals").getJSONObject(option)) }
            .orEmpty()

        require(correctOption in options) {
            "Level ${json.getInt("level")} has a correct answer that is not in its options."
        }
        require(optionVisuals.keys.all(options::contains)) {
            "Level ${json.getInt("level")} has visuals for unknown answer options."
        }

        return Puzzle(
            level = json.getInt("level"),
            title = json.getString("title"),
            prompt = json.getString("prompt"),
            category = PuzzleCategory.valueOf(json.getString("category")),
            difficulty = Difficulty.valueOf(json.getString("difficulty")),
            content = parseContent(json.getJSONObject("content")),
            options = options,
            correctOption = correctOption,
            hint = json.getString("hint"),
            explanation = json.getString("explanation"),
            optionVisuals = optionVisuals,
        )
    }

    private fun parseContent(json: JSONObject): PuzzleContent = when (json.getString("type")) {
        "shapeSequence" -> PuzzleContent.ShapeSequence(json.getJSONArray("tokens").nullableTokens())
        "numberSequence" -> PuzzleContent.NumberSequence(json.getJSONArray("values").strings())
        "matrix" -> PuzzleContent.Matrix(
            rows = json.getInt("rows"),
            columns = json.getInt("columns"),
            tokens = json.getJSONArray("tokens").nullableTokens(),
        )
        "rotationSequence" -> PuzzleContent.RotationSequence(
            json.getJSONArray("rotations").mapValues { index ->
                if (isNull(index)) null else getDouble(index).toFloat()
            },
        )
        "clueList" -> PuzzleContent.ClueList(json.getJSONArray("clues").strings())
        "equations" -> PuzzleContent.Equations(json.getJSONArray("rows").strings())
        "oddOneOut" -> PuzzleContent.OddOneOut(
            json.getJSONArray("groups").mapValues { index ->
                getJSONArray(index).mapObjects { it.toVisualToken() }
            },
        )
        "routeGrid" -> PuzzleContent.RouteGrid(
            rows = json.getInt("rows"),
            columns = json.getInt("columns"),
            blockedCells = json.getJSONArray("blockedCells").ints().toSet(),
            startCell = json.getInt("startCell"),
            endCell = json.getInt("endCell"),
        )
        "statements" -> PuzzleContent.Statements(json.getJSONArray("statements").strings())
        else -> error("Unknown puzzle content type: ${json.getString("type")}")
    }

    private fun parseAnswerVisual(json: JSONObject): PuzzleAnswerVisual = when (json.getString("type")) {
        "token" -> PuzzleAnswerVisual.Token(json.getJSONObject("token").toVisualToken())
        "tokenGroup" -> PuzzleAnswerVisual.TokenGroup(
            json.getJSONArray("tokens").mapObjects { it.toVisualToken() },
        )
        else -> error("Unknown answer visual type: ${json.getString("type")}")
    }

    private fun JSONObject.toVisualToken() = VisualToken(
        shape = ShapeKind.valueOf(getString("shape")),
        rotationDegrees = optDouble("rotationDegrees", 0.0).toFloat(),
        accentIndex = optInt("accentIndex", 0),
    )

    private fun JSONArray.nullableTokens() = mapValues { index ->
        if (isNull(index)) null else getJSONObject(index).toVisualToken()
    }

    private fun JSONArray.strings() = mapValues { index -> getString(index) }
    private fun JSONArray.ints() = mapValues { index -> getInt(index) }
    private fun <T> JSONArray.mapObjects(transform: (JSONObject) -> T) =
        mapValues { index -> transform(getJSONObject(index)) }

    private fun <T> JSONArray.mapValues(transform: JSONArray.(Int) -> T) =
        List(length()) { index -> transform(index) }

    private companion object {
        const val SCHEMA_VERSION = 1
    }
}
