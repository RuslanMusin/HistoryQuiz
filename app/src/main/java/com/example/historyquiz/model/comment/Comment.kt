package com.example.historyquiz.model.comment


import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

import java.util.Calendar

@IgnoreExtraProperties
class Comment {

    var id: String? = null
    var text: String? = null
    var authorId: String? = null
    var createdDate: Long = 0

    @Exclude
    @Transient
    var authorName: String? = null

    @Exclude
    @Transient
    var authorPhotoUrl: String? = null


    constructor() {}

    constructor(text: String) {
        this.text = text
        this.createdDate = Calendar.getInstance().timeInMillis
    }
}
