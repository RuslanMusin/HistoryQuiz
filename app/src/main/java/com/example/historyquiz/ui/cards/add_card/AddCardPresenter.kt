package com.example.historyquiz.ui.cards.add_card

import android.annotation.SuppressLint
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.historyquiz.repository.api.WikiApiRepository
import com.example.historyquiz.ui.base.BasePresenter
import com.example.historyquiz.utils.Const.TAG_LOG
import javax.inject.Inject

@InjectViewState
class AddCardPresenter @Inject constructor() : BasePresenter<AddCardView>() {

    @Inject
    lateinit var wikiApiRepository: WikiApiRepository

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        Log.d(TAG_LOG, "attach presenter")
    }

    @SuppressLint("CheckResult")
    fun query(query: String) {
        wikiApiRepository!!
                .query(query)
                .subscribe({ viewState.setQueryResults(it) }, { viewState.handleError(it) })

    }
}
