package com.example.historyquiz.repository.user

import android.util.Log
import com.example.historyquiz.model.user.User
import com.example.historyquiz.repository.game.GameRepository
import com.example.historyquiz.repository.game.GameRepositoryImpl.Companion.TABLE_LOBBIES
import com.example.historyquiz.utils.AppHelper
import com.example.historyquiz.utils.AppHelper.Companion.currentUser
import com.example.historyquiz.utils.AppHelper.Companion.userInSession
import com.example.historyquiz.utils.Const.OFFLINE_STATUS
import com.example.historyquiz.utils.Const.TAG_LOG
import com.example.historyquiz.utils.RxUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import io.reactivex.Observable
import io.reactivex.Single
import java.util.*
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor() : UserRepository {

    private val databaseReference: DatabaseReference

    init {
        this.databaseReference = FirebaseDatabase.getInstance().reference.child(TABLE_NAME)
    }

    override fun createUser(user: User) {
        databaseReference.child(user.id!!).setValue(user) { databaseError, databaseReference ->
            if (databaseError != null) {
                Log.d(TAG, "database error = " + databaseError.message)
            }
            Log.d(TAG, "completed ")
        }
    }

    override fun readUserById(userId: String): Single<User> {
        val single: Single<User> = Single.create{ e ->
            val query: Query = databaseReference.child(userId)
            query.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user: User? = dataSnapshot.getValue(User::class.java)
                    user?.let { e.onSuccess(it) }
                }

            })
        }
        return single.compose(RxUtils.asyncSingle())
    }

    override fun findUsers(cardsIds: List<String>): Single<List<User>> {
        val single: Single<List<User>> = Single.create{ e ->
            Observable
                .fromIterable(cardsIds)
                .flatMap {
                    this.readUserById(it).toObservable()
                }
                .toList()
                .subscribe{users ->
                    e.onSuccess(users)
                }
        }
        return single.compose(RxUtils.asyncSingle())
    }

    override fun updateUser(user: User) {
        val updatedValues = HashMap<String, Any>()
        databaseReference.child(user.id!!).updateChildren(updatedValues)
    }

    override fun changeUserStatus(user: User): Single<Boolean> {
        Log.d(TAG_LOG,"chageUserStatus = ${user.status}")
        val single: Single<Boolean> = Single.create{e ->
            user.id.let { databaseReference.child(it).child(FIELD_STATUS).setValue(user.status) }
            user.lobbyId?.let { databaseReference.root.child(TABLE_LOBBIES).child(it).child(FIELD_STATUS).setValue(user.status) }
            e.onSuccess(true)
        }
        return single.compose(RxUtils.asyncSingle())
    }

    override fun changeJustUserStatus(status: String): Single<Boolean> {
        Log.d(TAG_LOG,"chageJustUserStatus = $status")
        val single: Single<Boolean> = Single.create{e ->
            if(userInSession) {
                currentUser?.let { user ->
                    user.status = status
                    user.id.let { databaseReference.child(it).child(FIELD_STATUS).setValue(user.status) }
                    user.lobbyId?.let {
                        databaseReference.root.child(TABLE_LOBBIES).child(it).child(FIELD_STATUS).setValue(user.status)
//                    databaseReference.root.child(GamesRepository.USERS_LOBBIES).child(user.id).child(it).child(FIELD_STATUS).setValue(user.status)
                    }
                    e.onSuccess(true)
                }
            }
        }
        return single.compose(RxUtils.asyncSingle())
    }

    override fun checkUserConnection(checkIt: () -> (Unit)) {
        if(AppHelper.userInSession) {
            AppHelper.currentUser.let {
                if(it.status.equals(OFFLINE_STATUS)) {
                    checkIt()
                }
                val myConnect = databaseReference.root.child(TABLE_NAME).child(it.id).child(FIELD_STATUS)
                myConnect.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (OFFLINE_STATUS.equals(snapshot.value) || it.status.equals(OFFLINE_STATUS)) {
                            Log.d(TAG_LOG, "my disconnect")
                            checkIt()
                        }

                    }

                })
                myConnect.onDisconnect().setValue(OFFLINE_STATUS)
            }
        }
    }

    companion object {

        private val TAG = "UserRepositoryImpl"

        const val TABLE_NAME = "users"

        const val FIELD_LOBBY_ID = "lobbyId"
        val FIELD_STATUS = "status"
    }
}
