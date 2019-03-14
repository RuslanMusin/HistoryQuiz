package com.example.historyquiz.ui.base.interfaces

import com.example.historyquiz.ui.base.BaseAdapter
import io.reactivex.disposables.Disposable

interface BaseRecyclerView<Entity> : BaseAdapter.OnItemClickListener<Entity> {

    fun handleError(throwable: Throwable)

    fun setNotLoading()

    fun showLoading(disposable: Disposable)

    fun hideLoading()

    fun loadNextElements(i: Int)

    fun changeDataSet(tests: List<Entity>)
}
