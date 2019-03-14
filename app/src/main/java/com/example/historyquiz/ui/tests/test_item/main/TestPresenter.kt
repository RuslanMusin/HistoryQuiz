package com.example.historyquiz.ui.tests.test_item

import com.arellomobile.mvp.InjectViewState
import com.example.historyquiz.ui.base.App
import com.example.historyquiz.ui.base.BasePresenter
import com.example.historyquiz.ui.tests.test_list.TestListView

@InjectViewState
class TestPresenter: BasePresenter<TestView>() {

    init {
        App.sAppComponent.inject(this)
    }

}