package com.example.historyquiz.repository.epoch

import com.example.historyquiz.model.epoch.Epoch
import io.reactivex.Single

interface EpochRepository {

    fun findEpoch(id: String): Single<Epoch>

    fun findEpoches(): Single<List<Epoch>>

    fun createEpoch(epoch: Epoch): Single<Boolean>

}