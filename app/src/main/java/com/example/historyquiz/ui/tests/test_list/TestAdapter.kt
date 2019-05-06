package com.example.historyquiz.ui.tests.test_list

import android.view.ViewGroup
import com.example.historyquiz.model.test.Test
import com.example.historyquiz.ui.base.BaseAdapter


class TestAdapter(items: MutableList<Test>) : BaseAdapter<Test, TestItemHolder>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestItemHolder {
        return TestItemHolder.create(parent)
    }

    override fun onBindViewHolder(holder: TestItemHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = getItem(position)
        holder.bind(item)
    }
}