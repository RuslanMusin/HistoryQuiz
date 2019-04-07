package com.example.historyquiz.model.db_dop_models

import com.google.firebase.database.IgnoreExtraProperties

//КЛАСС ДЛЯ ТАБЛИЦ ID-ID
@IgnoreExtraProperties
open class ElementId : Identified {

    override lateinit var id: String

}
