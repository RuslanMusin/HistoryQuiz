package com.example.historyquiz.model.user

import com.example.historyquiz.utils.Const.OFFLINE_STATUS
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class User {

    lateinit var id: String

    var email: String? = null

    var username: String? = null

    var lowerUsername: String? = null

    var photoUrl: String? = null

    var desc: String? = null

    var score: String? = null

    var role: String? = null

    var status: String = OFFLINE_STATUS

    constructor() {}

    constructor(email: String, username: String) {
        this.email = email
        this.username = username
    }

    constructor(email: String) {
        this.email = email
    }
}
