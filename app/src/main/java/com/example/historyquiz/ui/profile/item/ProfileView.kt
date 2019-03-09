package com.example.historyquiz.ui.profile.item

import com.arellomobile.mvp.MvpView
import com.example.historyquiz.model.wiki_api.query.Page
import com.example.historyquiz.ui.base.interfaces.BasicFunctional

interface ProfileView: BasicFunctional {

    fun handleError(throwable: Throwable)

}