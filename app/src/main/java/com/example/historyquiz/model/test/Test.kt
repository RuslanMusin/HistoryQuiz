package com.example.historyquiz.model.test

import com.example.historyquiz.model.card.Card
import com.example.historyquiz.model.comment.Comment
import com.example.historyquiz.model.db_dop_models.Relation
import com.example.historyquiz.model.epoch.Epoch
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class Test {

    var id: String? = null

    var title: String? = null

    var lowerTitle: String? = null

    var desc: String? = null

    var authorId: String? = null

    var authorName: String? = null

    var cardId: String? = null

    var questions: MutableList<Question> = ArrayList()

    var links: MutableList<Link> = ArrayList()

    var type: String? = null

    var imageUrl: String? = null

    lateinit var epochId: String

    @Exclude
    var comments: MutableList<Comment> = ArrayList()

    @Exclude
    var card: Card? = null

    @Exclude
    var testDone: Boolean = false

    @Exclude
    var epoch: Epoch? = null

    @field:Exclude var testRelation: Relation? = null

    @Exclude
    lateinit var rightQuestions: List<Question>

    @Exclude
    lateinit var wrongQuestions: List<Question>
}
