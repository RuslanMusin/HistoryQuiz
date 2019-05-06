package com.example.historyquiz.ui.cards.card_list

import android.content.Context
import android.support.annotation.NonNull
import android.support.v7.widget.RecyclerView
import android.view.View
import com.bumptech.glide.Glide
import com.example.historyquiz.R
import com.example.historyquiz.model.card.AbstractCard
import com.example.historyquiz.utils.AppHelper.Companion.cutLongDescription
import kotlinx.android.synthetic.main.item_member.view.*

class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    companion object {

        const val MAX_LENGTH = 80
        fun create(context: Context): CardViewHolder {
            val view = View.inflate(context, R.layout.item_member, null)
            val holder = CardViewHolder(view)
            return holder
        }
    }


    fun bind(@NonNull item: AbstractCard) {
        itemView.tv_name.text = item.name
        itemView.tv_description.text = item.description?.let { cutLongDescription(it, MAX_LENGTH) }
        if(item.photoUrl != null) {
            Glide.with(itemView.context)
                .load(item.photoUrl)
                .into(itemView.iv_cover)
        }
    }
}