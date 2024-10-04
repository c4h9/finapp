package com.example.finance.data.service

import android.app.Notification
import android.content.Intent
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.example.finance.NotificationInfo
import com.example.finance.domain.repository.NotificationListener

class NotificationService : NotificationListenerService() {

    private val TAG = NotificationService::class.simpleName
    private val binder = NotificationBinder()
    private var listener: NotificationListener? = null

    companion object {
        const val ACTION_BIND_LISTENER = "com.example.finance.BIND_LISTENER"
    }

    override fun onBind(intent: Intent): IBinder? {
        return if (intent.action == ACTION_BIND_LISTENER) {
            binder
        } else {
            super.onBind(intent)
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        Log.i(TAG, "Notification posted from: ${sbn.packageName}")
        val notificationInfo = sbn.toNotificationInfo()
        listener?.onNotificationReceived(notificationInfo)
    }

    fun setNotificationListener(listener: NotificationListener?) {
        Log.i(TAG, "setNotificationListener called")
        this.listener = listener
    }

    fun removeNotificationListener() {
        Log.i(TAG, "removeNotificationListener called")
        listener = null
    }

    inner class NotificationBinder : Binder() {
        fun getService(): NotificationService = this@NotificationService
    }

    private fun StatusBarNotification.toNotificationInfo(): NotificationInfo {
        val notification: Notification = this.notification
        val extras: Bundle = notification.extras
        val title = extras.getString("android.title")
        val text = extras.getCharSequence("android.text")?.toString()
        val subText = extras.getCharSequence("android.subText")?.toString()
        val infoText = extras.getCharSequence("android.infoText")?.toString()

        return NotificationInfo(
            packageName = this.packageName,
            title = title,
            text = text,
            subText = subText,
            infoText = infoText
        )
    }
}
