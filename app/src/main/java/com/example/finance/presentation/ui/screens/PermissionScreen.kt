package com.example.finance.presentation.ui.screens

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun PermissionScreen(onPermissionGranted: () -> Unit) {
    Button(onClick = {
        onPermissionGranted()
    }) {
        Text("Grant Notification Access")
    }
}
