package com.example.historyquiz.ui.statists.tab_fragment.common_stats

import com.arellomobile.mvp.InjectViewState
import com.example.historyquiz.model.epoch.UserEpoch
import com.example.historyquiz.repository.epoch.UserEpochRepository
import com.example.historyquiz.ui.base.BasePresenter
import com.example.historyquiz.utils.AppHelper
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import javax.inject.Inject

@InjectViewState
class CommonStatsPresenter @Inject constructor() : BasePresenter<CommonStatsView>() {

    @Inject
    lateinit var userEpochRepository: UserEpochRepository

    fun loadStats() {
        val disposable =  userEpochRepository
            .findUserEpoches(AppHelper.currentUser.id)
            .map { epoches -> epoches.sortedWith(compareByDescending(UserEpoch::keSub)) }
            .doOnSubscribe(Consumer<Disposable> { viewState.showListLoading() })
            .doAfterTerminate(Action { viewState.hideListLoading() })
            .subscribe({ viewState.changeDataSet(it) }, { viewState.handleError(it) })
        compositeDisposable.add(disposable)
    }
}