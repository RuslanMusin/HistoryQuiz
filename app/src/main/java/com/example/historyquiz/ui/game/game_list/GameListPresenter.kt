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
                .doOnSubscribe(Consumer<Disposable> { viewState.showLoading() })
                .doAfterTerminate(Action { viewState.hideLoading() })
                .subscribe({ viewState.changeDataSet(it) }, { viewState.handleError(it) })
        }
    }

    fun loadOfficialTests() {
        Log.d(Const.TAG_LOG, "load books")
        AppHelper.currentUser?.id?.let {
            gameRepository
                .findOfficialTests(it)
                .doOnSubscribe({ viewState.showLoading() })
                .doAfterTerminate({ viewState.hideLoading() })
                .doAfterTerminate({ viewState.setNotLoading() })
                .subscribe({ viewState.changeDataSet(it) }, { viewState.handleError(it) })
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
        val gameData: GameData = GameData()
        lobby.creator?.playerId?.let{ gameData.enemyId = it}
        cardRepository.findMyCards(gameData.enemyId).subscribe{ enemyCards ->
            val cardsSize = enemyCards.size
            if(cardsSize >= lobby.cardNumber) {
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
            viewState.hideProgressDialog()
            gameRepository.notAccepted(lobby)
        }
    }

    fun findBotGame() {
        val lobby: Lobby = Lobby()

        val playerData = LobbyPlayerData()
        playerData.playerId = currentId
        playerData.online = true

        lobby.creator = playerData
        lobby.status = IN_GAME_STATUS
        lobby.isFastGame = true
        lobby.type = USER_TYPE

        val enemyData = LobbyPlayerData()
        enemyData.playerId = BOT_ID
        enemyData.online = true

        val gameData: GameData = GameData()
        gameData.enemyId = BOT_ID
        gameData.gameMode = BOT_GAME
        gameData.role = FIELD_CREATOR
        lobby.gameData = gameData

        AppHelper.currentUser?.let {
            it.gameLobby = lobby
            Log.d(TAG_LOG,"enemyId = ${lobby.gameData?.enemyId}")
            Log.d(TAG_LOG,"enemyId 2= ${it.gameLobby?.gameData?.enemyId}")
            gameRepository.createBotLobby(lobby) {
                //            viewState.waitEnemy()
                val relation: Relation = Relation()
                relation.relation = IN_GAME_STATUS
                relation.id = lobby.id
//                gameRepository.setRelation(relation, UserRepositoryImpl.currentId)
                userRepository.changeJustUserStatus(IN_GAME_STATUS).subscribe()
                viewState.onBotGameFinded()
                /*gameRepository.joinBot(lobby).subscribe{ e ->


                    Log.d(TAG_LOG,"gameLobby loaded")
                    Log.d(TAG_LOG,"change user status")
                    userRepository.changeUserStatus(it).subscribe()
                }
                viewState.onGameFinded()
            }*/
            }

        }
    }
}
