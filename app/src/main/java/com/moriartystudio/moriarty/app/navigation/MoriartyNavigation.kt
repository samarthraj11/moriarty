package com.moriartystudio.moriarty.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.moriartystudio.moriarty.app.AppContainer
import com.moriartystudio.moriarty.feature.daily.presentation.DailyScreen
import com.moriartystudio.moriarty.feature.daily.presentation.DailyViewModel
import com.moriartystudio.moriarty.feature.home.presentation.HomeScreen
import com.moriartystudio.moriarty.feature.home.presentation.HomeViewModel
import com.moriartystudio.moriarty.feature.leaderboard.presentation.LeaderboardScreen
import com.moriartystudio.moriarty.feature.leaderboard.presentation.LeaderboardViewModel
import com.moriartystudio.moriarty.feature.levels.presentation.LevelsScreen
import com.moriartystudio.moriarty.feature.levels.presentation.LevelsViewModel
import com.moriartystudio.moriarty.feature.profile.presentation.ProfileScreen
import com.moriartystudio.moriarty.feature.profile.presentation.ProfileViewModel
import com.moriartystudio.moriarty.feature.puzzle.presentation.PuzzleScreen
import com.moriartystudio.moriarty.feature.puzzle.presentation.PuzzleViewModel
import com.moriartystudio.moriarty.feature.settings.presentation.SettingsScreen
import com.moriartystudio.moriarty.feature.settings.presentation.SettingsViewModel
import com.moriartystudio.moriarty.feature.solution.presentation.SolutionScreen
import com.moriartystudio.moriarty.feature.solution.presentation.SolutionViewModel

enum class RootDestination { Levels, Daily, Leaderboard, Profile }

private data object HomeRoute
private data object LevelsRoute
private data object DailyRoute
private data object LeaderboardRoute
private data object ProfileRoute
private data object SettingsRoute
private data class PuzzleRoute(val level: Int)
private data class SolutionRoute(val level: Int, val seconds: Int)

@Composable
fun MoriartyApp() {
    val backStack = remember { mutableStateListOf<Any>(HomeRoute) }
    fun back() { if (backStack.size > 1) backStack.removeAt(backStack.lastIndex) }
    fun open(route: Any) { backStack.add(route) }
    fun openRoot(destination: RootDestination) {
        val route = when (destination) { RootDestination.Levels -> HomeRoute; RootDestination.Daily -> DailyRoute; RootDestination.Leaderboard -> LeaderboardRoute; RootDestination.Profile -> ProfileRoute }
        backStack.clear(); backStack.add(route)
    }

    NavDisplay(
        backStack = backStack,
        onBack = ::back,
        entryDecorators = listOf(rememberSaveableStateHolderNavEntryDecorator(), rememberViewModelStoreNavEntryDecorator()),
        entryProvider = entryProvider {
            entry<HomeRoute> {
                val vm = viewModel { HomeViewModel(AppContainer.gameRepository) }
                HomeScreen(vm, { open(PuzzleRoute(it)) }, { open(LevelsRoute) }, ::openRoot, { openRoot(RootDestination.Profile) })
            }
            entry<LevelsRoute> {
                val vm = viewModel { LevelsViewModel(AppContainer.gameRepository) }
                LevelsScreen(vm, ::back) { open(PuzzleRoute(it)) }
            }
            entry<PuzzleRoute> { route ->
                val vm = viewModel(key = "puzzle-${route.level}") { PuzzleViewModel(route.level, AppContainer.gameRepository) }
                PuzzleScreen(vm, ::back) { level, seconds -> open(SolutionRoute(level, seconds)) }
            }
            entry<SolutionRoute> { route ->
                val vm = viewModel(key = "solution-${route.level}-${route.seconds}") { SolutionViewModel(route.level, route.seconds, AppContainer.gameRepository) }
                SolutionScreen(
                    vm,
                    onNext = { next ->
                        backStack.clear()
                        backStack.add(HomeRoute)
                        backStack.add(PuzzleRoute(next))
                    },
                    onComplete = { openRoot(RootDestination.Levels) },
                    onLeaderboard = { openRoot(RootDestination.Leaderboard) },
                )
            }
            entry<DailyRoute> {
                val vm = viewModel { DailyViewModel(AppContainer.gameRepository) }
                DailyScreen(vm, { open(PuzzleRoute(1)) }, ::openRoot)
            }
            entry<LeaderboardRoute> {
                val vm = viewModel { LeaderboardViewModel(AppContainer.gameRepository) }
                LeaderboardScreen(vm, ::openRoot)
            }
            entry<ProfileRoute> {
                val vm = viewModel { ProfileViewModel(AppContainer.gameRepository) }
                ProfileScreen(vm, { open(SettingsRoute) }, ::openRoot)
            }
            entry<SettingsRoute> {
                val vm = viewModel { SettingsViewModel() }
                SettingsScreen(vm, ::back)
            }
        },
    )
}
