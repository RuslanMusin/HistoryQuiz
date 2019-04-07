package com.example.historyquiz.ui.navigation

import android.support.v7.widget.Toolbar
import com.example.historyquiz.model.game.GameData
import com.example.historyquiz.model.game.Lobby
import com.example.historyquiz.ui.base.interfaces.BasicFunctional

interface NavigationView: BasicFunctional {



    fun setRequest(request: () -> Unit)

    fun showConnectionError()

    fun supportActionBar(toolbar: Toolbar)

    fun onBackPressed()

    fun hideDialog()
    fun goToGame()
    fun setDialog(gameData: GameData, lobby: Lobby)

}