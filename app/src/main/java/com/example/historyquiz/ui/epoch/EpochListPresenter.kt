package com.example.historyquiz.ui.epoch

import com.arellomobile.mvp.InjectViewState
import com.example.historyquiz.repository.epoch.EpochRepository
import com.example.historyquiz.ui.base.BasePresenter
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import javax.inject.Inject

@InjectViewState
class EpochListPresenter @Inject constructor() : BasePresenter<EpochListView>() {

    @Inject
    lateinit var epochRepository: EpochRepository

    fun loadEpoches(hasDefault: Boolean) {

        val disposable =  epochRepository
            .findEpoches(hasDefault)
            .doOnSubscribe(Consumer<Disposable> { viewState.showLoading() })
            .doAfterTerminate(Action { viewState.hideLoading() })
            .subscribe({ viewState.changeDataSet(it) }, { viewState.handleError(it) })
        compositeDisposable.add(disposable)
    }
}