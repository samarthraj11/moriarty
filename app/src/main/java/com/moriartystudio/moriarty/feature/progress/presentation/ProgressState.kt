package com.moriartystudio.moriarty.feature.progress.presentation

import com.moriartystudio.moriarty.domain.progress.Badge
import com.moriartystudio.moriarty.domain.progress.ProgressSummary
import com.moriartystudio.moriarty.domain.progress.RankProgress
import com.moriartystudio.moriarty.domain.progress.Ranks

data class ProgressState(
    val summary: ProgressSummary = ProgressSummary(),
    val rank: RankProgress = Ranks.progress(0),
    val unlocked: Set<Badge> = emptySet(),
)

sealed interface ProgressSideEffect
