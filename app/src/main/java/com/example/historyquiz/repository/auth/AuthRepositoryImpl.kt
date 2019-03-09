package com.example.historyquiz.repository.auth

import com.example.historyquiz.api.services.AuthService
import com.example.historyquiz.model.auth.LoginResult
import com.example.historyquiz.model.user.User
import com.example.historyquiz.utils.RxUtils
import io.reactivex.Single
import retrofit2.adapter.rxjava2.Result
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor() : AuthRepository {

    @Inject
    lateinit var authService: AuthService

    override fun login(user: User): Single<Result<LoginResult>> {
        return authService
            .login(user)
            .compose(RxUtils.asyncSingle())
    }
}