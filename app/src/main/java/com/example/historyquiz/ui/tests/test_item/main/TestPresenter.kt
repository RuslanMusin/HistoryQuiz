package com.example.historyquiz.ui.tests.test_item.main

import com.arellomobile.mvp.InjectViewState
import com.example.historyquiz.model.test.Test
import com.example.historyquiz.repository.card.CardRepository
import com.example.historyquiz.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class TestPresenter @Inject constructor() : BasePresenter<TestView>() {

    @Inject
    lateinit var cardRepository: CardRepository

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