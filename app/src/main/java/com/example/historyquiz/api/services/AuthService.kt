package com.example.historyquiz.api.services

import com.example.historyquiz.model.auth.LoginResult
import com.example.historyquiz.model.user.User
import io.reactivex.Single
import retrofit2.adapter.rxjava2.Result
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

    @POST("login")
    fun login(@Body user: User): Single<Result<LoginResult>>
}