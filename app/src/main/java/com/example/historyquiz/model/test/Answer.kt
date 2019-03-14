package com.example.historyquiz.model.test

import com.google.firebase.database.Exclude
import com.google.gson.annotations.Expose

class Answer {

    var text: String? = null

    var isRight: Boolean = false

    var userClicked: Boolean = false
}
