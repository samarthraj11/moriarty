package com.moriartystudio.moriarty.feature.home.presentation

import androidx.lifecycle.ViewModel
import com.moriartystudio.moriarty.domain.repository.GameRepository
import org.orbitmvi.orbit.OrbitContainerHost
import org.orbitmvi.orbit.viewmodel.orbitContainer

class HomeViewModel(repository: GameRepository) : ViewModel(), OrbitContainerHost<HomeState, HomeState, HomeSideEffect> {
    private val initial = repository.levels()
    override val container = orbitContainer<HomeState, HomeSideEffect>(
        HomeState(currentLevel = initial.first { it.number == 7 }, nearbyLevels = initial.filter { it.number in 5..9 }),
    )

    fun continuePuzzle() = intent { postSideEffect(HomeSideEffect.OpenPuzzle(state.currentLevel?.number ?: 7)) }
    fun openAllLevels() = intent { postSideEffect(HomeSideEffect.OpenAllLevels) }
    fun openDaily() = intent { postSideEffect(HomeSideEffect.OpenDaily) }
    fun openProfile() = intent { postSideEffect(HomeSideEffect.OpenProfile) }
}
