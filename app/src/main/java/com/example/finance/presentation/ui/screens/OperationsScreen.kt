package com.example.finance.presentation.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.finance.domain.entity.OperationEntity
import com.example.finance.presentation.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OperationsScreen(viewModel: MainViewModel) {
    val operations = viewModel.operations.collectAsState().value

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(operations) { operation ->
            OperationItem(operation)
        }
    }
}

@Composable
fun OperationItem(operation: OperationEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            val icon = getCategoryIconByName(operation.categoryName)
            Icon(
                imageVector = icon,
                contentDescription = operation.categoryName,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
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
        }
    }
}


// Функция для получения иконки по названию категории
fun getCategoryIconByName(name: String): ImageVector {
    return when (name) {
        "Еда" -> Icons.Default.Fastfood
        "Транспорт" -> Icons.Default.DirectionsCar
        "Дом" -> Icons.Default.Home
        "Работа" -> Icons.Default.Work
        "Спорт" -> Icons.Default.FitnessCenter
        "Покупки" -> Icons.Default.ShoppingCart
        "Развлечения" -> Icons.Default.Movie
        "Добавить" -> Icons.Default.Add
        else -> Icons.Default.Help
    }
}


fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
