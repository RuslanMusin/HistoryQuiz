package com.example.historyquiz.model.test.result

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class QuestionResult {

    var answers: MutableList<AnswerResult> = ArrayList()

    var userRight: Boolean = false

}