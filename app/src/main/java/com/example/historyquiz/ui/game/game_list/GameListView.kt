package com.example.historyquiz.ui.game.game_list

import com.example.historyquiz.model.game.Lobby
import com.example.historyquiz.ui.base.interfaces.BaseRecyclerView

interface GameListView : BaseRecyclerView<Lobby> {

    fun showDetails(comics: Lobby)

    fun loadOfficialTests()

    fun onGameFinded()

    fun onBotGameFinded()

    fun removeLobby(lobbyId: String)
}
