package com.example.finance.presentation.ui.screens

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun SettingsScreen(
    notificationTextValue: String,
    onGetNotificationAccess: () -> Unit
) {
    // Your Settings UI components
    Button(onClick = {
        onGetNotificationAccess()
    }) {
        Text("Get Notification Access")
    }
    Text(notificationTextValue)
}
