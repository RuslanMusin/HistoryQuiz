package com.example.historyquiz.repository.epoch

import com.example.historyquiz.model.epoch.Epoch
import com.example.historyquiz.model.epoch.UserEpoch
import com.example.historyquiz.model.game.Lobby
import com.example.historyquiz.model.test.Test
import com.example.historyquiz.model.user.User
import io.reactivex.Single

interface UserEpochRepository {

    fun findUserEpoch(userId: String, epochId: String): Single<UserEpoch>

    fun findUserEpoches(userId: String, hasDefault: Boolean): Single<List<UserEpoch>>

    fun findEpochById(epoches: List<Epoch>, epochId: String): Epoch

    fun updateUserEpoch(userEpoch: UserEpoch): Single<Boolean>

    fun createStartEpoches(user: User, hasDefault: Boolean)

    fun updateAfterGame(lobby: Lobby, playerId: String?, isWin: Boolean, score: Int): Single<Boolean>

    fun updateAfterTest(userId: String, test: Test): Single<Boolean>

}