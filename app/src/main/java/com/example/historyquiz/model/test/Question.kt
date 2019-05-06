package com.example.historyquiz.model.test

import com.example.historyquiz.utils.Const.TEST_ONE_TYPE
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class Question {

    var id: String? = null

    var question: String? = null

    var answers: MutableList<Answer> = ArrayList()

    @field:Exclude var userRight: Boolean = false

    var type: String = TEST_ONE_TYPE

}
