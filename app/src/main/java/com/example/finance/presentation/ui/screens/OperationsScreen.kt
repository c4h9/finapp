package com.example.finance.presentation.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.finance.data.entity.OperationEntity
import com.example.finance.domain.entity.Category
import com.example.finance.domain.entity.CategoryIconType
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

//@Preview(showBackground = true)
//@Composable
//fun PreviewOperationsScreen() {
//    val sampleOperations = listOf(
//        OperationEntity(id = 1, categoryName = "Food", amount = 20.0, sourceName = "Карта", timestamp = System.currentTimeMillis(), iconName = "FoodIcon"),
//        OperationEntity(id = 2, categoryName = "Transport", amount = 15.0, sourceName = "Карта", timestamp = System.currentTimeMillis(), iconName = "TransportIcon"),
//        OperationEntity(id = 3, categoryName = "Entertainment", amount = 50.0, sourceName = "Наличные", timestamp = System.currentTimeMillis(), iconName = "EntertainmentIcon")
//    )
//
//    OperationsScreen(
//        operations = sampleOperations,
//        onDeleteOperations = {},
//        onEditConfirm = { -> }
//    )
//}

@Composable
fun OperationsScreen(
    categoriesValue: List<Category>,
    operations: List<OperationEntity>,
    onDeleteOperations: (List<Int>) -> Unit,
    onEditConfirm: (OperationEntity) -> Unit,
) {
    var showButton by rememberSaveable { mutableStateOf(false) }
    var selectionMode by rememberSaveable { mutableStateOf(false) }
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var dialogOperation by remember { mutableStateOf<OperationEntity?>(null) }
    val selectedOperations = remember { mutableStateOf(mutableSetOf<Int>()) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(operations) { operation ->
                OperationItem(
                    operation = operation,
                    isSelected = selectedOperations.value.contains(operation.id),
                    onLongPress = {
                        if (!selectionMode) {
                            selectionMode = true
                            toggleSelection(operation.id, selectedOperations)
                            showButton = true
                        }
                    },
                    onClick = {
                        if (!selectionMode) {
                            dialogOperation = operation
                            showDialog = true
                        } else {
                            toggleSelection(operation.id, selectedOperations)
                            if (selectedOperations.value.isEmpty()) {
                                selectionMode = false
                                showButton = false
                            }
                        }
                    }
                )
            }
        }

        if (showButton && selectedOperations.value.isNotEmpty()) {
            FloatingActionButton(
                onClick = {
                    onDeleteOperations(selectedOperations.value.toList())
                    selectedOperations.value.clear()
                    showButton = false
                    selectionMode = false
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }

        if (showDialog && dialogOperation != null) {
            EditOperationDialog(
                operation = dialogOperation!!,
                categories = categoriesValue,
                onDismissRequest = { showDialog = false },
                onConfirm = {
                    onEditConfirm(it)
                    showDialog = false
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OperationItem(
    operation: OperationEntity,
    isSelected: Boolean,
    onLongPress: () -> Unit,
    onClick: () -> Unit
) {
    val iconType = getCategoryIconByName(operation.iconName)
    val icon = iconType.icon

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongPress
            ),
        shape = CircleShape,
        elevation = if (isSelected) CardDefaults.cardElevation(4.dp) else CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.background
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(5.dp)
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
                    text = operation.sourceName,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                text = "${operation.amount}",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

fun toggleSelection(operationId: Int, selectedOperations: MutableState<MutableSet<Int>>) {
    val newSet = selectedOperations.value.toMutableSet()
    if (newSet.contains(operationId)) {
        newSet.remove(operationId)
    } else {
        newSet.add(operationId)
    }
    selectedOperations.value = newSet
}

fun getCategoryIconByName(iconName: String): CategoryIconType {
    return try {
        CategoryIconType.valueOf(iconName)
    } catch (e: IllegalArgumentException) {
        CategoryIconType.Help
    }
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

@Composable
fun EditOperationDialog(
    operation: OperationEntity,
    categories: List<Category>,
    onDismissRequest: () -> Unit,
    onConfirm: (OperationEntity) -> Unit
) {
    var editedAmount by remember { mutableStateOf(operation.amount.toString()) }
    var selectedCategory by remember { mutableStateOf(operation.categoryName) }
    var selectedSourceName by remember { mutableStateOf(operation.sourceName) }
    var selectedDate by remember { mutableStateOf(operation.timestamp) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Редактировать операцию") },
        text = {
            Column {
                DatePicker(
                    timestamp = selectedDate,
                    onDateSelected = { selectedDate = it }
                )
                OutlinedTextField(
                    label = { Text("Сумма") },
                    value = editedAmount,
                    onValueChange = { editedAmount = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                CategoryDropdown(
                    categories = categories,
                    selectedCategory = categories.first { it.name == selectedCategory },
                    onCategorySelected = { selectedCategory = it.name }
                )
                OutlinedTextField(
                    label = { Text("Счёт") },
                    value = selectedSourceName,
                    onValueChange = { selectedSourceName = it }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val amount = editedAmount.toDoubleOrNull()
                if (amount != null) {
                    val updatedOperation = operation.copy(
                        amount = amount,
                        categoryName = selectedCategory,
                        sourceName = selectedSourceName,
                        timestamp = selectedDate
                    )
                    onConfirm(updatedOperation)
                } else {
                }
            }) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Отмена")
            }
        }
    )
}


@Composable
fun DatePicker(
    timestamp: Long,
    onDateSelected: (Long) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance().apply {
        timeInMillis = timestamp
    }
    val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    val dateText = dateFormatter.format(Date(timestamp))
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedCalendar = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            onDateSelected(selectedCalendar.timeInMillis)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    OutlinedTextField(
        label = { Text("Дата") },
        value = dateText,
        onValueChange = {},
        readOnly = true,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                datePickerDialog.show()
            }
    )
}



