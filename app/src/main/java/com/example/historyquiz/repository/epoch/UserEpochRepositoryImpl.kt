package com.example.historyquiz.repository.epoch

import android.util.Log
import com.example.historyquiz.model.epoch.Epoch
import com.example.historyquiz.model.epoch.LeaderStat
import com.example.historyquiz.model.epoch.UserEpoch
import com.example.historyquiz.model.game.Lobby
import com.example.historyquiz.model.test.Test
import com.example.historyquiz.model.user.User
import com.example.historyquiz.repository.user.UserRepository
import com.example.historyquiz.utils.AppHelper
import com.example.historyquiz.utils.Const
import com.example.historyquiz.utils.Const.GAME_LOSE_POINTS
import com.example.historyquiz.utils.Const.GAME_WIN_POINTS
import com.example.historyquiz.utils.Const.TAG_LOG
import com.example.historyquiz.utils.RxUtils
import com.google.firebase.database.*
import io.reactivex.Single
import javax.inject.Inject

class UserEpochRepositoryImpl @Inject constructor(): UserEpochRepository {

    @Inject
    lateinit var epochRepository: EpochRepository

    @Inject
    lateinit var leaderStatRepository: LeaderStatRepository

    @Inject
    lateinit var userRepository: UserRepository

    var databaseReference: DatabaseReference

    val TABLE_NAME = "users_epoches"

    private val FIELD_ID = "id"
    private val FIELD_USER_ID = "userId"
    private val FIELD_EPOCH_ID = "epochId"
    private val FIELD_SUM = "sum"
    private val FIELD_WIN = "win"
    private val FIELD_LOSE = "lose"
    private val FIELD_GE = "ge"
    private val FIELD_LAST_GE = "lastGe"
    private val FIELD_GE_SUB = "geSub"
    private val FIELD_RIGHT = "right"
    private val FIELD_WRONG = "wrong"
    private val FIELD_KE = "ke"
    private val FIELD_LAST_KE = "lastKe"
    private val FIELD_KE_SUB = "keSub"
    private val FIELD_UPDATE_DATE = "updateDate"



    init {
        this.databaseReference = FirebaseDatabase.getInstance().reference.child(TABLE_NAME)
    }

