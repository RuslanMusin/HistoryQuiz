package com.example.historyquiz.ui.statists.tab_fragment.common_stats

import com.example.historyquiz.model.epoch.UserEpoch
import com.example.historyquiz.ui.base.interfaces.BaseRecyclerView
import com.example.historyquiz.ui.base.interfaces.ReloadableView

interface CommonStatsView: BaseRecyclerView<UserEpoch>, ReloadableView {

    fun showStats(themes: List<UserEpoch>)
}