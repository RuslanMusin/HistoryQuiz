package com.example.historyquiz.ui.base

import android.support.annotation.CallSuper
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.example.historyquiz.widget.EmptyStateRecyclerView

abstract class BaseAdapter<T, VH : RecyclerView.ViewHolder>(list: MutableList<T>) : RecyclerView.Adapter<VH>() {

    val items: MutableList<T> = ArrayList()

    private var onItemClickListener: OnItemClickListener<T>? = null

    companion object {

        const val TAG_BASE_ADAPTER = "TAG_BASE_ADAPTER"
    }

    private val listener = View.OnClickListener { view ->
        if (onItemClickListener != null) {
            val position = view.tag as Int
            val item = items[position]
            onItemClickListener?.onItemClick(item)
        }
    }

    private var recyclerView: EmptyStateRecyclerView? = null

    init {
        this.items.addAll(list)
    }

    fun attachToRecyclerView(recyclerView: EmptyStateRecyclerView) {
        this.recyclerView = recyclerView
        this.recyclerView?.adapter = this
        refreshRecycler()
    }

    fun add(value: T) {
        items.add(value)
        refreshRecycler()
    }

    fun changeDataSet(values: List<T>) {
        Log.d(TAG_BASE_ADAPTER, "values size = " + values.size)
        items.clear()
        items.addAll(values)
        refreshRecycler()
    }

    fun addAll(values: List<T>) {
        for (value in values) {
            items.add(value)
            notifyItemInserted(items.size - 1)
        }
    }

    fun clear() {
        items.clear()
        refreshRecycler()
    }

    protected fun refreshRecycler() {
        notifyDataSetChanged()
        recyclerView?.checkIfEmpty()
    }

    @CallSuper
    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.itemView.tag = position
        holder.itemView.setOnClickListener(listener)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener<T>?) {
        this.onItemClickListener = onItemClickListener
    }

    fun getItem(position: Int): T {
        return items[position]
    }

    override fun getItemCount(): Int {
        return items.size
    }

    interface OnItemClickListener<T> {
        fun onItemClick(item: T)
    }
}
