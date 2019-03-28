package com.example.historyquiz.ui.cards.wiki_page

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.historyquiz.repository.card.CardRepository
import com.example.historyquiz.ui.base.App
import com.example.historyquiz.ui.cards.card_list.CardListView
import javax.inject.Inject

@InjectViewState
class WikiPagePresenter : MvpPresenter<WikiPageView>() {

    init {
        App.sAppComponent.inject(this)
    }

}