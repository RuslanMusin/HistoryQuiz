package com.example.historyquiz.ui.profile.item

import android.os.CountDownTimer
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.historyquiz.R
import com.example.historyquiz.model.db_dop_models.Relation
import com.example.historyquiz.model.game.Lobby
import com.example.historyquiz.model.game.LobbyPlayerData
import com.example.historyquiz.model.user.User
import com.example.historyquiz.repository.card.CardRepository
import com.example.historyquiz.repository.game.GameRepository
import com.example.historyquiz.repository.user.UserRepository
import com.example.historyquiz.ui.base.BasePresenter
import com.example.historyquiz.ui.game.play.PlayGameFragment
import com.example.historyquiz.utils.AppHelper
import com.example.historyquiz.utils.AppHelper.Companion.currentId
import com.example.historyquiz.utils.Const
import com.example.historyquiz.utils.Const.ADD_REQUEST
import com.example.historyquiz.utils.Const.IN_GAME_STATUS
import com.example.historyquiz.utils.Const.NOT_ACCEPTED
import com.example.historyquiz.utils.Const.OWNER_TYPE
import com.example.historyquiz.utils.Const.TAG_LOG
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

@InjectViewState
class ProfilePresenter @Inject constructor() : BasePresenter<ProfileView>() {

    lateinit var timer: CountDownTimer

    @Inject
    lateinit var userRepository: UserRepository
    @Inject
    lateinit var cardRepository: CardRepository
    @Inject
    lateinit var gamesRepository: GameRepository

    fun setUserRelationAndView(user: User) {
        var type: String = ""
        Log.d(TAG_LOG, "not my")
        if (user.id != currentId) {
            val query = user.id?.let { userRepository.checkType(currentId, it) }
            val listener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    if (dataSnapshot.exists()) {
                        val userRelation = dataSnapshot.getValue(Relation::class.java)
                        type = userRelation!!.relation
                    } else {
                        type = ADD_REQUEST
                    }
                    viewState.changeType(type)
                    viewState.initViews()
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            }

            query?.addListenerForSingleValueEvent(listener)
        } else {
            viewState.changeType(OWNER_TYPE)
            viewState.initViews()
        }
    }


    fun playGame(userId: String, lobby: Lobby) {
        userRepository.checkUserStatus(userId).subscribe{ isOnline ->
            if(isOnline) {
                Log.d(TAG_LOG, "play fast game")
                gamesRepository.createFastLobby(userId,lobby).subscribe { e ->
                    gamesRepository.waitEnemy().subscribe { relation ->
                        timer.cancel()
                        if(relation.relation.equals(IN_GAME_STATUS)) {
                            AppHelper.currentUser?.let {
                                timer.cancel()
                                it.status = IN_GAME_STATUS
                                userRepository.changeUserStatus(it).subscribe()
                                val fragment = PlayGameFragment.newInstance()
                                viewState.pushFragments(fragment, true)
                            }
                        } else if(relation.relation.equals(NOT_ACCEPTED)) {
                            Log.d(TAG_LOG, "user not accept")
                            viewState.hideProgressDialog()
                            viewState.changePlayButton(true)
                            gamesRepository.removeFastLobby(userId,lobby).subscribe()
                        }
                    }
                    timer = object : CountDownTimer(20000, 1000) {

                        override fun onTick(millisUntilFinished: Long) {

                        }

                        override fun onFinish() {
                            Log.d(TAG_LOG, "user not accept")
                            viewState.hideProgressDialog()
                            viewState.changePlayButton(true)
                            gamesRepository.removeFastLobby(userId,lobby).subscribe()
                        }

                    }
                    timer.start()
                }
            }
        }

    }

    fun playGameClick(userId: String) {
        viewState.changePlayButton(false)
        userRepository.checkUserStatus(userId).subscribe { isOnline ->
            if (isOnline) {
                viewState.showGameDialog()
            } else {
                viewState.showSnackBar(R.string.enemy_not_online)
                viewState.changePlayButton(true)
            }
        }
    }

    fun createGame(lobby: Lobby, user: User) {
        if(lobby.cardNumber >= Const.CARD_NUMBER) {
            cardRepository.findMyCardsByEpoch(user.id, lobby.epochId).subscribe{ cards ->
                val cardNumber = cards.size
                if(cardNumber >= lobby.cardNumber) {
                    cardRepository.findMyCardsByEpoch(currentId, lobby.epochId).subscribe { myCards ->
                        val mySize = myCards.size
                        if (mySize >= lobby.cardNumber) {
                            viewState.hideGameDialog()
                            viewState.showProgressDialog()
                            lobby.isFastGame = true
                            val playerData = LobbyPlayerData()
                            playerData.playerId = currentId
                            playerData.online = true
                            lobby.creator = playerData
                            user?.id?.let { playGame(it, lobby) }
                        } else {
                            viewState.showSnackBar(R.string.you_dont_have_card_min)
                        }
                    }
                } else {
                    viewState.showSnackBar(R.string.enemy_doesnt_have_card_min)
                }
            }

        } else {
            viewState.showSnackBar(R.string.set_card_min)
        }
    }

    fun addFriend(userId: String) {
        userRepository.addFriend(currentId, userId)
    }

    fun addFriendRequest(userId: String) {
        userRepository.addFriendRequest(currentId, userId)
    }

    fun removeFriend(userId: String) {
        userRepository.removeFriend(currentId, userId)
    }

    fun removeFriendRequest(userId: String) {
        userRepository.removeFriendRequest(currentId, userId)
    }


}