    fun toMap(card: UserEpoch?): Map<String, Any?> {
        val result = HashMap<String, Any?>()

        card?.let {
            result[FIELD_ID] = card.id
            result[FIELD_USER_ID] = card.userId
            result[FIELD_EPOCH_ID] = card.epochId
            result[FIELD_SUM] = card.sum
            result[FIELD_WIN] = card.win
            result[FIELD_LOSE] = card.lose

            result[FIELD_GE] = card.ge
            result[FIELD_LAST_GE] = card.lastGe
            result[FIELD_GE_SUB] = card.geSub
            result[FIELD_RIGHT] = card.right
            result[FIELD_WRONG] = card.wrong
            result[FIELD_KE] = card.ke
            result[FIELD_LAST_KE] = card.lastKe
            result[FIELD_KE_SUB] = card.keSub
            result[FIELD_UPDATE_DATE] = card.updateDate
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

    override fun findUserEpoch(userId: String, epochId: String): Single<UserEpoch> {
        val single: Single<UserEpoch> = Single.create{ e ->
            val query: Query = databaseReference.child(userId).child(epochId)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val epoch: UserEpoch = dataSnapshot.getValue(UserEpoch::class.java)!!
                    epochRepository.findEpoch(epoch.epochId).subscribe { ep ->
                        epoch.epoch = ep
                        e.onSuccess(epoch)
                    }

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d(Const.TAG_LOG, "loadPost:onCancelled", databaseError.toException())
                }
            })

        }
        return single.compose(RxUtils.asyncSingle())
    }

    override fun findUserEpoches(userId: String): Single<List<UserEpoch>> {
        val single: Single<List<UserEpoch>> = Single.create{ e ->
            epochRepository.findEpoches().subscribe { epoches ->
                val query: Query = databaseReference.child(userId)
                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val comments: MutableList<UserEpoch> = ArrayList()
                        for (postSnapshot in dataSnapshot.children) {
                            val userEpoch = postSnapshot.getValue(UserEpoch::class.java)!!
                            userEpoch.epoch = findEpochById(epoches, userEpoch.epochId)
                            comments.add(userEpoch)
                        }
                        e.onSuccess(comments)

                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.d(Const.TAG_LOG, "loadPost:onCancelled", databaseError.toException())
                    }
                })
            }


        }
        return single.compose(RxUtils.asyncSingle())
    }

    override fun findEpochById(epoches: List<Epoch>, epochId: String): Epoch {
        for(ep in epoches) {
            if(ep.id.equals(epochId)) {
                return ep
            }
        }
        return Epoch()
    }

    override fun updateUserEpoch(userEpoch: UserEpoch): Single<Boolean> {
        val single: Single<Boolean> = Single.create { e ->

            val ref : DatabaseReference = databaseReference.child(userEpoch.userId).child(userEpoch.id)
            ref.setValue(userEpoch).addOnCompleteListener { lis ->
                e.onSuccess(true)
            }
            /* query.addListenerForSingleValueEvent(object : ValueEventListener {
                 override fun onDataChange(dataSnapshot: DataSnapshot) {
                     databaseReference.child(userEpoch.userId).child(userEpoch.id).setValue(userEpoch)
                     e.onSuccess(true)
                 }

                 override fun onCancelled(databaseError: DatabaseError) {
                     Log.d(Const.TAG_LOG, "loadPost:onCancelled", databaseError.toException())
                 }
             })*/

        }
        return single.compose(RxUtils.asyncSingle())
    }

    override fun createStartEpoches(user: User) {
        epochRepository.findEpoches().subscribe { epoches ->
            for(item in epoches) {
                updateUserEpoch(UserEpoch(item, user)).subscribe { e ->
                    Log.d(TAG_LOG, "updated user epoch ${item.name}")
                }
            }
        }

    }

    override fun updateAfterGame(lobby: Lobby, playerId: String?, isWin: Boolean, score: Int): Single<Boolean> {
        Log.d(TAG_LOG, "update after game")
        val single: Single<Boolean> = Single.create { e ->
            playerId?.let {
                userRepository.readUserById(it).subscribe { user ->
                    findUserEpoch(it, lobby.epochId).subscribe { epoch ->
                        Log.d(TAG_LOG, "find epoch after game")
                        if (isWin) {
                            epoch.win++
                            user.points += GAME_WIN_POINTS
                        } else {
                            epoch.lose++
                            user.points += GAME_LOSE_POINTS
                        }
                        if (user.points >= user.nextLevel) {
                            user.nextLevel = (1.5 * user.points + 20 * user.level).toLong()
                            user.level++
                            user.points = 0
                        }
                        epoch.updateGe()
                        userRepository.updateUser(user)
                        Log.d(TAG_LOG, "after update user")
                        if (user.id.equals(AppHelper.currentUser.id)) {
                            AppHelper.currentUser = user
                        }
                        epoch.right += score
                        epoch.wrong += (lobby.cardNumber - score)
                        updateUserEpoch(epoch).subscribe { e ->
                            findUserEpoches(playerId).subscribe { epoches ->
                                user.epochList = epoches.toMutableList()
                                val leaderStat = LeaderStat(user)
                                Log.d(TAG_LOG, "create leader stat")
                                leaderStatRepository.updateLeaderStat(leaderStat).subscribe { e ->
                                    Log.d(TAG_LOG, "created leaderStat")
                                }
                            }
                        }
                    }

                }
            }
        }
        return single.compose(RxUtils.asyncSingle())
    }

    override fun updateAfterTest(userId: String, test: Test): Single<Boolean> {
        val single: Single<Boolean> = Single.create { e ->
            findUserEpoch(userId, test.epochId).subscribe { userEpoch ->
                Log.d(TAG_LOG, "epoch was finded")
                val right = test.rightQuestions.size
                val wrong = test.wrongQuestions.size
                userEpoch.right += right
                userEpoch.wrong += wrong
                userEpoch.ke += ((right - wrong).toDouble() / (right + wrong))
                Log.d(TAG_LOG, "userEpoch.ke = ${userEpoch.ke}")
                updateUserEpoch(userEpoch).subscribe { flag  -> e.onSuccess(true)}
            }
        }
        return single.compose(RxUtils.asyncSingle())

    }

}
