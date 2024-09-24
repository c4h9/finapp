package com.example.finance

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity(), NotificationListener {
    private val TAG = MainActivity::class.simpleName
    private var notificationService: NotificationService? = null
    private var notificationServiceConnection: NotificationServiceConnection? = null
    private lateinit var context: Context
    private val notificationText = mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = applicationContext

        setContent {
            MainContent()
        }
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "onStart: called")
        val serviceIntent = Intent(this, NotificationService::class.java)
        serviceIntent.action = "ui"
        notificationServiceConnection = NotificationServiceConnection()
        val serviceStatus = bindService(serviceIntent, notificationServiceConnection!!, BIND_AUTO_CREATE)
        Log.i(TAG, "service status: $serviceStatus")
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume: called")
        notificationService?.setNotificationListener(this)
    }

    override fun onStop() {
        super.onStop()
        notificationServiceConnection?.let {
            unbindService(it)
        }
    }

    override fun receiveNotification(notificationInfo: NotificationInfo) {
        Log.i(TAG, "${notificationInfo.packageName}: Notification received")
        notificationText.value += "\nNotification Message from: ${notificationInfo.packageName}" +
                "\nTitle: ${notificationInfo.title}" +
                "\nText: ${notificationInfo.text}" +
                "\nInfo: ${notificationInfo.infoText}\n"
    }

    override fun onListenerStatusChange() {
        // Implement if needed
    }

    private fun getNotificationAccessPermission() {
        Log.i(TAG, "getNotificationAccessPermission: called")
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationListenerComp = ComponentName(this, NotificationService::class.java)
        val notificationAccessGranted = notificationManager.isNotificationListenerAccessGranted(notificationListenerComp)

        if (notificationAccessGranted) {
            Log.i(TAG, "notification_access already granted")
        } else {
            Log.i(TAG, "Request for notification access permission")
            val showPermissionIntent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
            startActivity(showPermissionIntent)
        }
    }

    private inner class NotificationServiceConnection : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            notificationService = (service as NotificationService.NotificationBinder).getService()
            notificationService?.setNotificationListener(this@MainActivity)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            notificationService = null
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun MainContent() {
        val notificationTextValue by notificationText

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Finance Notification Listener") }
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                Button(
                    onClick = {
                        Log.i(TAG, "NotificationAccess: button onClick() called")
                        getNotificationAccessPermission()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Get Notification Access")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = notificationTextValue,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
