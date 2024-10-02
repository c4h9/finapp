package com.example.finance

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CategoriesScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Экран Категории")
    }
}

@Composable
fun OperationsScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Экран Операции")
    }
}

@Composable
fun SettingsScreen(
    notificationTextValue: String,
    onGetNotificationAccess: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Button(
            onClick = onGetNotificationAccess,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Получить доступ к уведомлениям")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = notificationTextValue,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun PermissionScreen(onPermissionGranted: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = onPermissionGranted) {
            Text("Предоставить разрешение")
        }
    }
}
