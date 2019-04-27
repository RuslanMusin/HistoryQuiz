package com.example.historyquiz.ui.statists.tab_fragment.leader_stats

import com.arellomobile.mvp.InjectViewState
import com.example.historyquiz.model.epoch.LeaderStat
import com.example.historyquiz.repository.epoch.LeaderStatRepository
import com.example.historyquiz.ui.base.BasePresenter
import com.example.historyquiz.utils.AppHelper
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import javax.inject.Inject

@InjectViewState
class LeaderStatsPresenter @Inject constructor() : BasePresenter<LeaderStatsView>() {

    @Inject
    lateinit var leaderStatRepository: LeaderStatRepository

    fun loadStats() {
        val disposable =  leaderStatRepository
            .findStats(AppHelper.currentUser)
            .map { stats -> stats.sortedWith(compareByDescending(LeaderStat::kg)) }
            .doOnSubscribe(Consumer<Disposable> { viewState.showListLoading() })
            .doAfterTerminate(Action { viewState.hideListLoading() })
            .subscribe({ viewState.changeDataSet(it) }, { viewState.handleError(it) })
        compositeDisposable.add(disposable)
    }
}