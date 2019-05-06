package com.example.historyquiz.model.game

import com.example.historyquiz.model.epoch.Epoch
import com.example.historyquiz.model.user.User
import com.google.firebase.database.Exclude


class LobbyData {

    lateinit var id: String

    var cardNumber: Int = 5

    lateinit var epochId: String

    @Exclude
    lateinit var epoch: Epoch

    var usersIds: MutableList<String> = ArrayList()

    @Exclude
    var userList: MutableList<User> = ArrayList()
    @Exclude
    var likes: Double = 0.0
}