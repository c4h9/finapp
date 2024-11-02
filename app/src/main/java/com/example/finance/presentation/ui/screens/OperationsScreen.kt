package com.example.finance.presentation.ui.screens

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.finance.data.entity.OperationEntity
import com.example.finance.domain.entity.Category
import com.example.finance.domain.entity.CategoryIconType
import com.example.finance.presentation.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OperationsScreen(
    operations: List<OperationEntity>,
    categories: List<Category>,
    viewModel: MainViewModel
) {
    var showButton by remember { mutableStateOf(false) }
    val selectedOperations = remember { mutableStateOf(mutableSetOf<Int>()) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(operations) { operation ->
                OperationItem(
                    operation = operation,
                    showButton = showButton,
                    isSelected = selectedOperations.value.contains(operation.id),
                    onLongPress = {
                        showButton = true
                    },
                    onCheckedChange = { isChecked ->
                        if (isChecked) {
                            selectedOperations.value.add(operation.id)
                        } else {
                            selectedOperations.value.remove(operation.id)
                        }
                        selectedOperations.value = selectedOperations.value.toMutableSet()
                    }
                )
            }
        }

        if (showButton && selectedOperations.value.isNotEmpty()) {
            FloatingActionButton(
                onClick = {
                    viewModel.deleteOperationsByIds(selectedOperations.value.toList())
                    selectedOperations.value.clear()
                    showButton = false
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}

@Composable
fun OperationItem(
    operation: OperationEntity,
    showButton: Boolean,
    isSelected: Boolean,
    onLongPress: () -> Unit,
    onCheckedChange: (Boolean) -> Unit
) {
    val iconType = getCategoryIconByName(operation.iconName)
    val icon = iconType.icon

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        onLongPress()
                    }
                )
            },
        shape = CircleShape,
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = operation.categoryName,
                modifier = Modifier
                    .size(50.dp)
                    .padding(horizontal = 10.dp)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 5.dp)
            ) {
                Text(
                    text = operation.categoryName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Сумма: ${operation.amount}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Дата: ${formatDate(operation.timestamp)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (showButton) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = onCheckedChange,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}





fun getCategoryIconByName(iconName: String): CategoryIconType {
    return try {
        CategoryIconType.valueOf(iconName)
    } catch (e: IllegalArgumentException) {
        CategoryIconType.Help // Возвращаем иконку по умолчанию
    }
}



fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
