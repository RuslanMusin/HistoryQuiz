package com.example.historyquiz.ui.tests.add_test

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.example.historyquiz.model.test.Test

class AddTestViewModel : ViewModel() {
    val test = MutableLiveData<Test>()
    val number = MutableLiveData<Int>()

    fun selectTest(item: Test) {
        test.value = item
    }

    fun selectNumber(student: Int) {
        number.value = student
    }
}
