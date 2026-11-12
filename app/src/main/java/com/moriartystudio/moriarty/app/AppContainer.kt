package com.moriartystudio.moriarty.app

import android.content.Context
import android.content.SharedPreferences
import com.moriartystudio.moriarty.data.repository.LocalGameRepository
import com.moriartystudio.moriarty.domain.repository.GameRepository

object AppContainer {
    private var appPreferences: SharedPreferences? = null
    private var repository: GameRepository? = null

    val gameRepository: GameRepository
        get() = checkNotNull(repository) { "AppContainer must be initialized before use." }

    fun initialize(context: Context) {
        // Kept apart from the game data so resetting progress doesn't replay the welcome screen.
        if (appPreferences == null) appPreferences = context.applicationContext.getSharedPreferences("moriarty_app", Context.MODE_PRIVATE)
        if (repository == null) repository = LocalGameRepository(context.applicationContext)
    }

    fun hasOnboarded(): Boolean = appPreferences?.getBoolean(ONBOARDED, false) ?: true

    fun completeOnboarding() {
        appPreferences?.edit()?.putBoolean(ONBOARDED, true)?.apply()
    }

    private const val ONBOARDED = "onboarded"
}
