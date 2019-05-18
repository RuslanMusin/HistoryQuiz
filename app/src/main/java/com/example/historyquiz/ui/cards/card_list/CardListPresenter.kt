package com.example.historyquiz.ui.cards.card_list

import com.arellomobile.mvp.InjectViewState
import com.example.historyquiz.repository.card.AbstractCardRepository
import com.example.historyquiz.ui.base.BasePresenter
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import javax.inject.Inject

@InjectViewState
class CardListPresenter @Inject constructor() : BasePresenter<CardListView>() {

    @Inject
    lateinit var abstractCardRepository: AbstractCardRepository

    fun loadUserCards(userId: String) {
        val dis = abstractCardRepository
            .findMyAbstractCards(userId)
            .doOnSubscribe(Consumer<Disposable> { viewState.showLoading() })
            .doAfterTerminate(Action { viewState.hideLoading() })
            .subscribe({ viewState.showCards(it) }, { viewState.handleError(it) })

        compositeDisposable.add(dis)
    }

    fun loadCardsByQuery(query: String, userId: String) {
        val dis = abstractCardRepository
            .findMyAbstractCardsByQuery(query, userId)
            .doOnSubscribe(Consumer<Disposable> { viewState.showListLoading() })
            .doAfterTerminate(Action { viewState.hideListLoading() })
            .subscribe({ viewState.changeDataSet(it) }, { viewState.handleError(it) })

        compositeDisposable.add(dis)
    }
}
