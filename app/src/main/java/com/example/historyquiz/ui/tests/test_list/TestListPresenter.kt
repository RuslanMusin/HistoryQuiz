package com.example.historyquiz.ui.tests.test_list

import com.arellomobile.mvp.InjectViewState
import com.example.historyquiz.repository.test.TestRepository
import com.example.historyquiz.repository.test.TestRepositoryImpl
import com.example.historyquiz.ui.base.App
import com.example.historyquiz.ui.base.BasePresenter
import com.example.historyquiz.utils.AppHelper
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import javax.inject.Inject

@InjectViewState
class TestListPresenter: BasePresenter<TestListView>() {

    @Inject
    lateinit var testRepositoryImpl: TestRepository

    init {
        App.sAppComponent.inject(this)
    }

    fun loadOfficialTestsByQUery(query: String) {
        AppHelper.currentUser?.id?.let {
            val disposable = testRepositoryImpl
                .findOfficialTestsByQuery(query, it)
                .doOnSubscribe(Consumer<Disposable> { viewState.showLoading(it) })
                .doAfterTerminate(Action { viewState.hideLoading() })
                .subscribe({ viewState.changeDataSet(it) }, { viewState.handleError(it) })
            compositeDisposable.add(disposable)
        }
    }

    fun loadOfficialTests() {
        AppHelper.currentUser?.id?.let {
            testRepositoryImpl
                .findOfficialTests(it)
                .doOnSubscribe({ viewState.showLoading(it) })
                .doAfterTerminate({ viewState.hideLoading() })
                .doAfterTerminate({ viewState.setNotLoading() })
                .subscribe({ viewState.changeDataSet(it) }, { viewState.handleError(it) })
        }
    }
}
