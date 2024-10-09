package com.example.finance.presentation.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.finance.Screen

val navigationItems = listOf(
    Screen.Categories,
    Screen.Operations,
    Screen.Settings
)

@Composable
fun BottomNavigationBar(navController: NavHostController, items: List<Screen> = navigationItems) {
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry.value?.destination

    NavigationBar {
        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = null) },
                label = { Text(screen.title) },
                selected = currentDestination?.route == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
