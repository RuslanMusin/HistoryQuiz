package com.example.historyquiz.ui.game.game_list

import android.os.CountDownTimer
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.historyquiz.model.db_dop_models.Relation
import com.example.historyquiz.model.game.GameData
import com.example.historyquiz.model.game.Lobby
import com.example.historyquiz.model.game.LobbyPlayerData
import com.example.historyquiz.repository.card.CardRepository
import com.example.historyquiz.repository.game.GameRepository
import com.example.historyquiz.repository.game.GameRepositoryImpl.Companion.FIELD_CREATOR
import com.example.historyquiz.repository.game.GameRepositoryImpl.Companion.FIELD_INVITED
import com.example.historyquiz.repository.user.UserRepository
import com.example.historyquiz.ui.base.BasePresenter
import com.example.historyquiz.utils.AppHelper
import com.example.historyquiz.utils.AppHelper.Companion.currentId
import com.example.historyquiz.utils.Const
import com.example.historyquiz.utils.Const.BOT_GAME
import com.example.historyquiz.utils.Const.BOT_ID
import com.example.historyquiz.utils.Const.IN_GAME_STATUS
import com.example.historyquiz.utils.Const.ONLINE_GAME
import com.example.historyquiz.utils.Const.TAG_LOG
import com.example.historyquiz.utils.Const.USER_TYPE
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import javax.inject.Inject

@InjectViewState
class GameListPresenter @Inject constructor() : BasePresenter<GameListView>() {

    lateinit var timer: CountDownTimer

    @Inject
    lateinit var gameRepository: GameRepository

    @Inject
    lateinit var cardRepository: CardRepository

    @Inject
    lateinit var userRepository: UserRepository

    fun removeLobby(lobbyId: String) {
        gameRepository.removeLobby(lobbyId)
    }

    fun loadOfficialTestsByQuery(query: String) {
        AppHelper.currentUser?.id?.let {
            gameRepository
                .findOfficialTestsByQuery(query, it)
                .doOnSubscribe(Consumer<Disposable> { viewState.showListLoading() })
                .doAfterTerminate(Action { viewState.hideListLoading() })
                .subscribe({ viewState.changeDataSet(it) }, { viewState.handleError(it) })
        }
    }

    fun loadOfficialTests() {
        Log.d(Const.TAG_LOG, "load books")
        AppHelper.currentUser?.id?.let { it ->
            val disposable = gameRepository
                .findOfficialTests(it)
                /*.doOnSubscribe({ viewState.showLoading() })
                .doAfterTerminate({ viewState.hideLoading() })
                .doAfterTerminate({ viewState.setNotLoading() })*/
                .subscribe({games ->
                    Log.d(TAG_LOG, "change games")
                    viewState.hideLoading()
                    viewState.showGames(games) }, { viewState.handleError(it) })
            compositeDisposable.add(disposable)

        }
    }

    fun onItemClick(lobby: Lobby) {
        if(!lobby.id.equals(AppHelper.currentUser?.lobbyId)) {
            viewState.showDetails(lobby)
        } else {
            viewState.showSnackBar("Вы не можете присоединиться к созданной вами игре")
        }
    }

    fun findGame(lobby: Lobby) {
        Log.d(TAG_LOG,"find game online")
        lobby.creator?.playerId?.let {creatorId ->
            val gameData: GameData = GameData()
            gameData.enemyId = creatorId
            userRepository.readUserById(creatorId).subscribe { enemy ->
                if(!lobby.id.equals(enemy.lobbyId)) {
                    viewState.showSnackBar("Игра была удалена. Список будет обновлен")
                    loadOfficialTests()
                    viewState.setItemClickable(true)
                } else {
                    cardRepository.findMyCardsByEpoch(gameData.enemyId, lobby.epochId).subscribe { enemyCards ->
                        val cardsSize = enemyCards.size
                        if (cardsSize >= lobby.cardNumber) {
                            gameData.role = FIELD_INVITED
                            gameData.gameMode = ONLINE_GAME
                            lobby.gameData = gameData
                            AppHelper.currentUser?.let { it.gameLobby = lobby }
//                viewState.showProgressDialog(R.string.loading)
                            timer = object : CountDownTimer(25000, 1000) {

                                override fun onTick(millisUntilFinished: Long) {
                                }

                                override fun onFinish() {
                                    viewState.hideProgressDialog()
                                    gameRepository.notAccepted(lobby)
                                }
                            }
                            timer.start()
                            gameRepository.goToLobby(lobby, gameFinded(), gameNotAccepted(lobby))
                        } else {
                            viewState.showSnackBar("У противника не хватает карт для игры")
                            viewState.setItemClickable(true)
                        }
                    }
                }
            }

        }
    }

    fun gameFinded(): () -> Unit {
        return {
            timer.cancel()
            viewState.onGameFinded()
        }
    }

    fun gameNotAccepted(lobby: Lobby): () -> Unit {
        return  {
            timer.cancel()
            viewState.setWaitStatus(true)
            viewState.hideProgressDialog()
            gameRepository.notAccepted(lobby)
        }
    }
}
