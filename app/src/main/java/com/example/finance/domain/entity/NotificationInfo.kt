package com.example.finance

import android.app.Notification
import android.os.Bundle
import android.service.notification.StatusBarNotification
import android.util.Log

data class NotificationInfo(
    var packageName: String = "",
    var title: String? = null,
    var text: String? = null,
    var subText: String? = null,
    var infoText: String? = null
) {
    private val TAG = NotificationInfo::class.simpleName

    constructor(sbn: StatusBarNotification) : this() {
        this.packageName = sbn.packageName
        val notification: Notification = sbn.notification
        if (notification != null) {
            val extras: Bundle = notification.extras
            this.title = extras.getString("android.title")
            this.text = extras.getCharSequence("android.text")?.toString()
            this.subText = extras.getCharSequence("android.subText")?.toString()
            this.infoText = extras.getCharSequence("android.infoText")?.toString()
        } else {
            Log.i(TAG, "Notification is null")
        }
    }

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "packageName" to packageName,
            "title" to title,
            "text" to text,
            "subText" to subText,
            "infoText" to infoText
        )
    }
}

