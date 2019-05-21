package com.example.historyquiz.ui.game.fast_game

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.historyquiz.R
import com.example.historyquiz.model.game.Lobby
import com.example.historyquiz.model.game.LobbyPlayerData
import com.example.historyquiz.model.user.User
import com.example.historyquiz.repository.card.CardRepository
import com.example.historyquiz.repository.game.GameRepository
import com.example.historyquiz.repository.user.UserRepository
import com.example.historyquiz.ui.base.BasePresenter
import com.example.historyquiz.ui.game.play.PlayGameFragment
import com.example.historyquiz.utils.AppHelper
import com.example.historyquiz.utils.Const
import com.example.historyquiz.utils.Const.TAG_LOG
import javax.inject.Inject

@InjectViewState
class FastGamePresenter @Inject constructor() : BasePresenter<FastGameView>() {

    @Inject
    lateinit var gamesRepository: GameRepository
    @Inject
    lateinit var cardRepository: CardRepository
    @Inject
    lateinit var userRepository: UserRepository

    lateinit var timer: CountDownTimer

    private fun validateLobby(lobby: Lobby): Boolean {
        var flag = true
        if (!(lobby.cardNumber >= Const.CARD_NUMBER)) {
            viewState.showSnackBar(R.string.set_card_min)
            flag = false
        }
        if (lobby.epoch == null) {
            flag = false
        }
        return flag
    }

    fun createGame(lobby: Lobby, user: User) {
        if(validateLobby(lobby)) {
            viewState.showProgressDialog(R.string.wait_enemy)
            cardRepository.findMyCardsByEpoch(user.id, lobby.epochId).subscribe{ cards ->
                val cardNumber = cards.size
                Log.d(TAG_LOG, "enemy cards size = $cardNumber")
                if(cardNumber >= lobby.cardNumber) {
                    cardRepository.findMyCardsByEpoch(AppHelper.currentId, lobby.epochId).subscribe { myCards ->
                        val mySize = myCards.size
                        Log.d(TAG_LOG, "my cards size = $mySize")
                        if (mySize >= lobby.cardNumber) {
                            viewState.setStatus(Const.ONLINE_STATUS)
                            lobby.isFastGame = true
                            val playerData = LobbyPlayerData()
                            playerData.playerId = AppHelper.currentId
                            playerData.online = true
                            lobby.creator = playerData
                            val args = Bundle()
                            user?.id?.let { playGame(it, lobby) }
                        } else {
                            viewState.hideProgressDialog()
                            viewState.showSnackBar(R.string.you_dont_have_card_min)
                        }
                    }
                } else {
                    viewState.hideProgressDialog()
                    viewState.showSnackBar(R.string.enemy_doesnt_have_card_min)
                }
            }

        }
    }

    fun playGame(userId: String, lobby: Lobby) {
        userRepository.checkUserStatus(userId).subscribe{ isOnline ->
            if(isOnline) {
                Log.d(Const.TAG_LOG, "play fast game")
                gamesRepository.createFastLobby(userId,lobby).subscribe { e ->
                    gamesRepository.waitEnemy().subscribe { relation ->
                        timer.cancel()
                        if(relation.relation.equals(Const.IN_GAME_STATUS)) {
                            AppHelper.currentUser?.let {
                                timer.cancel()
                                it.status = Const.IN_GAME_STATUS
                                viewState.hideProgressDialog()
                                userRepository.changeUserStatus(it).subscribe()
                                viewState.removeStackDownTo(1)
                                val fragment = PlayGameFragment.newInstance()
                                viewState.pushFragments(fragment, true)
                            }
                        } else if(relation.relation.equals(Const.NOT_ACCEPTED)) {
                            viewState.hideProgressDialog()
                            viewState.showSnackBar(R.string.not_accepted)
                            gamesRepository.removeFastLobby(userId,lobby).subscribe()
                            viewState.changeStatus(false)
                        }
                    }
                    timer = object : CountDownTimer(20000, 1000) {

                        override fun onTick(millisUntilFinished: Long) {

                        }

                        override fun onFinish() {
                            viewState.hideProgressDialog()
                            viewState.showSnackBar(R.string.time_end)
                            gamesRepository.removeFastLobby(userId,lobby).subscribe()
                            viewState.changeStatus(false)
                        }

                    }
                    timer.start()
                }
            }
        }

    }



}