package com.example.historyquiz.ui.tests.test_list

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.historyquiz.repository.test.TestRepository
import com.example.historyquiz.ui.base.BasePresenter
import com.example.historyquiz.utils.AppHelper
import com.example.historyquiz.utils.Const.TAG_LOG
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import javax.inject.Inject

@InjectViewState
class TestListPresenter @Inject constructor() : BasePresenter<TestListView>() {

    @Inject
    lateinit var testRepository: TestRepository

    fun loadOfficialTestsByQUery(userId: String, query: String, type: String) {
        AppHelper.currentUser?.id?.let {
            val disposable = testRepository
                .findTestsByQuery(userId, query, type)
                .doOnSubscribe(Consumer<Disposable> { viewState.showListLoading() })
                .doAfterTerminate(Action { viewState.hideListLoading() })
                .subscribe({ viewState.changeDataSet(it) }, { viewState.handleError(it) })
            compositeDisposable.add(disposable)
        }
    }

    fun loadOfficialTests(userId: String, type: String) {
        Log.d(TAG_LOG, "load tests")
        AppHelper.currentUser?.id?.let {
            testRepository
                .findTests(userId, type)
                .doOnSubscribe({ viewState.showListLoading() })
                .doAfterTerminate({ viewState.hideListLoading() })
                .doAfterTerminate({ viewState.setNotLoading() })
                .subscribe({ viewState.changeDataSet(it) }, { viewState.handleError(it) })
        }
    }
}
