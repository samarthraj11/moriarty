package com.moriartystudio.moriarty.feature.progress.presentation

import androidx.lifecycle.ViewModel
import com.moriartystudio.moriarty.domain.progress.Badges
import com.moriartystudio.moriarty.domain.progress.Ranks
import com.moriartystudio.moriarty.domain.repository.GameRepository
import org.orbitmvi.orbit.OrbitContainerHost
import org.orbitmvi.orbit.viewmodel.orbitContainer
import java.time.LocalDate

class ProgressViewModel(private val repository: GameRepository) : ViewModel(), OrbitContainerHost<ProgressState, ProgressState, ProgressSideEffect> {
    override val container = orbitContainer<ProgressState, ProgressSideEffect>(buildState())

    private fun buildState(): ProgressState {
        val summary = repository.progress(LocalDate.now())
        return ProgressState(summary, Ranks.progress(summary.points), Badges.unlocked(summary))
    }

    fun refresh() = intent { reduce { buildState() } }
}
