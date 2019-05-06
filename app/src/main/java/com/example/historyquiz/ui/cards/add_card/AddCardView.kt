package com.example.historyquiz.ui.cards.add_card

import com.example.historyquiz.model.wiki_api.query.Page
import com.example.historyquiz.ui.base.interfaces.BasicFunctional

interface AddCardView : BasicFunctional {

    fun setQueryResults(list: List<Page>)

    fun handleError(throwable: Throwable)
}

