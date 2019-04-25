package com.example.historyquiz.ui.epoch

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.historyquiz.repository.epoch.EpochRepository
import com.example.historyquiz.ui.base.BasePresenter
import com.example.historyquiz.ui.game.game_list.GameListView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import javax.inject.Inject

@InjectViewState
class EpochListPresenter @Inject constructor() : BasePresenter<EpochListView>() {

    @Inject
    lateinit var epochRepository: EpochRepository

    fun loadEpoches() {

        val disposable =  epochRepository
            .findEpoches()
            .doOnSubscribe(Consumer<Disposable> { viewState.showLoading() })
            .doAfterTerminate(Action { viewState.hideLoading() })
            .subscribe({ viewState.changeDataSet(it) }, { viewState.handleError(it) })
        compositeDisposable.add(disposable)
    }
}