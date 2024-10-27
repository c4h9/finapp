package com.example.finance.presentation.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.finance.Screen
import com.example.finance.presentation.viewmodel.MainViewModel

@Composable
fun MainContent(viewModel: MainViewModel) {
    val isDataLoaded by viewModel.isDataLoaded.collectAsState()

    if (!isDataLoaded) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(100.dp)) {
                drawCircle(
                    color = Color.Blue,
                    radius = size.minDimension / 2
                )
            }
            Icon(
                imageVector = Icons.Filled.Wallet,
                contentDescription = null,
                modifier = Modifier.size(50.dp),
                tint = Color.White
            )
        }
    } else {
        val navController = rememberNavController()
        val notificationTextValue = viewModel.notificationText.collectAsState().value
        val currentBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStackEntry?.destination

        val hideBottomBarRoutes = listOf(Screen.FirstLaunch.route, Screen.Permission.route)

        val showBottomBar = currentDestination?.route !in hideBottomBarRoutes

        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    BottomNavigationBar(navController = navController)
                }
            },
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                NavigationGraph(
                    navController = navController,
                    viewModel = viewModel,
                    notificationTextValue = notificationTextValue,
                    onGetNotificationAccess = {
                        viewModel.getNotificationAccessPermission()
                    }
                )
            }
        }
    }
}