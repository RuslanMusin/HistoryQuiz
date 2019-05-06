package com.example.historyquiz.ui.game.add_game

import com.example.historyquiz.ui.base.interfaces.BasicFunctional

interface AddGameView : BasicFunctional {

    fun onGameCreated()
    fun showTitleError(b: Boolean)
    fun showEpochError(b: Boolean)
}