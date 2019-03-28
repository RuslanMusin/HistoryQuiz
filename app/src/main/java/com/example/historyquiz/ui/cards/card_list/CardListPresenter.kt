package com.example.historyquiz.ui.cards.card_list

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.historyquiz.repository.card.AbstractCardRepository
import com.example.historyquiz.repository.card.CardRepository
import com.example.historyquiz.repository.test.TestRepository
import com.example.historyquiz.ui.base.App
import com.example.historyquiz.ui.base.BasePresenter
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import javax.inject.Inject

@InjectViewState
class CardListPresenter : BasePresenter<CardListView>() {

    @Inject
    lateinit var abstractCardRepository: AbstractCardRepository

    init {
        App.sAppComponent.inject(this)
    }

    fun loadUserCards(userId: String) {
        val dis = abstractCardRepository
            .findMyAbstractCards(userId)
            .doOnSubscribe(Consumer<Disposable> { viewState.showLoading() })
            .doAfterTerminate(Action { viewState.hideLoading() })
            .subscribe({ viewState.changeDataSet(it) }, { viewState.handleError(it) })

        compositeDisposable.add(dis)
    }

    fun loadCardsByQuery(userId: String, query: String) {
        val dis = abstractCardRepository
            .findMyAbstractCardsByQuery(query, userId)
            .doOnSubscribe(Consumer<Disposable> { viewState.showLoading() })
            .doAfterTerminate(Action { viewState.hideLoading() })
            .subscribe({ viewState.changeDataSet(it) }, { viewState.handleError(it) })

        compositeDisposable.add(dis)
    }
}
