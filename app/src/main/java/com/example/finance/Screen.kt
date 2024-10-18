package com.example.finance

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object FirstLaunch : Screen("first_launch", "Первый запуск", Icons.Default.Info)
    object Categories : Screen("categories", "Категории", Icons.Default.Home)
    object Operations : Screen("operations", "Операции", Icons.Default.List)
    object Settings : Screen("settings", "Настройки", Icons.Default.Settings)
    object Permission : Screen("permission", "Разрешения", Icons.Default.Info)
}
