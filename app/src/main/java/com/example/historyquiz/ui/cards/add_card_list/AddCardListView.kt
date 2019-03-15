package com.example.historyquiz.ui.cards.add_card_list

import com.arellomobile.mvp.MvpView
import com.example.historyquiz.model.wiki_api.opensearch.Item
import com.example.historyquiz.ui.base.interfaces.BasicFunctional
import io.reactivex.disposables.Disposable

interface AddCardListView : BasicFunctional {
    fun setOpenSearchList(list: List<Item>)

    fun handleError(throwable: Throwable)

    fun showLoading(disposable: Disposable)

    fun hideLoading()
}

