package com.example.historyquiz.repository

import com.example.historyquiz.repository.user.UserRepositoryImpl

class RepositoryProvider {

    companion object {

        val userRepository: UserRepositoryImpl by lazy {
            UserRepositoryImpl()
        }
    }
}