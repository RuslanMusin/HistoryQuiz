package com.example.historyquiz.ui.statists.tab_fragment.leader_stats

import com.example.historyquiz.model.epoch.LeaderStat
import com.example.historyquiz.ui.base.interfaces.BaseRecyclerView
import com.example.historyquiz.ui.base.interfaces.ReloadableView

interface LeaderStatsView: BaseRecyclerView<LeaderStat>, ReloadableView {

    fun showStats(themes: List<LeaderStat>)
}