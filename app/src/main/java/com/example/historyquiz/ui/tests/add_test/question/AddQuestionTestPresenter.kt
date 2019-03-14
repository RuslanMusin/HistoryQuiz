package com.example.historyquiz.ui.tests.add_test.question

import com.arellomobile.mvp.InjectViewState
import com.example.historyquiz.ui.base.App
import com.example.historyquiz.ui.base.BasePresenter
import com.example.historyquiz.ui.tests.test_list.TestListView

@InjectViewState
class AddQuestionTestPresenter: BasePresenter<AddQuestionTestView>() {

    init {
        App.sAppComponent.inject(this)
    }

}