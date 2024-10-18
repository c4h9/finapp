package com.example.finance.presentation.ui.screens

import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.finance.data.service.NotificationService
import com.example.finance.presentation.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun PermissionScreen(navController: NavController, viewModel: MainViewModel, onPermissionGranted: () -> Unit) {
    val context = LocalContext.current
    var dontShowAgain by remember { mutableStateOf(false) }
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val notificationListenerComp = ComponentName(context, NotificationService::class.java)
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Доступ к уведомлениям",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Чтобы приложение могло отслеживать транзакции, ему необходимо разрешение на доступ к уведомлениям."
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Checkbox(
                checked = dontShowAgain,
                onCheckedChange = {
                    dontShowAgain = it
                    coroutineScope.launch {
                        viewModel.setDontShowPermissionScreen(it)
                    }
                }
            )
            Text(text = "Больше не показывать")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        ) {
            Text("Запросить доступ к уведомлениям")
        }
    }

    val hasNotificationAccess = notificationManager.isNotificationListenerAccessGranted(notificationListenerComp)
    LaunchedEffect(hasNotificationAccess) {
        if (hasNotificationAccess) {
            navController.navigate("categories") {
                popUpTo("permission") { inclusive = true }
            }
        }
    }
}
