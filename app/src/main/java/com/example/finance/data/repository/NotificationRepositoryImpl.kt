package com.example.finance.data.repository

import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.finance.data.service.NotificationService
import com.example.finance.domain.repository.NotificationListener
import com.example.finance.domain.repository.NotificationRepository

class NotificationRepositoryImpl(
    private val context: Context
) : NotificationRepository {

    private val TAG = NotificationRepositoryImpl::class.simpleName
    private var listener: NotificationListener? = null
    private var notificationService: NotificationService? = null
    private var notificationServiceConnection: NotificationServiceConnection? = null

    override fun getNotificationAccessPermission() {
        Log.i(TAG, "getNotificationAccessPermission called")
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationListenerComp = ComponentName(context, NotificationService::class.java)
        val notificationAccessGranted = notificationManager.isNotificationListenerAccessGranted(notificationListenerComp)

        if (notificationAccessGranted) {
            Log.i(TAG, "Notification access already granted")
        } else {
            Log.i(TAG, "Requesting notification access permission")
            val showPermissionIntent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
            showPermissionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(showPermissionIntent)
        }
    }

    override fun setNotificationListener(listener: NotificationListener) {
        Log.i(TAG, "Setting notification listener")
        this.listener = listener
        bindNotificationService()
    }

    override fun removeNotificationListener() {
        Log.i(TAG, "Removing notification listener")
        listener = null
        unbindNotificationService()
    }

    private fun bindNotificationService() {
        val serviceIntent = Intent(context, NotificationService::class.java)
        serviceIntent.action = NotificationService.ACTION_BIND_LISTENER
        notificationServiceConnection = NotificationServiceConnection()
        context.bindService(serviceIntent, notificationServiceConnection!!, Context.BIND_AUTO_CREATE)
    }

    private fun unbindNotificationService() {
        notificationServiceConnection?.let {
            context.unbindService(it)
            notificationServiceConnection = null
        }
    }

    inner class NotificationServiceConnection : android.content.ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            notificationService = (service as NotificationService.NotificationBinder).getService()
            notificationService?.setNotificationListener(listener)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            notificationService = null
        }
    }
}
