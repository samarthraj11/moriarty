package com.moriartystudio.moriarty.domain.progress

import com.moriartystudio.moriarty.core.model.PuzzleCategory

/** A seal the player can earn. [category] colours section seals; others use gold. */
enum class Badge(val title: String, val hint: String, val category: PuzzleCategory? = null) {
    FirstCase("First case", "Solve a puzzle"),
    SharpEye("Sharp eye", "5 right first answers in a row"),
    OnARoll("On a roll", "10 right first answers in a row"),
    Regular("Regular", "Play the daily 3 days in a row"),
    Devoted("Devoted", "Play the daily 7 days in a row"),
    DailyFive("Daily five", "Play 5 daily challenges"),
    PatternMaster("Patterns", "Solve every Patterns puzzle", PuzzleCategory.Pattern),
    SpatialMaster("Spatial", "Solve every Spatial puzzle", PuzzleCategory.Spatial),
    DeductionMaster("Deduction", "Solve every Deduction puzzle", PuzzleCategory.Deduction),
    LogicMaster("Logic", "Solve every Logic puzzle", PuzzleCategory.Logic),
    Flawless("Flawless", "10 puzzles, all right first time"),
    Century("Century", "Reach 100 points"),
}

object Badges {
    fun unlocked(summary: ProgressSummary): Set<Badge> = Badge.entries.filterTo(mutableSetOf()) { isUnlocked(it, summary) }

    private fun isUnlocked(badge: Badge, s: ProgressSummary): Boolean = when (badge) {
        Badge.FirstCase -> s.solved >= 1
        Badge.SharpEye -> s.bestCorrectRun >= 5
        Badge.OnARoll -> s.bestCorrectRun >= 10
        Badge.Regular -> s.bestDailyStreak >= 3
        Badge.Devoted -> s.bestDailyStreak >= 7
        Badge.DailyFive -> s.dailiesPlayed >= 5
        Badge.PatternMaster -> cleared(s, PuzzleCategory.Pattern)
        Badge.SpatialMaster -> cleared(s, PuzzleCategory.Spatial)
        Badge.DeductionMaster -> cleared(s, PuzzleCategory.Deduction)
        Badge.LogicMaster -> cleared(s, PuzzleCategory.Logic)
        Badge.Flawless -> s.answered >= 10 && s.firstTryCorrect == s.answered
        Badge.Century -> s.points >= 100
    }

    private fun cleared(s: ProgressSummary, category: PuzzleCategory): Boolean =
        s.sections.firstOrNull { it.category == category }?.let { it.total > 0 && it.solved >= it.total } == true
}
