package com.example.historyquiz.ui.profile.item

import android.os.Bundle
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.historyquiz.R
import com.example.historyquiz.model.db_dop_models.Relation
import com.example.historyquiz.model.user.User
import com.example.historyquiz.repository.card.CardRepository
import com.example.historyquiz.repository.game.GameRepository
import com.example.historyquiz.repository.user.UserRepository
import com.example.historyquiz.ui.base.BasePresenter
import com.example.historyquiz.ui.game.fast_game.FastGameFragment
import com.example.historyquiz.utils.AppHelper.Companion.currentId
import com.example.historyquiz.utils.Const.ADD_REQUEST
import com.example.historyquiz.utils.Const.OWNER_TYPE
import com.example.historyquiz.utils.Const.TAG_LOG
import com.example.historyquiz.utils.Const.USER_ITEM
import com.example.historyquiz.utils.Const.gson
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

@InjectViewState
class ProfilePresenter @Inject constructor() : BasePresenter<ProfileView>() {

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

    fun playGameClick(user: User) {
        viewState.changePlayButton(false)
        userRepository.checkUserStatus(user.id).subscribe { isOnline ->
            if (isOnline) {
                val args = Bundle()
                args.putString(USER_ITEM, gson.toJson(user))
                val fragment = FastGameFragment.newInstance(args)
                viewState.pushFragments(fragment, true)
            } else {
                viewState.showSnackBar(R.string.enemy_not_online)
                viewState.changePlayButton(true)
            }
        }
    }

}