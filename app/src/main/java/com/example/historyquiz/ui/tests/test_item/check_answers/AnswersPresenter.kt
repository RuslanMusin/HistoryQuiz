package com.example.historyquiz.ui.tests.test_item.check_answers

import com.arellomobile.mvp.InjectViewState
import com.example.historyquiz.ui.base.App
import com.example.historyquiz.ui.base.BasePresenter
import com.example.historyquiz.ui.tests.test_item.main.TestView

@InjectViewState
class AnswersPresenter: BasePresenter<AnswersView>() {

    init {
        App.sAppComponent.inject(this)
    }

}