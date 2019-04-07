package com.example.historyquiz.ui.game.add_photo

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import com.bumptech.glide.Glide
import com.example.historyquiz.R
import com.example.historyquiz.model.db_dop_models.PhotoItem
import kotlinx.android.synthetic.main.item_member.view.*

class AddPhotoItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(item: PhotoItem) {
        Glide.with(itemView.iv_cover.context)
            .load(item.photoUrl)
            .into(itemView.iv_cover)
    }


    companion object {

        fun create(context: Context): AddPhotoItemHolder {
            val view = View.inflate(context, R.layout.item_photo, null)
            return AddPhotoItemHolder(view)
        }
    }
}
