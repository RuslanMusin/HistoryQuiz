package com.example.historyquiz.ui.statists.tab_fragment.game_stats

import com.example.historyquiz.model.epoch.UserEpoch
import com.example.historyquiz.ui.base.interfaces.BaseRecyclerView
import com.example.historyquiz.ui.base.interfaces.ReloadableView

interface GameStatsView: BaseRecyclerView<UserEpoch>, ReloadableView {

    fun showStats(themes: List<UserEpoch>)
}