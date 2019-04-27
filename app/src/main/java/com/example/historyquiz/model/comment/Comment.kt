package com.example.historyquiz.model.comment


import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
class Comment {

    var id: String? = null
    var content: String? = null
    lateinit var authorId: String
    var createdDate: Long = 0

    @Exclude
    @Transient
    var authorName: String? = null

    @Exclude
    @Transient
    var authorPhotoUrl: String? = null


    constructor() {}

    constructor(text: String) {
        this.content = text
        this.createdDate = Calendar.getInstance().timeInMillis
    }
}
