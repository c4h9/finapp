package com.example.finance.domain.repository

import com.example.finance.NotificationInfo

interface NotificationRepository {
    fun getNotificationAccessPermission()
    fun setNotificationListener(listener: NotificationListener)
    fun removeNotificationListener()
}

interface NotificationListener {
    fun onNotificationReceived(notificationInfo: NotificationInfo)
    fun onListenerStatusChange()
}
