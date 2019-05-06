package com.example.historyquiz.ui.epoch

import android.view.ViewGroup
import com.example.historyquiz.model.epoch.Epoch
import com.example.historyquiz.ui.base.BaseAdapter

class EpochAdapter(items: MutableList<Epoch>) : BaseAdapter<Epoch, EpochItemHolder>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpochItemHolder {
        return EpochItemHolder.create(parent)
    }

    override fun onBindViewHolder(holder: EpochItemHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = getItem(position)
        holder.bind(item)
    }
}