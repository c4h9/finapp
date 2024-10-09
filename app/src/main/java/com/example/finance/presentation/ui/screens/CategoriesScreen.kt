package com.example.finance.presentation.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import com.example.finance.domain.entity.Category

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

    var openAddCategoryDialog by remember { mutableStateOf(false) }
    var openAddAmountDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }

    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        CategoriesGrid(
            categories = categories,
            onAddCategoryClick = {
                openAddCategoryDialog = true
            },
            onCategoryClick = { category ->
                selectedCategory = category
                openAddAmountDialog = true
            }
        )

        if (openAddCategoryDialog) {
            AddCategoryDialog(
                onDismissRequest = { openAddCategoryDialog = false },
                onConfirm = { newCategory ->
                    categories.add(newCategory)
                    openAddCategoryDialog = false
                }
            )
        }

        if (openAddAmountDialog && selectedCategory != null) {
            AddAmountDialog(
                categories = categories,
                initialCategory = selectedCategory!!,
                onDismissRequest = { openAddAmountDialog = false },
                onConfirm = { category, amount ->
                    openAddAmountDialog = false
                    // заглушка, пока я не сделал бд
                    Toast.makeText(
                        context,
                        "Категория: ${category.name}, Сумма: $amount",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
        }
    }
}


@Composable
fun CategoriesGrid(
    categories: List<Category>,
    onAddCategoryClick: () -> Unit,
    onCategoryClick: (Category) -> Unit
) {
    val columns = 4

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
                        onCategoryClick(category)
                    }
                }
            )
        }
    }
}

@Composable
fun AddAmountDialog(
    categories: List<Category>,
    initialCategory: Category,
    onDismissRequest: () -> Unit,
    onConfirm: (Category, Double) -> Unit
) {
    var selectedCategory by remember { mutableStateOf(initialCategory) }
    var amountText by remember { mutableStateOf("") }
    var amountError by remember { mutableStateOf(false) }

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
                    text = "Добавить запись",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Выбор категории
                CategoryDropdown(
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Ввод суммы
                OutlinedTextField(
                    value = amountText,
                    onValueChange = {
                        amountText = it
                        amountError = false
                    },
                    label = { Text("Сумма") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = amountError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                if (amountError) {
                    Text(
                        text = "Введите не нулевое значение",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

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
                            val amount = amountText.toDoubleOrNull()
                            if (amount == null || amount == 0.0) {
                                amountError = true
                            } else {
                                onConfirm(selectedCategory, amount)
                            }
                        },
                        enabled = amountText.isNotBlank()
                    ) {
                        Text("Добавить")
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryDropdown(
    categories: List<Category>,
    selectedCategory: Category,
    onCategorySelected: (Category) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = selectedCategory.name,
        onValueChange = {},
        label = { Text("Категория") },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true },
        readOnly = true,
        trailingIcon = {
            Icon(
                imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                contentDescription = null
            )
        }
    )

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        categories.forEach { category ->
            DropdownMenuItem(
                text = { Text(category.name) },
                onClick = {
                    onCategorySelected(category)
                    expanded = false
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