package com.example.historyquiz.ui.tests.test_item.question

import com.arellomobile.mvp.InjectViewState
import com.example.historyquiz.ui.base.App
import com.example.historyquiz.ui.base.BasePresenter
import com.example.historyquiz.ui.tests.test_item.main.TestView

@InjectViewState
class QuestionPresenter: BasePresenter<QuestionView>() {

    init {
        App.sAppComponent.inject(this)
    }

}