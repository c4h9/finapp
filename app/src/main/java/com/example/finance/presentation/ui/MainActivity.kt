package com.example.finance.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.finance.presentation.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize your ViewModel here (using DI or ViewModelProvider)
        viewModel = MainViewModel(application)
        setContent {
            MainContent(viewModel)
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.bindNotificationService()
    }

    override fun onStop() {
        super.onStop()
        viewModel.unbindNotificationService()
    }

//    override fun onStart() {
//        super.onStart()
//        Log.i(TAG, "onStart: called")
//        val serviceIntent = Intent(this, NotificationService::class.java)
//        serviceIntent.action = "ui"
//        notificationServiceConnection = NotificationServiceConnection()
//        val serviceStatus = bindService(serviceIntent, notificationServiceConnection!!, BIND_AUTO_CREATE)
//        Log.i(TAG, "service status: $serviceStatus")
//    }
//
//    override fun onStop() {
//        super.onStop()
//        notificationServiceConnection?.let {
//            unbindService(it)
//        }
//    }
//    override fun onResume() {
//        super.onResume()
//        Log.i(TAG, "onResume: called")
//
//        notificationService?.setNotificationListener(this)
//    }
//
//    override fun receiveNotification(notificationInfo: NotificationInfo) {
//        Log.i(TAG, "${notificationInfo.packageName}: Notification received")
//        notificationText.value += "\nNotification Message from: ${notificationInfo.packageName}" +
//                "\nTitle: ${notificationInfo.title}" +
//                "\nText: ${notificationInfo.text}" +
//                "\nInfo: ${notificationInfo.infoText}\n"
//    }
//
//    override fun onListenerStatusChange() {
//        //
//    }
//
//    private fun getNotificationAccessPermission(context: Context) {
//        Log.i(TAG, "getNotificationAccessPermission: called")
//        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        val notificationListenerComp = ComponentName(context, NotificationService::class.java)
//        val notificationAccessGranted = notificationManager.isNotificationListenerAccessGranted(notificationListenerComp)
//
//        if (notificationAccessGranted) {
//            Log.i(TAG, "notification_access already granted")
//            Toast.makeText(applicationContext, "notification_access already granted", LENGTH_SHORT).show()
//        } else {
//            Log.i(TAG, "Request for notification access permission")
//            val showPermissionIntent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
//            showPermissionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            context.startActivity(showPermissionIntent)
//        }
//    }
//
//
//    private inner class NotificationServiceConnection : ServiceConnection {
//        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
//            notificationService = (service as NotificationService.NotificationBinder).getService()
//            notificationService?.setNotificationListener(this@MainActivity)
//        }
//
//        override fun onServiceDisconnected(name: ComponentName?) {
//            notificationService = null
//        }
//    }
//
//    @OptIn(ExperimentalMaterial3Api::class)
//    @Composable
//    fun MainContent() {
//        val navController = rememberNavController()
//        val notificationTextValue by notificationText
//        val context = LocalContext.current
//
//        Scaffold(
//            topBar = {
//                TopAppBar(
//                    title = { Text("Finance Notification Listener") }
//                )
//            },
//            bottomBar = {
//                BottomNavigationBar(navController = navController)
//            },
//        ) { innerPadding ->
//            Box(modifier = Modifier.padding(innerPadding)) {
//                NavigationGraph(
//                    navController = navController,
//                    notificationTextValue = notificationTextValue,
//                    onGetNotificationAccess = {
//                        getNotificationAccessPermission(context)
//                    }
//                )
//            }
//        }
//    }
//
//    @Composable
//    fun BottomNavigationBar(navController: NavHostController) {
//        val items = listOf(
//            Screen.Categories,
//            Screen.Operations,
//            Screen.Settings
//        )
//        val currentBackStackEntry by navController.currentBackStackEntryAsState()
//        val currentDestination = currentBackStackEntry?.destination
//
//        NavigationBar {
//            items.forEach { screen ->
//                NavigationBarItem(
//                    icon = { Icon(screen.icon, contentDescription = null) },
//                    label = { Text(screen.title) },
//                    selected = currentDestination?.route == screen.route,
//                    onClick = {
//                        navController.navigate(screen.route) {
//                            navController.graph.startDestinationRoute?.let { route ->
//                                popUpTo(route) {
//                                    saveState = true
//                                }
//                            }
//                            launchSingleTop = true
//                            restoreState = true
//                        }
//                    }
//                )
//            }
//        }
//    }
//
//    @Composable
//    fun NavigationGraph(
//        navController: NavHostController,
//        notificationTextValue: String,
//        onGetNotificationAccess: () -> Unit
//    ) {
//        NavHost(navController = navController, startDestination = Screen.Categories.route) {
//            composable(Screen.Categories.route) {
//                CategoriesScreen()
//            }
//            composable(Screen.Operations.route) {
//                OperationsScreen()
//            }
//            composable(Screen.Settings.route) {
//                SettingsScreen(
//                    notificationTextValue = notificationTextValue,
//                    onGetNotificationAccess = onGetNotificationAccess
//                )
//            }
//            composable(Screen.Permission.route) {
//                PermissionScreen(onPermissionGranted = {
//                    getNotificationAccessPermission(context)
//                })
//            }
//        }
//    }


}
