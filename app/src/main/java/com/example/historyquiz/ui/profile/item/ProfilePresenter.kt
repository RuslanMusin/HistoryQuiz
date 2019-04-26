package com.example.historyquiz.ui.profile.item

import android.os.CountDownTimer
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.historyquiz.model.game.Lobby
import com.example.historyquiz.model.user.User
import com.example.historyquiz.repository.api.WikiApiRepository
import com.example.historyquiz.repository.user.UserRepository
import com.example.historyquiz.ui.base.BasePresenter
import com.example.historyquiz.utils.AppHelper
import com.example.historyquiz.utils.Const.ADD_REQUEST
import com.example.historyquiz.utils.Const.IN_GAME_STATUS
import com.example.historyquiz.utils.Const.NOT_ACCEPTED
import com.example.historyquiz.utils.Const.TAG_LOG
import com.example.historyquiz.utils.Const.gson
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

@InjectViewState
class ProfilePresenter @Inject constructor() : BasePresenter<ProfileView>() {

    lateinit var timer: CountDownTimer

    fun setUserRelationAndView(userJson: String?) {
        if (userJson != null) {
            Log.d(TAG_LOG, "not my")
            val user = gson.fromJson(testActivity.intent.getStringExtra(USER_KEY), User::class.java)
            testActivity.user = user
            if (user.id != UserRepository.currentId) {
                val query = user.id?.let { UserRepository().checkType(UserRepository.currentId, it) }
                val listener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            val userRelation = dataSnapshot.getValue(Relation::class.java)
                            testActivity.type = userRelation!!.relation
                        } else {
                            testActivity.type = ADD_REQUEST
                        }
                        testActivity.initViews()
                    }

                    override fun onCancelled(databaseError: DatabaseError) {

                    }
                }

                query?.addListenerForSingleValueEvent(listener)
            } else {
                testActivity.type = OWNER_TYPE
            }
        } else {
            testActivity.type = OWNER_TYPE
        }

        if (OWNER_TYPE == testActivity.type) {
            testActivity.initViews()
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
                                PlayGameActivity.start(testActivity)
                            }
                        } else if(relation.relation.equals(NOT_ACCEPTED)) {
                            Log.d(TAG_LOG, "user not accept")
                            testActivity.hideProgressDialog()
                            testActivity.changePlayButton(true)
                            gamesRepository.removeFastLobby(userId,lobby).subscribe()
                        }
                    }
                    timer = object : CountDownTimer(20000, 1000) {

                        override fun onTick(millisUntilFinished: Long) {

                        }

                        override fun onFinish() {
                            Log.d(TAG_LOG, "user not accept")
                            testActivity.hideProgressDialog()
                            testActivity.changePlayButton(true)
                            gamesRepository.removeFastLobby(userId,lobby).subscribe()
                        }

                    }
                    timer.start()
                }
            }
        }

    }

}