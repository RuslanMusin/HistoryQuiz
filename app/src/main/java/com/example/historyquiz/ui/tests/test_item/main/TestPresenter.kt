package com.example.historyquiz.ui.tests.test_item.main

import com.arellomobile.mvp.InjectViewState
import com.example.historyquiz.model.test.Test
import com.example.historyquiz.repository.card.CardRepository
import com.example.historyquiz.repository.test.TestRepository
import com.example.historyquiz.ui.base.App
import com.example.historyquiz.ui.base.BasePresenter
import com.example.historyquiz.ui.tests.test_list.TestListView
import javax.inject.Inject

@InjectViewState
class TestPresenter: BasePresenter<TestView>() {

    @Inject
    lateinit var cardRepository: CardRepository

    init {
        App.sAppComponent.inject(this)
    }

    fun readCardForTest(test: Test) {
        test.cardId?.let {
            val dis = cardRepository.readCardForTest(it).subscribe{ card ->
                test.card = card
                viewState.setData()
            }
            compositeDisposable.add(dis)
        }
    }
}