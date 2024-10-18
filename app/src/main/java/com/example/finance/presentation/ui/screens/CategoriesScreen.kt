package com.example.finance.presentation.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.finance.domain.entity.Category
import com.example.finance.domain.entity.CategoryIconType
import com.example.finance.presentation.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(viewModel: MainViewModel) {
    val categories by viewModel.categories.collectAsState()

    var openAddCategoryBottomSheet by remember { mutableStateOf(false) }
    var openAddAmountBottomSheet by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }

    val coroutineScope = rememberCoroutineScope()

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val amountBottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    var selectedPeriod by remember { mutableStateOf("Месяц") }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            Column(modifier = Modifier.fillMaxSize()) {
                TimePeriodChips(
                    selectedPeriod = selectedPeriod,
                    onPeriodSelected = { period ->
                        selectedPeriod = period
                        //
                    }
                )
                BudgetCard()
                Spacer(modifier = Modifier.height(16.dp))
                CategoriesGrid(
                    categories = categories,
                    onAddCategoryClick = {
                        openAddCategoryBottomSheet = true
                        coroutineScope.launch { bottomSheetState.show() }
                    },
                    onCategoryClick = { category ->
                        selectedCategory = category
                        openAddAmountBottomSheet = true
                        coroutineScope.launch { amountBottomSheetState.show() }
                    }
                )

                if (openAddAmountBottomSheet && selectedCategory != null) {
                    ModalBottomSheet(
                        onDismissRequest = {
                            coroutineScope.launch { amountBottomSheetState.hide() }
                            openAddAmountBottomSheet = false
                        },
                        sheetState = amountBottomSheetState,
                        dragHandle = null
                    ) {
                        AddAmountBottomSheetContent(
                            categories = categories,
                            initialCategory = selectedCategory!!,
                            onDismissRequest = {
                                coroutineScope.launch { amountBottomSheetState.hide() }
                                openAddAmountBottomSheet = false
                            },
                            onConfirm = { category, amount ->
                                viewModel.addOperation(category, amount)
                                coroutineScope.launch { amountBottomSheetState.hide() }
                                openAddAmountBottomSheet = false
                            }
                        )
                    }
                }
            }

            if (openAddCategoryBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        coroutineScope.launch { bottomSheetState.hide() }
                        openAddCategoryBottomSheet = false
                    },
                    sheetState = bottomSheetState,
                    dragHandle = null
                ) {
                    AddCategoryBottomSheetContent(
                        onDismissRequest = {
                            coroutineScope.launch { bottomSheetState.hide() }
                            openAddCategoryBottomSheet = false
                        },
                        onConfirm = { newCategory ->
                            viewModel.addCategory(newCategory)
                            coroutineScope.launch { bottomSheetState.hide() }
                            openAddCategoryBottomSheet = false
                        }
                    )
                }
            }
        }
    }
}




@Composable
fun CategoriesGrid(
    categories: List<Category>,
    onAddCategoryClick: () -> Unit,
    onCategoryClick: (Category) -> Unit
) {
    val itemWidth = 80.dp

    val items = categories + Category("Добавить", CategoryIconType.Add)

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = itemWidth),
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
fun AddAmountBottomSheetContent(
    categories: List<Category>,
    initialCategory: Category,
    onDismissRequest: () -> Unit,
    onConfirm: (Category, Double) -> Unit
) {
    var selectedCategory by remember { mutableStateOf(initialCategory) }
    var amountText by remember { mutableStateOf("") }
    var amountError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {
        Text(
            text = "Добавить запись",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

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
    val iconSize = 64.dp
    val itemWidth = 80.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .width(itemWidth)
    ) {
        Text(
            text = category.name,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(bottom = 4.dp)
                .heightIn(max = 40.dp)
        )
        Card(
            modifier = Modifier
                .size(iconSize)
                .clickable(onClick = onClick),
            elevation = CardDefaults.cardElevation(4.dp),
            shape = CircleShape
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = category.iconType.icon,
                    contentDescription = category.name,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}



@Composable
fun IconSelectionGrid(
    icons: List<CategoryIconType>,
    selectedIcon: CategoryIconType,
    onIconSelected: (CategoryIconType) -> Unit
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
                        val iconType = icons[index]
                        IconButton(
                            onClick = { onIconSelected(iconType) }
                        ) {
                            Icon(
                                imageVector = iconType.icon,
                                contentDescription = null,
                                tint = if (iconType == selectedIcon) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
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
fun TimePeriodChips(
    selectedPeriod: String,
    onPeriodSelected: (String) -> Unit
) {
    val periods = listOf("День", "Неделя", "Месяц", "Год", "За всё время", "Заданный период")
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(8.dp),
    ) {
        periods.forEach { period ->
            val isSelected = selectedPeriod == period
            AssistChip(
                onClick = { onPeriodSelected(period) },
                label = { Text(period) },
                modifier = Modifier.padding(end = 4.dp),
                colors = if (isSelected) {
                    AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        labelColor = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    AssistChipDefaults.assistChipColors()
                }
            )
        }
    }
}


@Composable
fun BudgetCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(MaterialTheme.shapes.large),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Бюджет:",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.weight(0.7f)
                )
                Text(
                    text = "[значение заглушка]",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Расходы:",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.weight(0.7f)
                )
                Text(
                    text = "[значение заглушка]",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Доходы:",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.weight(0.7f)
                )
                Text(
                    text = "[значение заглушка]",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}


@Composable
fun AddCategoryBottomSheetContent(
    onDismissRequest: () -> Unit,
    onConfirm: (Category) -> Unit
) {
    var categoryName by remember { mutableStateOf("") }
    var selectedIconType by remember { mutableStateOf(CategoryIconType.Home) }

    val iconOptions = CategoryIconType.values().toList()

    Column(
        modifier = Modifier
            .padding(16.dp)
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
            selectedIcon = selectedIconType,
            onIconSelected = { selectedIconType = it }
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
                    val newCategory = Category(categoryName, selectedIconType)
                    onConfirm(newCategory)
                },
                enabled = categoryName.isNotBlank()
            ) {
                Text("Добавить")
            }
        }
    }
}