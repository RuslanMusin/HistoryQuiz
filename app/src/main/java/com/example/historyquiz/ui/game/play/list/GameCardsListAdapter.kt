package com.example.historyquiz.ui.game.play.list

import android.content.Context
import android.support.constraint.Constraints
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.Glide
import com.example.historyquiz.R
import com.example.historyquiz.model.card.Card
import com.ms.square.android.expandabletextview.ExpandableTextView

class GameCardsListAdapter(
    val items: MutableList<Card>,
    val context: Context,
    val onClick: (card: Card) -> Unit) :
    RecyclerView.Adapter<GameCardsListHolder>() {

    var isClickable: Boolean = true

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameCardsListHolder {
        return GameCardsListHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.item_game_card_small, parent, false))
    }


    override fun onBindViewHolder(holder: GameCardsListHolder, position: Int) {
//        holder.nameView.text = items[position].abstractCard!!.name
        Glide.with(context)
            .load(items[position].abstractCard.photoUrl)
            .into(holder.image)

        holder.itemView.setOnClickListener {
            if(isClickable) {
                isClickable = false
                val pos = holder.adapterPosition

                removeElement(items[pos])
            }
        }

        holder.itemView.setOnLongClickListener{

            val dialog: MaterialDialog = MaterialDialog.Builder(it.context)
                .customView(R.layout.fragment_test_card, false)
                .build()

            val view: View? = dialog.customView
            view?.findViewById<ExpandableTextView>(R.id.expand_text_view)?.text = items[position].abstractCard.description
            view?.findViewById<TextView>(R.id.tv_name)?.text = items[position].abstractCard.name
            view?.findViewById<TextView>(R.id.tv_test_name)?.text = items[position].test.title

            view?.findViewById<ImageView>(R.id.iv_portrait)?.let { it1 ->
                Glide.with(it1.context)
                    .load(items[position].abstractCard.photoUrl)
                    .into(it1)
            }

            dialog.show()

            true
        }
    }

    fun changeDataSet(values: List<Card>) {
        items.clear()
        Log.d(Constraints.TAG, "values size = " + values.size)
        items.addAll(values)
        notifyDataSetChanged()
    }

    fun removeElement(card: Card) {
        val pos = items.indexOf(card)

        onClick(items[pos])
        notifyItemRemoved(pos)
        items.removeAt(pos)
    }
}
