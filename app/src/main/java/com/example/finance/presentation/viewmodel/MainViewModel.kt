package com.example.finance.presentation.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.finance.data.repository.NotificationRepositoryImpl
import com.example.finance.NotificationInfo
import com.example.finance.domain.repository.NotificationListener
import com.example.finance.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application), NotificationListener {

    private val notificationRepository: NotificationRepository
    private val _notificationText = MutableStateFlow("")
    val notificationText = _notificationText.asStateFlow()

    init {
        notificationRepository = NotificationRepositoryImpl(getApplication())
    }

    fun getNotificationAccessPermission() {
        notificationRepository.getNotificationAccessPermission()
    }

    fun bindNotificationService() {
        notificationRepository.setNotificationListener(this)
    }

    fun unbindNotificationService() {
        notificationRepository.removeNotificationListener()
    }

    override fun onNotificationReceived(notificationInfo: NotificationInfo) {
        viewModelScope.launch {
            _notificationText.value += "\nNotification Message from: ${notificationInfo.packageName}" +
                    "\nTitle: ${notificationInfo.title}" +
                    "\nText: ${notificationInfo.text}" +
                    "\nInfo: ${notificationInfo.infoText}\n"
        }
    }

    override fun onListenerStatusChange() {
        // hui
    }
}
