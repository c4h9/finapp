package com.example.finance

import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class NotificationService : NotificationListenerService() {
    private val mBinder = NotificationBinder()
    private val TAG = NotificationService::class.simpleName
    private lateinit var context: Context

    companion object {
        private var listener: NotificationListener? = null
    }

    override fun onCreate() {
        Log.i(TAG, "Service started")
        super.onCreate()
        context = applicationContext
    }

    override fun onBind(intent: Intent): IBinder? {
        return if (intent.action == "ui") {
            mBinder
        } else {
            super.onBind(intent)
        }
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.i(TAG, "onListenerConnected called")
        listener?.onListenerStatusChange()
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Log.i(TAG, "onListenerDisconnected called")
        listener?.onListenerStatusChange()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        Log.i(TAG, "onNotificationPosted invoked")
        Log.i(TAG, "Notification from: ${sbn.packageName}")

        val notificationInfo = NotificationInfo(sbn)
        // Send notification information to UI
        listener?.receiveNotification(notificationInfo)
    }

    fun setNotificationListener(listener: NotificationListener) {
        Log.i(TAG, "setNotificationListener called")
        NotificationService.listener = listener
    }

    fun removeNotificationListener() {
        Log.i(TAG, "removeNotificationListener: remove notification listener")
        NotificationService.listener = null
    }

    inner class NotificationBinder : Binder() {
        fun getService(): NotificationService {
            Log.i(TAG, "getService: called")
            return this@NotificationService
        }
    }
}
