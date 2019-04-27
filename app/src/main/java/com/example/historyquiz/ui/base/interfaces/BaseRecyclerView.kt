package com.example.historyquiz.ui.base.interfaces

import com.example.historyquiz.ui.base.BaseAdapter

interface BaseRecyclerView<Entity> : BasicFunctional, BaseAdapter.OnItemClickListener<Entity> {

    fun handleError(throwable: Throwable)

    fun setNotLoading()

    fun showListLoading()

    fun hideListLoading()

    fun loadNextElements(i: Int)

    fun changeDataSet(tests: List<Entity>)
}