package com.example.historyquiz.ui.tests.test_item.finish

import com.example.historyquiz.model.test.Test
import com.example.historyquiz.ui.base.interfaces.BasicFunctional

interface FinishView: BasicFunctional {

    fun checkAnswers(test: Test)

    fun setCardData(hasCard: Boolean)
}