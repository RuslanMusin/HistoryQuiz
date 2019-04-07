package com.example.historyquiz.model.user

import com.example.historyquiz.model.game.Lobby
import com.example.historyquiz.model.epoch.UserEpoch
import com.example.historyquiz.utils.Const.OFFLINE_STATUS
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class User {

    lateinit var id: String

    var email: String? = null

    var username: String? = null

    var password: String? = null

    var lowerUsername: String? = null

    lateinit var photoUrl: String

    var score: String? = null

    var role: String? = null

    var status: String = OFFLINE_STATUS

    var lobbyId: String? = null

    @Exclude
    var gameLobby: Lobby? = null

    var epochList: MutableList<UserEpoch> = ArrayList()

    var level: Int = 1

    var points: Long = 0

    var nextLevel: Long = 60

    constructor() {}

    constructor(email: String, username: String) {
        this.email = email
        this.username = username
    }

    constructor(email: String) {
        this.email = email
    }
}
