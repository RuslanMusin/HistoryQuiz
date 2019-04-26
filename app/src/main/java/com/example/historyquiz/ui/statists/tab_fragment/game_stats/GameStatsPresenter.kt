package com.example.historyquiz.ui.statists.tab_fragment.game_stats

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.historyquiz.repository.epoch.LeaderStatRepository
import com.example.historyquiz.repository.epoch.UserEpochRepository
import com.example.historyquiz.ui.base.BasePresenter
import com.example.historyquiz.utils.AppHelper
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import javax.inject.Inject


@InjectViewState
class GameStatsPresenter @Inject constructor() : BasePresenter<GameStatsView>() {

    @Inject
    lateinit var userEpochRepository: UserEpochRepository

    fun loadStats() {

        val disposable =  userEpochRepository
            .findUserEpoches(AppHelper.currentUser.id)
            .map { epoches -> epoches.sortedWith(compareByDescending { i -> Math.abs(i.geSub)} ) }
            .doOnSubscribe(Consumer<Disposable> { viewState.showLoading() })
            .doAfterTerminate(Action { viewState.hideLoading() })
            .subscribe({ viewState.changeDataSet(it) }, { viewState.handleError(it) })
        compositeDisposable.add(disposable)
    }
}