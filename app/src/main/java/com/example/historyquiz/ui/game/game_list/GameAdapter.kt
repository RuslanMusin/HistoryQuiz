package com.example.historyquiz.ui.game.game_list

import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.example.historyquiz.R
import com.example.historyquiz.model.game.Lobby
import com.example.historyquiz.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.item_game.view.*

class GameAdapter(items: MutableList<Lobby>) : BaseAdapter<Lobby, GameItemHolder>(items) {

    lateinit var listener: GameListView

    constructor(listener: GameListView, items: MutableList<Lobby>) : this(items) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameItemHolder {
        return GameItemHolder.create(parent)
    }

    override fun onBindViewHolder(holder: GameItemHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = getItem(position)
        holder.bind(item)

        if(item.isMyCreation) {
            holder.itemView.btn_delete.visibility = View.VISIBLE

            holder.itemView.btn_delete.setOnClickListener{
                MaterialDialog.Builder(holder.itemView.context)
                    .title(R.string.delete_game)
                    .content(R.string.game_will_be_deleted)
                    .positiveText("Удалить")
                    .onPositive(object : MaterialDialog.SingleButtonCallback {
                        override fun onClick(dialog: MaterialDialog, which: DialogAction) {
                            removeItem(item)
                        }

                    })
                    .negativeText("Отмена")
                    .onNegative{ dialog, action -> dialog.cancel()}
                    .show()
            }
        }

    }

    fun removeItemById(id: String?) {
        id?.let {
            for(item in items) {
                if(item.id.equals(it)) {
                    removeItem(item)
                }
            }
        }
    }

    private fun removeItem(item: Lobby) {
        val pos = items.indexOf(item)
        listener.removeLobby(item.id)
        items.removeAt(pos)
        notifyItemRemoved(pos)
    }
}