package com.example.historyquiz.ui.profile.item

import com.example.historyquiz.ui.base.interfaces.BasicFunctional

interface ProfileView: BasicFunctional {

    fun handleError(throwable: Throwable)

    fun changePlayButton(isClickable: Boolean)

    fun changeType(type: String)

    fun initViews()

    fun deleteUserPrefs()

}