package com.example.historyquiz.ui.tests.add_test.question

import com.arellomobile.mvp.InjectViewState
import com.example.historyquiz.model.test.Test
import com.example.historyquiz.repository.test.TestRepository
import com.example.historyquiz.ui.base.BasePresenter
import com.example.historyquiz.utils.AppHelper
import javax.inject.Inject

@InjectViewState
class AddQuestionTestPresenter @Inject constructor() : BasePresenter<AddQuestionTestView>() {

    @Inject
    lateinit var testRepositoryImpl: TestRepository

    fun createTest(test: Test) {
        AppHelper.currentUser?.let {
            testRepositoryImpl
                .createTest(test, it)
                .subscribe { e -> viewState.navigateToTest() }
        }
    }

}