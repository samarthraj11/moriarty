package com.moriartystudio.moriarty.app

import com.moriartystudio.moriarty.data.repository.LocalGameRepository
import com.moriartystudio.moriarty.domain.repository.GameRepository

object AppContainer {
    val gameRepository: GameRepository by lazy { LocalGameRepository() }
}
