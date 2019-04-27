package com.example.historyquiz.ui.game.add_game

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.historyquiz.R
import com.example.historyquiz.model.game.Lobby
import com.example.historyquiz.repository.card.CardRepository
import com.example.historyquiz.repository.game.GameRepository
import com.example.historyquiz.ui.base.BasePresenter
import com.example.historyquiz.utils.AppHelper
import com.example.historyquiz.utils.AppHelper.Companion.currentUser
import com.example.historyquiz.utils.Const
import javax.inject.Inject

@InjectViewState
class AddGamePresenter @Inject constructor() : BasePresenter<AddGameView>() {

    @Inject
    lateinit var gamesRepository: GameRepository

    @Inject
    lateinit var cardRepository: CardRepository

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
    }

    fun createGame(lobby: Lobby) {
        cardRepository.findMyCardsByEpoch(AppHelper.currentId, lobby.epochId).subscribe { myCards ->
            val mySize = myCards.size
            Log.d(Const.TAG_LOG,"mySize = $mySize and cardNumber = ${lobby.cardNumber}")
            if (mySize >= lobby.cardNumber) {
                lobby.usersIds.add(currentUser.id)
                gamesRepository.createLobby(lobby) {
                    viewState.onGameCreated() }
            } else {
                viewState.showSnackBar(R.string.you_dont_have_card_min)
            }
        }

    }
}