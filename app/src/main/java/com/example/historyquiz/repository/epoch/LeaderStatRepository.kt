package com.example.historyquiz.repository.epoch

import com.example.historyquiz.model.epoch.LeaderStat
import com.example.historyquiz.model.user.User
import io.reactivex.Single

interface LeaderStatRepository {

    fun findStats(user: User): Single<List<LeaderStat>>

    fun updateLeaderStat(stat: LeaderStat): Single<Boolean>

}