package com.moriartystudio.moriarty.data.local

import android.content.res.AssetManager
import com.moriartystudio.moriarty.core.model.Puzzle

interface PuzzleLocalDataSource {
    fun puzzles(): List<Puzzle>
}

class AssetPuzzleLocalDataSource(
    private val assetManager: AssetManager,
    private val parser: PuzzleFileParser = PuzzleFileParser(),
) : PuzzleLocalDataSource {
    private val catalogue by lazy {
        val json = assetManager.open(FILE_NAME).bufferedReader().use { it.readText() }
        parser.parse(json)
    }

    override fun puzzles(): List<Puzzle> = catalogue

    private companion object {
        const val FILE_NAME = "puzzles.json"
    }
}
