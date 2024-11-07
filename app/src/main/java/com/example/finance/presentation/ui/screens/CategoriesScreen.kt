package com.example.finance.presentation.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import kotlinx.coroutines.launch


val sampleCategories = listOf(
    Category("Food", CategoryIconType.Home, false),
    Category("Salary", CategoryIconType.Salary, true),
    Category("Transport", CategoryIconType.Pets, false)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    categories: List<Category>,
    onConfirmAddAmountBottomSheetContent: (Category, Double) -> Unit,
    addCategory: (Category, Boolean) -> Unit,
    budget: Double,
    outcomes: Double,
    incomes: Double,
    categorySums: Map<String, Double>
) {
    var openAddCategoryBottomSheet by remember { mutableStateOf(false) }
    var openAddAmountBottomSheet by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var showInCome by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val amountBottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    Column(modifier = Modifier.fillMaxSize()) {
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
            },
            showInCome,
            toggleShowInCome = { showInCome = !showInCome },
            budget = budget,
            outcomes = outcomes,
            incomes = incomes,
            categorySums = categorySums
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
                        onConfirmAddAmountBottomSheetContent(category, amount)
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
                    addCategory(newCategory, showInCome)
                    coroutineScope.launch { bottomSheetState.hide() }
                    openAddCategoryBottomSheet = false
                },
                showInCome
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoriesGrid(
    categories: List<Category>,
    onAddCategoryClick: () -> Unit,
    onCategoryClick: (Category) -> Unit,
    showIncome: Boolean,
    toggleShowInCome: () -> Unit,
    budget: Double,
    outcomes: Double,
    incomes: Double,
    categorySums: Map<String, Double>
) {
    var selectedPeriod by remember { mutableStateOf("Месяц") }
    val incomeItems: List<Category> = categories.filter { it.isIncome } + Category("Добавить", CategoryIconType.Add, true)
    val outcomeItems: List<Category> = categories.filter { !it.isIncome } + Category("Добавить", CategoryIconType.Add, false)

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            TimePeriodChips(
                selectedPeriod = selectedPeriod,
                onPeriodSelected = { period ->
                    selectedPeriod = period
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            BudgetCard(
                modifier = Modifier.fillMaxWidth(),
                onClickBudgetCard = { toggleShowInCome() },
                budget = budget,
                outcomes = outcomes,
                incomes = incomes
            )
        }
        item {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                val items = if (showIncome) incomeItems else outcomeItems
                items.forEach { category ->
                    CategoryItem(
                        category = category,
                        onClick = {
                            if (category.name == "Добавить") {
                                onAddCategoryClick()
                            } else {
                                onCategoryClick(category)
                            }
                        },
                        amount = if (category.name == "Добавить") null else categorySums[category.name] ?: 0.0
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryItem(
    category: Category,
    onClick: () -> Unit,
    amount: Double? = 0.0
) {
    val iconSize = 60.dp
    val itemWidth = 75.dp

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

        if (amount != null) {
            Text(
                text = roundUpToTwoDecimalPlaces(amount).toString() + " ₽",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (amount != 0.0) FontWeight.Bold else FontWeight.Normal
                ),
                color = if (amount == 0.0) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.tertiary,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .heightIn(max = 40.dp)
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
    onPeriodSelected: (String) -> Unit,
    modifier: Modifier
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
fun BudgetCard(modifier: Modifier, onClickBudgetCard: () -> Unit, budget: Double, outcomes: Double, incomes: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(MaterialTheme.shapes.large),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        elevation = CardDefaults.cardElevation(10.dp),
        onClick = { onClickBudgetCard() }
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
                    text = roundUpToTwoDecimalPlaces(budget).toString(),
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
                    text = roundUpToTwoDecimalPlaces(outcomes).toString(),
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
                    text = "Доходы: ",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.weight(0.7f)
                )
                Text(
                    text = incomes.toString(),
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
    onConfirm: (Category) -> Unit,
    isIncome: Boolean
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
                    val newCategory = Category(categoryName, selectedIconType, isIncome)
                    onConfirm(newCategory)
                },
                enabled = categoryName.isNotBlank()
            ) {
                Text("Добавить")
            }
        }
    }
}

fun roundUpToTwoDecimalPlaces(value: Double): Double {
    return kotlin.math.ceil(value * 100) / 100
}

