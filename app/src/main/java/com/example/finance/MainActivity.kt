package com.example.finance

import android.app.NotificationManager
import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

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
        //
    }

    private fun getNotificationAccessPermission(context: Context) {
        Log.i(TAG, "getNotificationAccessPermission: called")
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationListenerComp = ComponentName(context, NotificationService::class.java)
        val notificationAccessGranted = notificationManager.isNotificationListenerAccessGranted(notificationListenerComp)

        if (notificationAccessGranted) {
            Log.i(TAG, "notification_access already granted")
            Toast.makeText(applicationContext, "notification_access already granted", LENGTH_SHORT).show()
        } else {
            Log.i(TAG, "Request for notification access permission")
            val showPermissionIntent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
            showPermissionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(showPermissionIntent)
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
    @Composable
    fun MainContent() {
        val navController = rememberNavController()
        val notificationTextValue by notificationText
        val context = LocalContext.current

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Finance Notification Listener") }
                )
            },
            bottomBar = {
                BottomNavigationBar(navController = navController)
            },
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                NavigationGraph(
                    navController = navController,
                    notificationTextValue = notificationTextValue,
                    onGetNotificationAccess = {
                        getNotificationAccessPermission(context)
                    }
                )
            }
        }
    }

    @Composable
    fun BottomNavigationBar(navController: NavHostController) {
        val items = listOf(
            Screen.Categories,
            Screen.Operations,
            Screen.Settings
        )
        val currentBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStackEntry?.destination

        NavigationBar {
            items.forEach { screen ->
                NavigationBarItem(
                    icon = { Icon(screen.icon, contentDescription = null) },
                    label = { Text(screen.title) },
                    selected = currentDestination?.route == screen.route,
                    onClick = {
                        navController.navigate(screen.route) {
                            navController.graph.startDestinationRoute?.let { route ->
                                popUpTo(route) {
                                    saveState = true
                                }
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }

    @Composable
    fun NavigationGraph(
        navController: NavHostController,
        notificationTextValue: String,
        onGetNotificationAccess: () -> Unit
    ) {
        NavHost(navController = navController, startDestination = Screen.Categories.route) {
            composable(Screen.Categories.route) {
                CategoriesScreen()
            }
            composable(Screen.Operations.route) {
                OperationsScreen()
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    notificationTextValue = notificationTextValue,
                    onGetNotificationAccess = onGetNotificationAccess
                )
            }
            composable(Screen.Permission.route) {
                PermissionScreen(onPermissionGranted = {
                    getNotificationAccessPermission(context)
                })
            }
        }
    }


}
