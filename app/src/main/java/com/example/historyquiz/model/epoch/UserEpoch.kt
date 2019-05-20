package com.example.historyquiz.model.epoch

import android.util.Log
import com.example.historyquiz.model.user.User
import com.example.historyquiz.utils.Const.TAG_LOG
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
class UserEpoch {

    lateinit var id: String
    lateinit var userId: String
    lateinit var epochId: String
    var win: Int = 0
    var lose: Int = 0
    var sum: Int = win + lose
        get() = win + lose
    @Exclude
    @Transient
    var epoch: Epoch? = null

    var right: Int = 0
    var wrong: Int = 0

    var ge: Double = 0.0
    var lastGe: Double = 0.0
    var geSub: Double = ge - lastGe
        get() = ge - lastGe
    var ke: Double = 0.0
    var lastKe: Double = 0.0
    var keSub: Double = ke - lastKe
        get() = ke - lastKe
    var updateDate: Long = 0

    constructor() {}

    constructor(epoch: Epoch, user: User) {
        this.id = epoch.id
        this.epoch = epoch
        this.userId = user.id
        this.epochId = epoch.id
        this.ge = 0.0
        this.updateDate = Date().time
    }

    fun updateGe() {
        Log.d(TAG_LOG, "ge = ($win - $lose) / $sum ")
        ge = ((win - lose).toDouble() / sum)
        val calendar = GregorianCalendar.getInstance()
        calendar.time = Date(updateDate)
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 1)
        val date = Date()
        if(calendar.time.after(date)) {
            updateDate = date.time
            lastGe = ge
        }
    }

    fun updateKe() {
        ke = (right - wrong).toDouble() / (right + wrong)
        val calendar = GregorianCalendar.getInstance()
        calendar.time = Date(updateDate)
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 1)
        val date = Date()
        if(date.after(calendar.time)) {
            updateDate = date.time
            lastKe = ke
        }
    }

    fun updateEpoch() {
        updateGe()
        updateKe()
    }
}
