package com.example.historyquiz.ui.profile.list.list_item

import com.example.historyquiz.model.user.User
import com.example.historyquiz.ui.base.interfaces.BaseRecyclerView
import com.example.historyquiz.ui.base.interfaces.ReloadableView

interface MemberListView: BaseRecyclerView<User>, ReloadableView {

    fun showItems(tests: List<User>)
}