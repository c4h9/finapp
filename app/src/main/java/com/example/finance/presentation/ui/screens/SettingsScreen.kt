package com.example.finance.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.finance.presentation.viewmodel.MainViewModel

@Composable
fun SettingsScreen(
    notificationTextValue: String,
    onGetNotificationAccess: () -> Unit,
    viewModel: MainViewModel
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = {
            onGetNotificationAccess()
        }) {
            Text("Запросить доступ к уведомлениям")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Список ключевых слов:")

        val keywords by viewModel.keywords.collectAsState()
        val newKeywordText = remember { mutableStateOf("") }

        LazyColumn {
            items(keywords) { keyword ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = keyword)
                    IconButton(onClick = { viewModel.removeKeyword(keyword) }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Удалить")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = newKeywordText.value,
                onValueChange = { newKeywordText.value = it },
                label = { Text("Новое ключевое слово") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    viewModel.addKeyword(newKeywordText.value)
                    newKeywordText.value = ""
                },
                enabled = newKeywordText.value.isNotBlank()
            ) {
                Text("Добавить")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Отображение текста уведомлений для отладки
        Text(notificationTextValue)
    }
}
