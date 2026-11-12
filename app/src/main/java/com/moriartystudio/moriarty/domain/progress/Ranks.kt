package com.moriartystudio.moriarty.domain.progress

/** The detective ladder. A first right answer is worth 10 points, so Moriarty takes ~42 clean solves. */
enum class DetectiveRank(val title: String, val numeral: String, val threshold: Int, val motto: String) {
    Rookie("Rookie", "I", 0, "Fresh eyes, open mind"),
    Sleuth("Sleuth", "II", 60, "You notice what others miss"),
    Inspector("Inspector", "III", 150, "Patterns bow to you"),
    ChiefInspector("Chief inspector", "IV", 280, "Few cases stay cold"),
    Moriarty("Moriarty", "V", 420, "The mastermind"),
}

data class RankProgress(
    val rank: DetectiveRank,
    val next: DetectiveRank?,
    val pointsToNext: Int,
    /** 0..1 across the whole ladder, for the tape-measure bar. */
    val ladderFraction: Float,
)

object Ranks {
    /** Wrong answers can push the total below zero; that still ranks as Rookie. */
    fun rankFor(points: Int): DetectiveRank = DetectiveRank.entries.last { points.coerceAtLeast(0) >= it.threshold }

    fun progress(points: Int): RankProgress {
        val safe = points.coerceAtLeast(0)
        val rank = rankFor(safe)
        val next = DetectiveRank.entries.getOrNull(rank.ordinal + 1)
        val top = DetectiveRank.entries.last().threshold
        return RankProgress(
            rank = rank,
            next = next,
            pointsToNext = next?.let { it.threshold - safe } ?: 0,
            ladderFraction = (safe.toFloat() / top).coerceIn(0f, 1f),
        )
    }
}
