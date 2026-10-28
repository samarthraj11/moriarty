package com.moriartystudio.moriarty.feature.daily.presentation

import androidx.lifecycle.ViewModel
import org.orbitmvi.orbit.OrbitContainerHost
import org.orbitmvi.orbit.viewmodel.orbitContainer

class DailyViewModel : ViewModel(), OrbitContainerHost<DailyState, DailyState, DailySideEffect> {
    override val container = orbitContainer<DailyState, DailySideEffect>(DailyState())
    fun start() = intent { postSideEffect(DailySideEffect.StartChallenge) }
}
