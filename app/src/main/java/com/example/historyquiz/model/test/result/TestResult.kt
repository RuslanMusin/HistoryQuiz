package com.example.historyquiz.model.test.result

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class TestResult {

    lateinit var id: String

    var questions: MutableList<QuestionResult> = ArrayList()

}
