package com.example.historyquiz.repository

import android.util.Log

class RepositoryProvider {

    companion object {

        val userRepository: UserRepository by lazy {
            UserRepository()
        }
    }
}