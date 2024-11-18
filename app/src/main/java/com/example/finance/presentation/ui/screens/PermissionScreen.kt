// PermissionScreen.kt
package com.example.finance.presentation.ui.screens

import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.finance.data.service.NotificationService

@Composable
fun PermissionScreen(
    onDontShowAgainChanged: (Boolean) -> Unit,
    onRequestNotificationAccess: () -> Unit,
    onPermissionGranted: () -> Unit
) {
    val context = LocalContext.current
    var dontShowAgain by remember { mutableStateOf(false) }
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val notificationListenerComp = ComponentName(context, NotificationService::class.java)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Доступ к уведомлениям",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Чтобы приложение могло отслеживать транзакции, ему необходимо разрешение на доступ к уведомлениям."
        )
        Spacer(modifier = Modifier.weight(1f))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = dontShowAgain,
                onCheckedChange = {
                    dontShowAgain = it
                    onDontShowAgainChanged(it)
                }
            )
            Text(text = "Больше не показывать")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRequestNotificationAccess,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Запросить доступ к уведомлениям")
        }
    }

    val hasNotificationAccess = notificationManager.isNotificationListenerAccessGranted(notificationListenerComp)
    LaunchedEffect(hasNotificationAccess) {
        if (hasNotificationAccess) {
            onPermissionGranted()
        }
    }
}
