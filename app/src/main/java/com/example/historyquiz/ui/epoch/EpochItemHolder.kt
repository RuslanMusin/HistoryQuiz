package com.example.historyquiz.ui.epoch

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.historyquiz.R
import com.example.historyquiz.model.epoch.Epoch
import kotlinx.android.synthetic.main.item_epoch.view.*

class EpochItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(item: Epoch) {
        itemView.tv_name.text = item.name
    }


    companion object {

        fun create(parent: ViewGroup): EpochItemHolder {
            val view =  LayoutInflater.from(parent.context).inflate(R.layout.item_epoch, parent, false);
            val holder = EpochItemHolder(view)
            return holder
        }
    }
}