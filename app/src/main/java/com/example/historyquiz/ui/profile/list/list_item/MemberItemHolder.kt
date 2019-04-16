package com.example.historyquiz.ui.profile.list.list_item

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import com.bumptech.glide.Glide
import com.example.historyquiz.R
import com.example.historyquiz.model.user.User
import com.example.historyquiz.utils.AppHelper
import com.example.historyquiz.utils.Const.STUB_PATH
import kotlinx.android.synthetic.main.item_member.view.*

class MemberItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(item: User) {
        itemView.tv_name.text = item.username

        if (item.photoUrl != null) {
            if (item.photoUrl.equals(STUB_PATH)) {
//                ImageLoadHelper.loadPictureByDrawableDefault(imageView, R.drawable.ic_person_black_24dp)
                Glide.with(itemView.iv_cover.context)
                    .load(R.drawable.knight)
                    .into(itemView.iv_cover)
            } else {
                //                ImageLoadHelper.loadPicture(imageView, items.getPhotoUrl());
                val imageReference = AppHelper.storageReference.child(item.photoUrl!!)

                Glide.with(itemView.iv_cover.context)
                    .load(imageReference)
                    .into(itemView.iv_cover)
            }

        }
    }

    private fun cutLongDescription(description: String): String {
        return if (description.length < MAX_LENGTH) {
            description
        } else {
            description.substring(0, MAX_LENGTH - MORE_TEXT.length) + MORE_TEXT
        }
    }

    companion object {

        private val MAX_LENGTH = 80
        private val MORE_TEXT = "..."

        fun create(context: Context): MemberItemHolder {
            val view = View.inflate(context, R.layout.item_member, null)
            val holder = MemberItemHolder(view)
            return holder
        }
    }
}
