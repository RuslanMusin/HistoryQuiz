package com.example.historyquiz.ui.statists.tab_fragment.game_stats

import android.view.ViewGroup
import com.example.historyquiz.model.epoch.UserEpoch
import com.example.historyquiz.ui.base.BaseAdapter

class GameStatsAdapter(items: MutableList<UserEpoch>) : BaseAdapter<UserEpoch, GameStatHolder>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameStatHolder {
        return GameStatHolder.create(parent)
    }

    override fun onBindViewHolder(holder: GameStatHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = getItem(position)
        holder.bind(item)
    }
}