package com.summer.itis.curatorapp.ui.base.base_first

import io.reactivex.disposables.Disposable

interface BaseRecyclerView<Entity> : BaseAdapter.OnItemClickListener<Entity> {

    fun handleError(throwable: Throwable)

    fun setNotLoading()

    fun showLoading(disposable: Disposable)

    fun hideLoading()

    fun loadNextElements(i: Int)

    fun changeDataSet(tests: List<Entity>)
}
