package com.example.historyquiz.ui.profile.item

import com.example.historyquiz.ui.base.interfaces.BasicFunctional

interface ProfileView: BasicFunctional {

    fun handleError(throwable: Throwable)

}