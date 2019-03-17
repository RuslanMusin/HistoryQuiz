package com.example.historyquiz.repository.test

import com.example.historyquiz.model.auth.LoginResult
import com.example.historyquiz.model.card.Card
import com.example.historyquiz.model.db_dop_models.Relation
import com.example.historyquiz.model.test.Test
import com.example.historyquiz.model.user.User
import io.reactivex.Single
import retrofit2.adapter.rxjava2.Result

interface TestRepository {

    fun toMap(test: Test): Map<String, Any?>

    fun toMap(id: String?,relation: String?): Map<String, Any?>

    fun changeStatus(testId: String, userId: String, relation: String): Single<Relation>

    fun createTest(test: Test, user: User): Single<Boolean>

    fun finishTest(test: Test, user: User, winnerCards: List<Card>): Single<Boolean>

    fun readTest(testId: String?): Single<Test>

    fun findTests(cardsIds: List<String>): Single<List<Test>>

    fun findTests(): Single<List<Test>>

    fun findTestsByQuery(userQuery: String): Single<List<Test>>
}