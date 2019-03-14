package com.example.historyquiz.ui.tests.test_list

import com.arellomobile.mvp.InjectViewState
import com.example.historyquiz.ui.base.App
import com.example.historyquiz.ui.base.BasePresenter
import com.example.historyquiz.ui.profile.item.ProfileView

@InjectViewState
class TestListPresenter: BasePresenter<TestListView>() {

    init {
        App.sAppComponent.inject(this)
    }

}
