package com.example.historyquiz.repository.user

import com.example.historyquiz.model.user.User
import io.reactivex.Single

interface UserRepository {

    fun createUser(user: User)

    fun readUserById(userId: String): Single<User>

    fun findUsers(cardsIds: List<String>): Single<List<User>>

    fun updateUser(user: User)

    fun changeUserStatus(user: User): Single<Boolean>

    fun changeJustUserStatus(status: String): Single<Boolean>

    fun checkUserStatus(userId: String): Single<Boolean>

    fun checkUserConnection(checkIt: () -> (Unit))

    fun loadDefaultUsers(): Single<List<User>>

    fun loadUsersByQuery(query: String): Single<List<User>>

    fun findUsersByTypeByQuery(userQuery: String, userId: String, type: String): Single<List<User>>

    fun findUsersByIdAndType(userId: String, type: String): Single<List<User>>
}