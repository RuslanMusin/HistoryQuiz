package com.example.historyquiz.ui.tests.test_list

import android.annotation.SuppressLint
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.historyquiz.repository.test.TestRepository
import com.example.historyquiz.ui.base.App
import com.example.historyquiz.ui.base.BasePresenter
import com.example.historyquiz.ui.profile.item.ProfileView
import com.example.historyquiz.utils.AppHelper
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import javax.inject.Inject

@InjectViewState
class TestListPresenter: BasePresenter<TestListView>() {

    @Inject
    lateinit var testRepository: TestRepository

    init {
        App.sAppComponent.inject(this)
    }

    fun loadOfficialTestsByQUery(query: String) {
        AppHelper.currentUser?.id?.let {
            val disposable = testRepository
                .findOfficialTestsByQuery(query, it)
                .doOnSubscribe(Consumer<Disposable> { viewState.showLoading(it) })
                .doAfterTerminate(Action { viewState.hideLoading() })
                .subscribe({ viewState.changeDataSet(it) }, { viewState.handleError(it) })
            compositeDisposable.add(disposable)
        }
    }

    fun loadOfficialTests() {
        AppHelper.currentUser?.id?.let {
            testRepository
                .findOfficialTests(it)
                .doOnSubscribe({ viewState.showLoading(it) })
                .doAfterTerminate({ viewState.hideLoading() })
                .doAfterTerminate({ viewState.setNotLoading() })
                .subscribe({ viewState.changeDataSet(it) }, { viewState.handleError(it) })
        }
    }
}
