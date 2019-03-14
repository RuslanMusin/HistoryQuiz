package com.example.historyquiz.ui.tests.add_test.main

import com.arellomobile.mvp.InjectViewState
import com.example.historyquiz.ui.base.App
import com.example.historyquiz.ui.base.BasePresenter
import com.example.historyquiz.ui.tests.test_list.TestListView

@InjectViewState
class AddMainTestPresenter: BasePresenter<AddMainTestView>() {

    init {
        App.sAppComponent.inject(this)
    }

}