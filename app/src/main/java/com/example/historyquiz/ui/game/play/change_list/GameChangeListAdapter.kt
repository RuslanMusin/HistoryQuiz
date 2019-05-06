package com.example.historyquiz.ui.game.play.change_list

import android.util.Log
import android.view.ViewGroup
import com.example.historyquiz.model.card.Card
import com.example.historyquiz.ui.base.BaseAdapter
import com.example.historyquiz.utils.Const.TAG_LOG
import com.example.historyquiz.utils.getRandom

class GameChangeListAdapter
    (items: MutableList<Card>, val allCards: MutableList<Card>, val size: Int, val onStop: () -> Unit)
    : BaseAdapter<Card, GameChangeListHolder>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameChangeListHolder {
//        return TestItemHolder.create(parent.context)
        return GameChangeListHolder.create(parent)
    }

    override fun onBindViewHolder(holder: GameChangeListHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = getItem(position)
        holder.itemView.setOnLongClickListener{t ->
            Log.d(TAG_LOG,"onLongClicked")
            allCards.getRandom()?.let {
                allCards.remove(it)
                items.toMutableList().let { cards ->
                    cards.removeAt(position)
                    cards.add(position, it)
                    changeDataSet(cards)
                }
                if(size - allCards.size >= 2 || allCards.size == 0) {
                    onStop()
                }
            }

            true
        }
        holder.bind(item)
    }

}