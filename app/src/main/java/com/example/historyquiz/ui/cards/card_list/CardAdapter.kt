package com.example.historyquiz.ui.cards.card_list

import android.view.ViewGroup
import com.example.historyquiz.model.card.AbstractCard
import com.example.historyquiz.ui.base.BaseAdapter

class CardAdapter(items: MutableList<AbstractCard>) : BaseAdapter<AbstractCard, CardViewHolder>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        return CardViewHolder.create(parent.context)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = getItem(position)
        holder.bind(item)
    }
}