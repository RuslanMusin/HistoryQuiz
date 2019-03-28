package com.example.historyquiz.repository.card

import com.example.historyquiz.model.comment.Comment
import io.reactivex.Single

interface CommentRepository {

    fun getComments(type: String, testId: String): Single<List<Comment>>

    fun createComment(type: String, elemId: String, comment: Comment): Single<Boolean>

}