package com.example.historyquiz.repository

import com.example.historyquiz.repository.user.UserRepository

class RepositoryProvider {

    companion object {

        val userRepository: UserRepository by lazy {
            UserRepository()
        }
    }
}