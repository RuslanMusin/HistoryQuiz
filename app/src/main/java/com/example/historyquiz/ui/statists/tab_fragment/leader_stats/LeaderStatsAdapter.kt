package com.example.historyquiz.ui.statists.tab_fragment.leader_stats

import android.view.ViewGroup
import com.example.historyquiz.model.epoch.LeaderStat
import com.example.historyquiz.ui.base.BaseAdapter

class LeaderStatsAdapter(items: MutableList<LeaderStat>) : BaseAdapter<LeaderStat, LeaderStatHolder>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderStatHolder {
        return LeaderStatHolder.create(parent)
    }

    override fun onBindViewHolder(holder: LeaderStatHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = getItem(position)
        holder.bind(item)
    }
}