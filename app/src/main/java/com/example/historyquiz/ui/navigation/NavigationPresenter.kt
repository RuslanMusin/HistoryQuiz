package com.example.historyquiz.ui.navigation

import android.os.Bundle
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.historyquiz.R
import com.example.historyquiz.model.game.GameData
import com.example.historyquiz.model.game.Lobby
import com.example.historyquiz.repository.game.GameRepository
import com.example.historyquiz.repository.game.GameRepositoryImpl
import com.example.historyquiz.repository.user.UserRepository
import com.example.historyquiz.ui.base.BasePresenter
import com.example.historyquiz.utils.AppHelper
import com.example.historyquiz.utils.AppHelper.Companion.currentId
import com.example.historyquiz.utils.AppHelper.Companion.currentUser
import com.example.historyquiz.utils.AppHelper.Companion.userStatus
import com.example.historyquiz.utils.Const
import com.example.historyquiz.utils.Const.ONLINE_GAME
import com.example.historyquiz.utils.Const.TAG_LOG
import com.example.historyquiz.utils.Const.gson
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

@InjectViewState
class NavigationPresenter @Inject constructor() : BasePresenter<NavigationView>() {

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var gameRepository: GameRepository
    @Inject
    lateinit var fireAuth: FirebaseAuth

    var isStopped: Boolean = false
    var isWaiting: Boolean = false

    fun setStatus(status: String) {
        Log.d(TAG_LOG, "current status = $status")
        userStatus = status
        userRepository.changeJustUserStatus(status).subscribe()
    }

    fun refuseGame(lobby: Lobby) {
        gameRepository.refuseGame(lobby).subscribe { e -> waitEnemy() }
    }

    fun changeJustUserStatus(status: String) {
        userRepository.changeJustUserStatus(Const.STOP_STATUS).subscribe()
    }

    fun checkUserConnection(offlineMode: () -> Unit) {
        userRepository.checkUserConnection(offlineMode)
    }

    fun waitEnemy() {
        gameRepository.waitEnemy().subscribe { relation ->
            if (isWaiting && !isStopped) {
                Log.d(TAG_LOG, "enemy waited")
                if (relation.relation.equals(Const.IN_GAME_STATUS)) {
                    gameRepository.findLobby(relation.id).subscribe { lobby ->
                        Log.d(TAG_LOG, "waited lobby finded/ isStopped = $isStopped")
                        AppHelper.currentUser.let {
                            it.gameLobby = lobby
                            val gameData: GameData = GameData()
                            gameData.gameMode = ONLINE_GAME
                            val invitedId = lobby.invited?.playerId
                            val creatorId = lobby.creator?.playerId
                            if (invitedId != null && creatorId.equals(currentId)) {
                                invitedId.let {
                                    gameData.enemyId = it
                                    gameData.role = GameRepositoryImpl.FIELD_CREATOR
                                }
                            } else {
                                creatorId?.let {
                                    gameData.enemyId = it
                                    gameData.role = GameRepositoryImpl.FIELD_INVITED
                                }
                            }
                            it.gameLobby?.gameData = gameData
                            Log.d(TAG_LOG, "setDialog")
                            viewState.setDialog(gameData, lobby)
                        }
                    }
                }
            } else {
                waitEnemy()
            }
        }
    }


    fun chooseDialogDecision(gameData: GameData, lobby: Lobby) {
        Log.d(TAG_LOG,"choose dialog decision")
        userRepository.checkUserStatus(gameData.enemyId).subscribe { isOnline ->
            viewState.hideDialog()
            if (isOnline) {
                userRepository.changeUserStatus(currentUser).subscribe()
                gameRepository.acceptMyGame(lobby).subscribe { e ->
                    Log.d(TAG_LOG,"play game after wait")
                    viewState.goToGame()
                }
            } else {
                viewState.showSnackBar(R.string.enemy_not_online)
                waitEnemy()
            }
        }
    }

    fun signIn(email: String, password: String) {
        fireAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener({ task ->
                if (task.isSuccessful) {
                    val user = fireAuth!!.currentUser
                    viewState.createCookie(email, password)
                    userRepository?.readUserById(currentId)
                        .subscribe { user ->
                            user?.let {
                                Log.d(TAG_LOG, "have user")
                                AppHelper.currentUser = it
                                it.status = Const.ONLINE_STATUS
                                userRepository.changeUserStatus(it).subscribe()
                                gameRepository.removeRedundantLobbies(true)
                                val args = Bundle()
                                args.putString(Const.USER_ITEM, gson.toJson(currentUser))
                                viewState.openNavigationPage()
                            }
                        }
                }
            })
    }
}