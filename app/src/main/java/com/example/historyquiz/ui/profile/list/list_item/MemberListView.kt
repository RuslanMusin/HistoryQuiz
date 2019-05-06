package com.example.historyquiz.ui.profile.list.list_item

import com.example.historyquiz.model.user.User
import com.example.historyquiz.ui.base.interfaces.BaseRecyclerView
import com.example.historyquiz.ui.base.interfaces.ReloadableView
import com.example.historyquiz.ui.base.interfaces.SearchListener

interface MemberListView: BaseRecyclerView<User>, ReloadableView, SearchListener {

    fun showItems(tests: List<User>)
}