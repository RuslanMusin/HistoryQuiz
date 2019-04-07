package com.example.historyquiz.repository.epoch

import android.util.Log
import com.example.historyquiz.model.epoch.LeaderStat
import com.example.historyquiz.model.user.User
import com.example.historyquiz.utils.Const
import com.example.historyquiz.utils.RxUtils
import com.google.firebase.database.*
import io.reactivex.Single

class LeaderStatRepositoryImpl : LeaderStatRepository {

    var databaseReference: DatabaseReference

    val TABLE_NAME = "leader_stats"
    val USERS_CARDS = "users_cards"
    val USERS_TESTS = "users_tests"
    val USERS_ABSTRACT_CARDS = "users_abstract_cards"


    private val FIELD_ID = "id"
    private val FIELD_NAME = "name"
    private val FIELD_LEVEL = "level"
    private val FIELD_KG = "kg"
    private val FIELD_WIN = "win"
    private val FIELD_LOSE = "lose"

    init {
        this.databaseReference = FirebaseDatabase.getInstance().reference.child(TABLE_NAME)
    }

    fun toMap(card: LeaderStat?): Map<String, Any?> {
        val result = HashMap<String, Any?>()

        card?.let {
            result[FIELD_ID] = card.id
            result[FIELD_NAME] = card.name
            result[FIELD_LEVEL] = card.level
            result[FIELD_KG] = card.kg
            result[FIELD_WIN] = card.win
            result[FIELD_LOSE] = card.lose
        }
        return result
    }

    fun toMapId( value: String?): Map<String, Any?> {
        val result = HashMap<String, Any?>()
        result[FIELD_ID] = value
        return result
    }


    fun setDatabaseReference(path: String) {
        this.databaseReference = FirebaseDatabase.getInstance().reference.child(TABLE_NAME)
    }

    fun getKey(crossingId: String): String? {
        return databaseReference!!.child(crossingId).push().key
    }

    override fun findStats(user: User): Single<List<LeaderStat>> {
        val single: Single<List<LeaderStat>> = Single.create{ e ->
            val query: Query = databaseReference.orderByChild(FIELD_KG)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val comments: MutableList<LeaderStat> = ArrayList()
                    for (postSnapshot in dataSnapshot.children) {
                        val stat = postSnapshot.getValue(LeaderStat::class.java)!!
                        if(Math.abs((user.level - stat.level)) <= 3) {
                            comments.add(stat)
                        }
                        if(comments.size == 100) {
                            break
                        }
                    }
                    e.onSuccess(comments)

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d(Const.TAG_LOG, "loadPost:onCancelled", databaseError.toException())
                }
            })

        }
        return single.compose(RxUtils.asyncSingle())
    }

    override fun updateLeaderStat(stat: LeaderStat): Single<Boolean> {
        val single: Single<Boolean> = Single.create { e ->
            val query: Query = databaseReference.child(stat.id)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    databaseReference.child(stat.id).setValue(stat)
                    e.onSuccess(true)

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d(Const.TAG_LOG, "loadPost:onCancelled", databaseError.toException())
                }
            })

        }
        return single.compose(RxUtils.asyncSingle())
    }

}
