package com.example.finance

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight


@Composable
fun CategoriesScreen() {
    val categories = remember {
        mutableStateListOf(
            Category("Еда", Icons.Default.Fastfood),
            Category("Транспорт", Icons.Default.DirectionsCar),
            Category("Дом", Icons.Default.Home),
            Category("Работа", Icons.Default.Work),
            Category("Спорт", Icons.Default.FitnessCenter),
            Category("Покупки", Icons.Default.ShoppingCart),
            Category("Развлечения", Icons.Default.Movie),
        )
    }

    val openDialog = remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        CategoriesGrid(
            categories = categories,
            onAddCategoryClick = {
                openDialog.value = true
            }
        )

        if (openDialog.value) {
            AddCategoryDialog(
                onDismissRequest = { openDialog.value = false },
                onConfirm = { newCategory ->
                    categories.add(newCategory)
                    openDialog.value = false
                }
            )
        }
    }
}

@Composable
fun CategoriesGrid(
    categories: List<Category>,
    onAddCategoryClick: () -> Unit
) {
    val columns = 2 // количество колонок в сетке

    val items = categories + Category("Добавить", Icons.Default.Add)

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(items.size) { index ->
            val category = items[index]
            CategoryItem(
                category = category,
                onClick = {
                    if (category.name == "Добавить") {
                        onAddCategoryClick()
                    } else {
                        // Обработка нажатия на категорию (если требуется)
                    }
                }
            )
        }
    }
}

@Composable
fun CategoryItem(
    category: Category,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = category.name,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = category.name,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun AddCategoryDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (Category) -> Unit
) {
    var categoryName by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf(Icons.Default.Home) }

    val iconOptions = listOf(
        Icons.Default.Home,
        Icons.Default.ShoppingCart,
        Icons.Default.Fastfood,
        Icons.Default.Work,
        Icons.Default.DirectionsCar,
        Icons.Default.FitnessCenter,
        Icons.Default.Movie,
        Icons.Default.School,
        Icons.Default.Pets,
        Icons.Default.TravelExplore
    )

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Добавить категорию",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    label = { Text("Название категории") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Выберите иконку",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                IconSelectionGrid(
                    icons = iconOptions,
                    selectedIcon = selectedIcon,
                    onIconSelected = { selectedIcon = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text("Отмена")
                    }
                    TextButton(
                        onClick = {
                            val newCategory = Category(categoryName, selectedIcon)
                            onConfirm(newCategory)
                        },
                        enabled = categoryName.isNotBlank()
                    ) {
                        Text("Добавить")
                    }
                }
            }
        }
    }
}

@Composable
fun IconSelectionGrid(
    icons: List<ImageVector>,
    selectedIcon: ImageVector,
    onIconSelected: (ImageVector) -> Unit
) {
    val columns = 5
    val rows = if (icons.size % columns == 0) {
        icons.size / columns
    } else {
        icons.size / columns + 1
    }

    Column {
        for (row in 0 until rows) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                for (column in 0 until columns) {
                    val index = row * columns + column
                    if (index < icons.size) {
                        val icon = icons[index]
                        IconButton(
                            onClick = { onIconSelected(icon) }
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (icon == selectedIcon) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
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
