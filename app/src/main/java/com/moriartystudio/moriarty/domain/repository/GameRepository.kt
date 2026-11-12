package com.moriartystudio.moriarty.domain.repository

import java.time.LocalDate
import com.moriartystudio.moriarty.core.model.Level
import com.moriartystudio.moriarty.core.model.PlayerProfile
import com.moriartystudio.moriarty.core.model.Puzzle
import com.moriartystudio.moriarty.domain.progress.ProgressSummary

interface GameRepository {
    fun levels(): List<Level>
    fun puzzle(level: Int): Puzzle
    fun completeLevel(level: Int)
    /** Scores the first answer on [level]; returns the points change (0 if it was already scored). */
    fun recordFirstAnswer(level: Int, correct: Boolean): Int
    fun totalPoints(): Int
    fun skipLevel(level: Int)
    /** Today's daily puzzle: one per date, the same for everyone. */
    fun dailyPuzzle(date: LocalDate): Puzzle
    fun dailyPlayedDays(): Set<LocalDate>
    fun recordDailyPlayed(date: LocalDate)
    /** The first day the daily calendar counts from; earlier days are never shown as missed. */
    fun dailyStartDate(today: LocalDate): LocalDate
    /** Rank, badge and chart inputs, computed from this device's play history. */
    fun progress(today: LocalDate): ProgressSummary
    fun profile(): PlayerProfile
}
