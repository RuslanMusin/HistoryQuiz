package com.example.historyquiz.utils

import android.content.Context
import android.text.format.DateUtils
import com.example.historyquiz.R
import java.text.SimpleDateFormat
import java.util.*

object FormatterUtil {

    var firebaseDBDate = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
    var firebaseDBDay = "yyyy-MM-dd"
    val NOW_TIME_RANGE = DateUtils.MINUTE_IN_MILLIS * 5 // 5 minutes

    var dateTime = "yyyy-MM-dd HH:mm:ss"

    var usualDateTime = "dd.MM.yyyy"

    val firebaseDateFormat: SimpleDateFormat
        get() {
            val cbDateFormat = SimpleDateFormat(firebaseDBDate)
            cbDateFormat.timeZone = TimeZone.getTimeZone("UTC")
            return cbDateFormat
        }

    fun formatFirebaseDay(date: Date): String {
        val cbDateFormat = SimpleDateFormat(firebaseDBDay)
        cbDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return cbDateFormat.format(date)
    }

    fun formatDateTime(date: Date): String {
        val cbDateFormat = SimpleDateFormat(dateTime)
        cbDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return cbDateFormat.format(date)
    }

    fun getRelativeTimeSpanString(context: Context, time: Long): CharSequence {
        val now = System.currentTimeMillis()
        val range = Math.abs(now - time)

        return if (range < NOW_TIME_RANGE) {
            context.getString(R.string.now_time_range)
        } else DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS)

    }

    fun getRelativeTimeSpanStringShort(context: Context, time: Long): CharSequence {
        val now = System.currentTimeMillis()
        val range = Math.abs(now - time)
        return formatDuration(context, range, time)
    }

    private fun formatDuration(context: Context, range: Long, time: Long): CharSequence {
        val res = context.resources
        if (range >= DateUtils.WEEK_IN_MILLIS + DateUtils.DAY_IN_MILLIS) {
            return shortFormatEventDay(context, time)
        } else if (range >= DateUtils.WEEK_IN_MILLIS) {
            val days = ((range + DateUtils.WEEK_IN_MILLIS / 2) / DateUtils.WEEK_IN_MILLIS).toInt()
            return String.format(res.getString(R.string.duration_week_shortest), days)
        } else if (range >= DateUtils.DAY_IN_MILLIS) {
            val days = ((range + DateUtils.DAY_IN_MILLIS / 2) / DateUtils.DAY_IN_MILLIS).toInt()
            return String.format(res.getString(R.string.duration_days_shortest), days)
        } else if (range >= DateUtils.HOUR_IN_MILLIS) {
            val hours = ((range + DateUtils.HOUR_IN_MILLIS / 2) / DateUtils.HOUR_IN_MILLIS).toInt()
            return String.format(res.getString(R.string.duration_hours_shortest), hours)
        } else if (range >= NOW_TIME_RANGE) {
            val minutes = ((range + DateUtils.MINUTE_IN_MILLIS / 2) / DateUtils.MINUTE_IN_MILLIS).toInt()
            return String.format(res.getString(R.string.duration_minutes_shortest), minutes)
        } else {
            return res.getString(R.string.now_time_range)
        }
    }

    private fun shortFormatEventDay(context: Context, time: Long): String {
        val flags = DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_ABBREV_MONTH
        val f = Formatter(StringBuilder(50), Locale.getDefault())
        return DateUtils.formatDateRange(context, f, time, time, flags).toString()
    }

    fun getDateFromString(dateStr: String): Date {
        val cbDateFormat = SimpleDateFormat(usualDateTime)
        cbDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return cbDateFormat.parse(dateStr)
    }

    fun getStringFromDate(date: Date): String {
        val cbDateFormat = SimpleDateFormat(usualDateTime)
//        cbDateFormat.timeZone = TimeZone.get("UTC")
        return cbDateFormat.format(date)
    }
}
