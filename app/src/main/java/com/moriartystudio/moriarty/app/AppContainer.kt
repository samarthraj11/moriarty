package com.moriartystudio.moriarty.app

import android.content.Context
import com.moriartystudio.moriarty.data.repository.LocalGameRepository
import com.moriartystudio.moriarty.domain.repository.GameRepository

object AppContainer {
    private var repository: GameRepository? = null

    val gameRepository: GameRepository
        get() = checkNotNull(repository) { "AppContainer must be initialized before use." }

    fun initialize(context: Context) {
        if (repository == null) repository = LocalGameRepository(context.applicationContext)
    }
}
