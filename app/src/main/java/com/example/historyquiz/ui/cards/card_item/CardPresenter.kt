package com.example.historyquiz.ui.cards.card_item

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.historyquiz.repository.card.CardRepository
import com.example.historyquiz.ui.base.App
import com.example.historyquiz.ui.base.BasePresenter
import com.example.historyquiz.ui.cards.card_list.CardListView
import io.reactivex.disposables.Disposable
import javax.inject.Inject

@InjectViewState
class CardPresenter : BasePresenter<CardView>() {

    init {
        App.sAppComponent.inject(this)
    }
}
