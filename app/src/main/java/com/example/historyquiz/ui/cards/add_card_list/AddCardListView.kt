package com.example.historyquiz.ui.cards.add_card_list

import com.arellomobile.mvp.MvpView
import com.example.historyquiz.model.wiki_api.opensearch.Item
import com.example.historyquiz.ui.base.interfaces.BaseRecyclerView
import com.example.historyquiz.ui.base.interfaces.BasicFunctional
import io.reactivex.disposables.Disposable

interface AddCardListView : BasicFunctional, BaseRecyclerView<Item> {
    fun setOpenSearchList(list: List<Item>)
}

