package com.example.historyquiz.ui.profile.list.list_item

import android.view.ViewGroup
import com.example.historyquiz.model.user.User
import com.example.historyquiz.ui.base.BaseAdapter

class MemberAdapter(items: MutableList<User>) : BaseAdapter<User, MemberItemHolder>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberItemHolder {
        return MemberItemHolder.create(parent.context)
    }

    override fun onBindViewHolder(holder: MemberItemHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = getItem(position)
        holder.bind(item)
    }
}