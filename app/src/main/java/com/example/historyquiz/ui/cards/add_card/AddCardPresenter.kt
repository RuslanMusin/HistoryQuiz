package com.example.historyquiz.ui.cards.add_card

import android.annotation.SuppressLint
import android.util.Log

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.historyquiz.repository.api.WikiApiRepository
import com.example.historyquiz.ui.base.App
import com.example.historyquiz.utils.Const.TAG_LOG
import io.reactivex.functions.Consumer
import javax.inject.Inject

@InjectViewState
class AddCardPresenter : MvpPresenter<AddCardView>() {

    @Inject
    lateinit var wikiApiRepository: WikiApiRepository

    init {
        App.sAppComponent.inject(this)
    }

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
