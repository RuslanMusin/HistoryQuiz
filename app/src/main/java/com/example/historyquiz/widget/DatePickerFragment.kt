package com.summer.itis.curatorapp.widget

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.DatePicker
import com.summer.itis.curatorapp.ui.work.work_step.add_step.DateListener
import com.summer.itis.curatorapp.utils.FormatterUtil
import java.util.*

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    var year = 2018
    var month = 12
    var day = 30

    val calendar = Calendar.getInstance()

    lateinit var callback: DateListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return DatePickerDialog(activity, this, year, month, day)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        this.year = year
        this.month = month
        this.day = day
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        callback.setDate(FormatterUtil.getStringFromDate(getDate()))
    }

    fun getDate(): Date {
        return calendar.time
    }

    fun setDate(date: Date) {
        calendar.time = date
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH)
        day = calendar.get(Calendar.DAY_OF_MONTH)
    }
}