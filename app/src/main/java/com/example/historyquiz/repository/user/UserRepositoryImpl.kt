package com.example.historyquiz.repository.user

import android.util.Log
import com.example.historyquiz.model.db_dop_models.Relation
import com.example.historyquiz.model.user.User
import com.example.historyquiz.repository.epoch.UserEpochRepository
import com.example.historyquiz.repository.game.GameRepositoryImpl.Companion.TABLE_LOBBIES
import com.example.historyquiz.utils.AppHelper
import com.example.historyquiz.utils.AppHelper.Companion.currentUser
import com.example.historyquiz.utils.AppHelper.Companion.userInSession
import com.example.historyquiz.utils.Const.ADD_FRIEND
import com.example.historyquiz.utils.Const.OFFLINE_STATUS
import com.example.historyquiz.utils.Const.ONLINE_STATUS
import com.example.historyquiz.utils.Const.QUERY_END
import com.example.historyquiz.utils.Const.REMOVE_FRIEND
import com.example.historyquiz.utils.Const.REMOVE_REQUEST
import com.example.historyquiz.utils.Const.SEP
import com.example.historyquiz.utils.Const.TAG_LOG
import com.example.historyquiz.utils.Const.USER_FRIENDS
import com.example.historyquiz.utils.RxUtils
import com.google.firebase.database.*
import dagger.Lazy
import io.reactivex.Observable
import io.reactivex.Single
import java.util.*
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor() : UserRepository {

    private val databaseReference: DatabaseReference

    @Inject
    lateinit var userEpochRepository: Lazy<UserEpochRepository>

    init {
        this.databaseReference = FirebaseDatabase.getInstance().reference.child(TABLE_NAME)
    }

    override fun createUser(user: User) {
        databaseReference.child(user.id!!).setValue(user) { databaseError, databaseReference ->
            if (databaseError != null) {
                Log.d(TAG, "database error = " + databaseError.message)
            }
            Log.d(TAG, "completed")
            userEpochRepository.get().createStartEpoches(user)
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

    override fun checkUserStatus(userId: String): Single<Boolean> {
        val single: Single<Boolean> = Single.create{e ->
            readUserById(userId).subscribe{user ->
                if(user.status.equals(ONLINE_STATUS)) {
                    e.onSuccess(true)
                } else {
                    e.onSuccess(false)
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

    override fun loadDefaultUsers(): Single<List<User>> {
        val single: Single<List<User>> = Single.create { e ->
            databaseReference.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val users: MutableList<User> = ArrayList()
                    for(snapshot in dataSnapshot.children) {
                        val user = snapshot.getValue(User::class.java)
                        user?.let { users.add(it) }
                    }
                    e.onSuccess(users)
                }

                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

            })
        }
        return single.compose(RxUtils.asyncSingle())
    }

    override fun loadUsersByQuery(query: String): Single<List<User>> {
        val queryPart = query.trim { it <= ' ' }.toLowerCase()
        val single: Single<List<User>> = Single.create { e ->
            val queryName = databaseReference.orderByChild(FIELD_LOWER_NAME).startAt(queryPart).endAt(queryPart + QUERY_END)
            queryName.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val users: MutableList<User> = ArrayList()
                    for(snapshot in dataSnapshot.children) {
                        val user = snapshot.getValue(User::class.java)
                        user?.let { users.add(it) }
                    }
                    e.onSuccess(users)
                }

                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

            })
        }
        return single.compose(RxUtils.asyncSingle())
    }

    override fun findUsersByTypeByQuery(userQuery: String, userId: String, type: String): Single<List<User>> {
        var query: Query = databaseReference.root.child(USER_FRIENDS).child(userId).orderByChild(FIELD_RELATION).equalTo(type)
        val single: Single<List<User>> = Single.create { e ->
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val elementIds: MutableList<String> = ArrayList()
                    for (snapshot in dataSnapshot.children) {
                        val elementId = snapshot.getValue(Relation::class.java)
                        elementId?.let {
                            elementIds.add(it.id)
                        }
                    }
                    val queryPart = userQuery.toLowerCase()
                    query = databaseReference.orderByChild(FIELD_LOWER_NAME).startAt(queryPart).endAt(queryPart + QUERY_END)
                    query.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val users: MutableList<User> = ArrayList()
                            for(snapshot in dataSnapshot.children) {
                                val user = snapshot.getValue(User::class.java)
                                if(elementIds.contains(user?.id)) {
                                    user?.let { users.add(it) }
                                }

                            }
                            e.onSuccess(users)
                        }

                        override fun onCancelled(databaseError: DatabaseError) {}
                    })

                }
                override fun onCancelled(databaseError: DatabaseError) {}
            })


        }
        return single.compose(RxUtils.asyncSingle())
    }

    override fun findUsersByIdAndType(userId: String, type: String): Single<List<User>> {
        val query: Query = databaseReference.root.child(USER_FRIENDS).child(userId).orderByChild(FIELD_RELATION).equalTo(type)
        val single: Single<List<User>> = Single.create { e ->
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val elementIds: MutableList<String> = ArrayList()
                    for (snapshot in dataSnapshot.children) {
                        val elementId = snapshot.getValue(Relation::class.java)
                        elementId?.let {
                            elementIds.add(it.id)
                        }
                    }
                    findUsers(elementIds).subscribe{ users ->
                        e.onSuccess(users)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })

        }

        return single.compose(RxUtils.asyncSingle())
    }

    override fun addFriend(userId: String, friendId: String) {
        val userValues = Relation.toMap(userId, REMOVE_FRIEND)
        val friendValues = Relation.toMap(friendId, REMOVE_FRIEND)
        val childUpdates = HashMap<String, Any>()
        childUpdates[USER_FRIENDS + SEP + userId + SEP + friendId] = friendValues
        childUpdates[USER_FRIENDS + SEP + friendId + SEP + userId] = userValues

        databaseReference.root.updateChildren(childUpdates)
    }

    override fun removeFriend(userId: String, friendId: String) {
        val userValues = Relation.toMap(userId, REMOVE_REQUEST)
        val childUpdates = HashMap<String, Any?>()
        childUpdates[USER_FRIENDS + SEP + userId + SEP + friendId] = null
        childUpdates[USER_FRIENDS + SEP + friendId + SEP + userId] = null

        databaseReference.root.updateChildren(childUpdates)
    }

    override fun addFriendRequest(userId: String, friendId: String) {
        val friendValues = Relation.toMap(friendId, REMOVE_REQUEST)
        val userValues = Relation.toMap(userId, ADD_FRIEND)
        val childUpdates = HashMap<String, Any>()
        childUpdates[USER_FRIENDS + SEP + userId + SEP + friendId] = friendValues
        childUpdates[USER_FRIENDS + SEP + friendId + SEP + userId] = userValues

        databaseReference.root.updateChildren(childUpdates)
    }

    override fun removeFriendRequest(userId: String, friendId: String) {
        val childUpdates = HashMap<String, Any?>()
        childUpdates[USER_FRIENDS + SEP + userId + SEP + friendId] = null
        childUpdates[USER_FRIENDS + SEP + friendId + SEP + userId] = null

        databaseReference.root.updateChildren(childUpdates)
    }

    override fun checkType(userId: String, friendId: String): Query {
        return databaseReference.root.child(USER_FRIENDS).child(userId).child(friendId)
    }

    companion object {

        private val TAG = "UserRepositoryImpl"

        const val TABLE_NAME = "users"

        const val FIELD_LOBBY_ID = "lobbyId"
        val FIELD_STATUS = "status"
        const val FIELD_ID = "id"
        const val FIELD_NAME = "username"
        const val FIELD_LOWER_NAME = "lowerUsername"
        const val FIELD_RELATION = "relation"
    }
}
