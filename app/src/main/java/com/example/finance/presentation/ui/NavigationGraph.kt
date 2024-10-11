package com.example.finance.presentation.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.finance.Screen
import com.example.finance.presentation.ui.screens.CategoriesScreen
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
    NavHost(
        navController = navController,
        startDestination = Screen.Categories.route
    ) {
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
            PermissionScreen(onPermissionGranted = {
                onGetNotificationAccess()
            })
        }
    }
}

