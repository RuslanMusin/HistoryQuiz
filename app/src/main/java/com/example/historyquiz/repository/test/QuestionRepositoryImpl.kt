package com.example.historyquiz.repository.test

import com.example.historyquiz.model.test.Question
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

import java.util.HashMap
import javax.inject.Inject

class QuestionRepositoryImpl @Inject constructor(){

    var databaseReference: DatabaseReference? = null
        private set

    val TABLE_NAME = "questions"

    private val FIELD_ID = "id"
    private val FIELD_QUESTION = "question"
    private val FIELD_PHOTO = "photoUrl"
    private val FIELD_WIKI = "wikiUrl"
    private val FIELD_ANSWERS = "answers"
    private val FIELD_RIGHT_ANSWERS = "right_answers"

    init {
        this.databaseReference = FirebaseDatabase.getInstance().reference.child(TABLE_NAME)
    }

    fun toMap(question: Question): Map<String, Any?> {
        val result = HashMap<String, Any?>()

        val id = databaseReference!!.push().key
        question.id = id
        result[FIELD_ID] = question.id
        result[FIELD_QUESTION] = question.question
        result[FIELD_ANSWERS] = question.answers

        return result
    }

    fun setDatabaseReference(path: String) {
        this.databaseReference = FirebaseDatabase.getInstance().reference.child(TABLE_NAME)
    }

    fun getKey(crossingId: String): String? {
        return databaseReference!!.child(crossingId).push().key
    }
}
