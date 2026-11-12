package com.moriartystudio.moriarty.app.navigation

import com.moriartystudio.moriarty.feature.category.presentation.CategoryViewModel
import com.moriartystudio.moriarty.feature.category.presentation.CategoryScreen
import com.moriartystudio.moriarty.core.model.PuzzleCategory
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import com.moriartystudio.moriarty.feature.welcome.presentation.WelcomeScreen
import androidx.activity.compose.BackHandler
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
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
import com.moriartystudio.moriarty.feature.progress.presentation.ProgressScreen
import com.moriartystudio.moriarty.feature.progress.presentation.ProgressViewModel
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

private const val NAV_ANIMATION_MS = 300

private fun forwardSlide(): ContentTransform =
    slideInHorizontally(tween(NAV_ANIMATION_MS)) { it } togetherWith slideOutHorizontally(tween(NAV_ANIMATION_MS)) { -it / 3 }

private fun backSlide(): ContentTransform =
    slideInHorizontally(tween(NAV_ANIMATION_MS)) { -it / 3 } togetherWith slideOutHorizontally(tween(NAV_ANIMATION_MS)) { it }

/** Full-width slide between tabs: a tab to the right enters from the right, one to the left from the left. */
private fun tabSlide(direction: Int): ContentTransform =
    slideInHorizontally(tween(NAV_ANIMATION_MS)) { it * direction } togetherWith slideOutHorizontally(tween(NAV_ANIMATION_MS)) { -it * direction }

// Position of a route in the bottom tab order, or null for non-tab screens.
private fun tabIndexOf(route: Any): Int? = when (route) {
    HomeRoute -> RootDestination.Levels.ordinal
    DailyRoute -> RootDestination.Daily.ordinal
    ProgressRoute -> RootDestination.Progress.ordinal
    ProfileRoute -> RootDestination.Profile.ordinal
    else -> null
}

enum class RootDestination { Levels, Daily, Progress, Profile }

private data object WelcomeRoute
private data object HomeRoute
private data object LevelsRoute
private data class CategoryRoute(val category: PuzzleCategory)
private data object DailyRoute
private data object ProgressRoute
private data object ProfileRoute
private data object SettingsRoute
private data class PuzzleRoute(val level: Int, val daily: Boolean = false)
private data class SolutionRoute(val level: Int, val seconds: Int)

@Composable
fun MoriartyApp() {
    val backStack = remember { mutableStateListOf<Any>(if (AppContainer.hasOnboarded()) HomeRoute else WelcomeRoute) }
    var showExitDialog by remember { mutableStateOf(false) }
    val activity = LocalContext.current.findActivity()
    // +1 / -1 while switching to a bottom tab on the right / left of the current one, else 0.
    val tabDirection = remember { IntArray(1) }
    fun back() { tabDirection[0] = 0; if (backStack.size > 1) backStack.removeAt(backStack.lastIndex) }
    fun open(route: Any) { tabDirection[0] = 0; backStack.add(route) }
    fun openRoot(destination: RootDestination) {
        val route = when (destination) { RootDestination.Levels -> HomeRoute; RootDestination.Daily -> DailyRoute; RootDestination.Progress -> ProgressRoute; RootDestination.Profile -> ProfileRoute }
        val currentTab = backStack.lastOrNull()?.let(::tabIndexOf)
        tabDirection[0] = when {
            currentTab == null || currentTab == destination.ordinal -> 0
            destination.ordinal > currentTab -> 1
            else -> -1
        }
        backStack.clear(); backStack.add(route)
    }

    // Bottom-nav roots sit alone on the stack: back goes to Home, and back on Home asks to exit.
    BackHandler {
        if (backStack.size > 1) back()
        else if (backStack.last() == WelcomeRoute) activity?.finish()
        else if (backStack.last() == HomeRoute) showExitDialog = true
        else openRoot(RootDestination.Levels)
    }
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Close Moriarty?") },
            text = { Text("Your progress is saved.") },
            confirmButton = { TextButton(onClick = { showExitDialog = false; activity?.finish() }) { Text("Close") } },
            dismissButton = { TextButton(onClick = { showExitDialog = false }) { Text("Stay") } },
        )
    }

    NavDisplay(
        backStack = backStack,
        onBack = ::back,
        // Going deeper slides the new screen in from the right and back reverses it;
        // switching bottom tabs slides toward the side the tab sits on.
        transitionSpec = { if (tabDirection[0] != 0) tabSlide(tabDirection[0]) else forwardSlide() },
        popTransitionSpec = { if (tabDirection[0] != 0) tabSlide(tabDirection[0]) else backSlide() },
        predictivePopTransitionSpec = { if (tabDirection[0] != 0) tabSlide(tabDirection[0]) else backSlide() },
        entryDecorators = listOf(rememberSaveableStateHolderNavEntryDecorator(), rememberViewModelStoreNavEntryDecorator()),
        entryProvider = entryProvider {
            entry<WelcomeRoute> {
                val context = LocalContext.current
                WelcomeScreen(
                    // Sign-in isn't wired to a backend yet, so only guest play moves on.
                    onGoogle = { Toast.makeText(context, "Google sign-in is coming soon", Toast.LENGTH_SHORT).show() },
                    onEmail = { Toast.makeText(context, "Email sign-in is coming soon", Toast.LENGTH_SHORT).show() },
                    onGuest = { AppContainer.completeOnboarding(); tabDirection[0] = 0; backStack.clear(); backStack.add(HomeRoute) },
                )
            }
            entry<HomeRoute> {
                val vm = viewModel { HomeViewModel(AppContainer.gameRepository) }
                HomeScreen(vm, { open(PuzzleRoute(it)) }, { open(LevelsRoute) }, ::openRoot, { openRoot(RootDestination.Profile) }, { open(CategoryRoute(it)) })
            }
            entry<CategoryRoute> { route ->
                val vm = viewModel(key = "category-${route.category}") { CategoryViewModel(route.category, AppContainer.gameRepository) }
                CategoryScreen(vm, ::back) { open(PuzzleRoute(it)) }
            }
            entry<LevelsRoute> {
                val vm = viewModel { LevelsViewModel(AppContainer.gameRepository) }
                LevelsScreen(vm, ::back) { open(PuzzleRoute(it)) }
            }
            entry<PuzzleRoute> { route ->
                val vm = viewModel(key = "puzzle-${route.level}-${route.daily}") { PuzzleViewModel(route.level, AppContainer.gameRepository, route.daily) }
                PuzzleScreen(
                    vm,
                    onBack = ::back,
                    onNext = { next ->
                        backStack.clear()
                        backStack.add(HomeRoute)
                        backStack.add(PuzzleRoute(next))
                    },
                    onNextSection = { category ->
                        backStack.clear()
                        backStack.add(HomeRoute)
                        backStack.add(CategoryRoute(category))
                    },
                    onFinish = { openRoot(RootDestination.Levels) },
                )
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
                    onLeaderboard = { openRoot(RootDestination.Progress) },
                )
            }
            entry<DailyRoute> {
                val vm = viewModel { DailyViewModel(AppContainer.gameRepository) }
                DailyScreen(vm, { level -> open(PuzzleRoute(level, daily = true)) }, ::openRoot)
            }
            entry<ProgressRoute> {
                val vm = viewModel { ProgressViewModel(AppContainer.gameRepository) }
                ProgressScreen(vm, ::openRoot)
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

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
