package com.example.historyquiz.model.auth

import com.google.gson.annotations.SerializedName

class LoginResult {
    @SerializedName("token")
    lateinit var token: String
    @SerializedName("user_id")
    lateinit var userId: String
}