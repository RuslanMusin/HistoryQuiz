package com.example.historyquiz.ui.tests.test_item.finish

import com.arellomobile.mvp.InjectViewState
import com.example.historyquiz.model.test.Test
import com.example.historyquiz.repository.card.CardRepository
import com.example.historyquiz.repository.test.TestRepository
import com.example.historyquiz.repository.test.TestRepositoryImpl
import com.example.historyquiz.ui.base.App
import com.example.historyquiz.ui.base.BasePresenter
import com.example.historyquiz.utils.AppHelper
import javax.inject.Inject

@InjectViewState
class FinishPresenter: BasePresenter<FinishView>() {

    @Inject
    lateinit var testRepositoryImpl: TestRepository

    @Inject
    lateinit var cardRepository: CardRepository

    init {
        App.sAppComponent.inject(this)
    }

    fun finishTest(test: Test) {
        AppHelper.currentUser?.let {user ->
            val absId = test.card?.cardId
            absId?.let {
                cardRepository.findMyAbstractCardStates(absId, user.id).subscribe { cards ->
                    testRepositoryImpl.finishTest(test, user, cards).subscribe() }
                }

            }
    }

}