package com.example.finance

interface NotificationListener {
    fun receiveNotification(notificationInfo: NotificationInfo)
    fun onListenerStatusChange()
}
