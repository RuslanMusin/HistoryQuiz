package com.example.historyquiz.repository.auth

import com.example.historyquiz.model.auth.LoginResult
import com.example.historyquiz.model.user.User
import io.reactivex.Single
import retrofit2.adapter.rxjava2.Result

interface AuthRepository {

    fun login(user: User): Single<Result<LoginResult>>
}