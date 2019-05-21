package com.example.historyquiz.ui.cards.card_list

import com.example.historyquiz.model.card.AbstractCard
import com.example.historyquiz.ui.base.interfaces.BaseRecyclerView
import com.example.historyquiz.ui.base.interfaces.BasicFunctional

interface CardListView: BasicFunctional, BaseRecyclerView<AbstractCard> {

    fun showCards(list: List<AbstractCard>)
}