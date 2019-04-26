package com.example.historyquiz.ui.statists.tab_fragment.common_stats

import android.view.ViewGroup
import com.example.historyquiz.model.epoch.UserEpoch
import com.example.historyquiz.ui.base.BaseAdapter

class CommonStatsAdapter(items: MutableList<UserEpoch>) : BaseAdapter<UserEpoch, CommonStatHolder>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonStatHolder {
        return CommonStatHolder.create(parent)
    }

    override fun onBindViewHolder(holder: CommonStatHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = getItem(position)
        holder.bind(item)
    }
}