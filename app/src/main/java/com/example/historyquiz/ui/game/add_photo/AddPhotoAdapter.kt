package com.example.historyquiz.ui.game.add_photo

import android.view.ViewGroup
import com.example.historyquiz.model.db_dop_models.PhotoItem
import com.example.historyquiz.ui.base.BaseAdapter

class AddPhotoAdapter(items: MutableList<PhotoItem>) : BaseAdapter<PhotoItem, AddPhotoItemHolder>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddPhotoItemHolder {
        return AddPhotoItemHolder.create(parent.context)
    }

    override fun onBindViewHolder(holder: AddPhotoItemHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = getItem(position)
        holder.bind(item)
    }
}