package com.example.finance.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.finance.Screen
import com.example.finance.presentation.ui.screens.CategoriesScreen
import com.example.finance.presentation.ui.screens.FirstLaunchScreen
import com.example.finance.presentation.ui.screens.OperationsScreen
import com.example.finance.presentation.ui.screens.PermissionScreen
import com.example.finance.presentation.ui.screens.SettingsScreen
import com.example.finance.presentation.viewmodel.MainViewModel

@Composable
fun NavigationGraph(
    navController: NavHostController,
    viewModel: MainViewModel,
    notificationTextValue: String,
    onGetNotificationAccess: () -> Unit
) {
    val isFirstLaunch by viewModel.isFirstLaunch.collectAsState()

    val startDestination = if (isFirstLaunch == true) {
        Screen.FirstLaunch.route
    } else {
        Screen.Categories.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.FirstLaunch.route) {
            FirstLaunchScreen(navController = navController, viewModel = viewModel)
        }
        composable(Screen.Categories.route) {
            CategoriesScreen(viewModel)
        }
        composable(Screen.Operations.route) {
            OperationsScreen(viewModel)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                notificationTextValue = notificationTextValue,
                onGetNotificationAccess = onGetNotificationAccess,
                viewModel = viewModel
            )
        }
        composable(Screen.Permission.route) {
            PermissionScreen(
                navController = navController,
                viewModel = viewModel,
                onPermissionGranted = {
                    onGetNotificationAccess()
                }
            )
        }
    }
}

