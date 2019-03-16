package com.example.historyquiz.repository.user

import android.util.Log
import com.example.historyquiz.model.user.User
import com.example.historyquiz.utils.RxUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import io.reactivex.Observable
import io.reactivex.Single
import java.util.*
import javax.inject.Inject

class UserRepository @Inject constructor(){
    private val databaseReference: DatabaseReference

    init {
        this.databaseReference = FirebaseDatabase.getInstance().reference.child(TABLE_NAME)
    }

    fun createUser(user: User) {
        databaseReference.child(user.id!!).setValue(user) { databaseError, databaseReference ->
            if (databaseError != null) {
                Log.d(TAG, "database error = " + databaseError.message)
            }
            Log.d(TAG, "completed ")
        }
    }

    fun readUser(userId: String): DatabaseReference {
        return databaseReference.child(userId)
    }

    fun readUserById(userId: String): Single<User> {
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

    fun findUsers(cardsIds: List<String>): Single<List<User>> {
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

    fun deleteUser(user: User) {
        databaseReference.child(user.id!!).removeValue()
    }

    fun updateUser(user: User) {
        val updatedValues = HashMap<String, Any>()
        databaseReference.child(user.id!!).updateChildren(updatedValues)
    }

    companion object {

        private val TAG = "UserRepository"

        const val TABLE_NAME = "users"

        const val FIELD_LOBBY_ID = "lobbyId"
        val FIELD_STATUS = "status"


        val currentId: String
            get() = Objects.requireNonNull<FirebaseUser>(FirebaseAuth.getInstance().currentUser).getUid()
    }
}
