package com.example.historyquiz.ui.statists.tab_fragment.common_stats

import com.example.historyquiz.model.epoch.UserEpoch
import com.example.historyquiz.ui.base.interfaces.BaseRecyclerView

interface CommonStatsView: BaseRecyclerView<UserEpoch> {

    fun showStats(themes: List<UserEpoch>)
}