package com.example.historyquiz.ui.cards.add_card_list

import android.annotation.SuppressLint
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.historyquiz.repository.api.WikiApiRepository
import com.example.historyquiz.ui.base.BasePresenter
import com.example.historyquiz.utils.Const.TAG_LOG
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import javax.inject.Inject

@InjectViewState
class AddCardListPresenter @Inject constructor() : BasePresenter<AddCardListView>() {

    @Inject
    lateinit var wikiApiRepository: WikiApiRepository

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        Log.d(TAG_LOG, "attach presenter")
    }

    @SuppressLint("CheckResult")
    fun opensearch(opensearch: String) {
        Log.d(TAG_LOG,"pres opensearch")
        wikiApiRepository
                .opensearch(opensearch)
                .doOnSubscribe(Consumer<Disposable> { viewState.showListLoading() })
                .doAfterTerminate(Action { viewState.hideListLoading() })
                .subscribe({ viewState.setOpenSearchList(it) }, { viewState.handleError(it) })
    }
}
