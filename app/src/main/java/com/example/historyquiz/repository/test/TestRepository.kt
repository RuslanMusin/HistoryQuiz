package com.example.historyquiz.repository.test

import com.example.historyquiz.model.db_dop_models.Relation
import com.example.historyquiz.model.test.Test
import com.example.historyquiz.model.test.result.TestResult
import com.example.historyquiz.model.user.User
import io.reactivex.Single

interface TestRepository {

    fun toMap(test: Test): Map<String, Any?>

//    fun toMap(id: String?,relation: String?): Map<String, Any?>

    fun toMap(id: String?): Map<String, Any?>

    fun changeStatus(testId: String, userId: String, relation: String): Single<Relation>

    fun createTest(test: Test, user: User): Single<Boolean>

    fun finishTest(test: Test, user: User): Single<Boolean>

    fun readTest(testId: String?): Single<Test>

    fun findTests(cardsIds: List<String>): Single<List<Test>>

    fun findTests(userId: String, type: String): Single<List<Test>>

    fun findTestsByQuery(userId: String, userQuery: String, type: String): Single<List<Test>>

    fun findUserAnswers(userId: String, testId: String): Single<TestResult>

    fun saveUserAnswers(userId: String, testResult: TestResult): Single<Boolean>
}