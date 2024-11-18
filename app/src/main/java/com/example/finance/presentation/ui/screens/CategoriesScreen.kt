package com.example.finance.presentation.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.finance.domain.entity.Category
import com.example.finance.domain.entity.CategoryIconType
import kotlinx.coroutines.launch

@Preview(showBackground = true)
@Composable
fun OverlayWithButtonsPreview() {
    OverlayWithButtons(onDismiss = {}, onPresetRangeClick = {}, onShowDataPicker = {})
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun CategoriesScreenPreview() {
    CategoriesScreen(
        categories = listOf(
            Category("Food", CategoryIconType.Home, false),
            Category("Salary", CategoryIconType.Salary, true),
            Category("Transport", CategoryIconType.Pets, false)
        ),
        onConfirmAddAmountBottomSheetContent = { _, _ -> },
        onDeleteCategory = { },
        addCategory = { _, _ -> },
        budget = 1000.0,
        outcomes = 500.0,
        incomes = 1500.0,
        categorySums = mapOf(
            "Food" to 200.0,
            "Transport" to 100.0,
            "Salary" to 1500.0
        ),
        toProfileScreen = {},
        doesCategoryExist = {_, _ ->},
        onPresetRangeClick = {}
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    categories: List<Category>,
    onConfirmAddAmountBottomSheetContent: (Category, Double) -> Unit,
    onDeleteCategory: (String) -> Unit,
    addCategory: (Category, Boolean) -> Unit,
    budget: Double,
    outcomes: Double,
    incomes: Double,
    onPresetRangeClick: (String) -> Unit,
    categorySums: Map<String, Double>,
    toProfileScreen: () -> Unit,
    doesCategoryExist: (String, (Boolean) -> Unit) -> Unit
) {
    var openAddCategoryBottomSheet by remember { mutableStateOf(false) }
    var openAddAmountBottomSheet by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedPeriod by remember { mutableStateOf("Месяц") }
    var showInCome by remember { mutableStateOf(false) }
    var showOverlay by remember { mutableStateOf(false) }
    var showDataPicker by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val amountBottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val snackState = remember { SnackbarHostState() }
    SnackbarHost(hostState = snackState, Modifier.zIndex(1f))
    val state = rememberDateRangePickerState()


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
            onDeleteCategory = onDeleteCategory,
            showInCome,
            toggleShowInCome = { showInCome = !showInCome },
            budget = budget,
            outcomes = outcomes,
            incomes = incomes,
            categorySums = categorySums,
            onShowOverlay = { showOverlay = true },
            toProfileScreen = toProfileScreen,
            selectedPeriod = selectedPeriod
        )

        if (openAddAmountBottomSheet && selectedCategory != null) {
            ModalBottomSheet(
                onDismissRequest = {
                    coroutineScope.launch { amountBottomSheetState.hide() }
                    openAddAmountBottomSheet = false
                },
                sheetState = amountBottomSheetState,
                dragHandle = null,
                modifier = Modifier.imePadding()
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
                isIncome = showInCome,
                doesCategoryExist = { categoryName, callback ->
                    doesCategoryExist(categoryName) { exists ->
                        callback(exists)
                    }
                }
            )
        }
    }
    if (showOverlay) {
        OverlayWithButtons(
            onDismiss = { showOverlay = false },
            onPresetRangeClick = { label ->
                selectedPeriod = label
                onPresetRangeClick(label)
            },
            onShowDataPicker = {
                showDataPicker = true
                selectedPeriod = "За период"
            }
        )
    }
    if (showDataPicker) {
        DateRangePickerWithButtons(
            state = state,
            onSaveClick = {
                onPresetRangeClick("${state.selectedStartDateMillis!!}+${state.selectedEndDateMillis!!}")
                showOverlay = false
                showDataPicker = false
            },
            onDismissClick = {
                showDataPicker = false
            }
        )
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoriesGrid(
    categories: List<Category>,
    onAddCategoryClick: () -> Unit,
    onCategoryClick: (Category) -> Unit,
    onDeleteCategory: (String) -> Unit,
    showIncome: Boolean,
    toggleShowInCome: () -> Unit,
    budget: Double,
    outcomes: Double,
    incomes: Double,
    categorySums: Map<String, Double>,
    onShowOverlay: () -> Unit,
    toProfileScreen: () -> Unit,
    selectedPeriod: String
) {
    val incomeItems: List<Category> = categories.filter { it.isIncome } + Category("Добавить", CategoryIconType.Add, true)
    val outcomeItems: List<Category> = categories.filter { !it.isIncome } + Category("Добавить", CategoryIconType.Add, false)

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { toProfileScreen() },
                    modifier = Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Button(
                    onClick = { onShowOverlay() },
                    modifier = Modifier.height(48.dp)
                ) { Text(selectedPeriod) }
                IconButton(
                    onClick = { /* Handle right button click */ },
                    modifier = Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit category grid",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
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
                        onLongClick = {if (category.name != "Добавить") {
                            onDeleteCategory(category.name)
                        }},
                        amount = if (category.name == "Добавить") null else categorySums[category.name] ?: 0.0
                    )
                }
            }
        }
    }
}

@Composable
fun OverlayWithButtons(onDismiss: () -> Unit, onPresetRangeClick: (String) -> Unit, onShowDataPicker: (String) -> Unit) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier
                    .height(380.dp)
                    .width(250.dp)
                    .pointerInput(Unit) {
                        detectTapGestures {} // Пустой обработчик, чтобы Card не реагировал на клики
                    }
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Column(verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxHeight()) {
                        IconButtonWithLabel(
                            icon = Icons.Default.Today,
                            label = "День",
                            onClick = onPresetRangeClick,
                            onDismiss = onDismiss
                        )
                        IconButtonWithLabel(
                            icon = Icons.Default.CalendarMonth,
                            label = "Месяц",
                            onClick = onPresetRangeClick,
                            onDismiss = onDismiss
                        )
                        IconButtonWithLabel(
                            icon = Icons.Default.AllInclusive,
                            label = "За всё время",
                            onClick = onPresetRangeClick,
                            onDismiss = onDismiss
                        )

                    }
                    Column(verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxHeight()) {
                        IconButtonWithLabel(
                            icon = Icons.Default.DateRange,
                            label = "Неделя",
                            onClick = onPresetRangeClick,
                            onDismiss = onDismiss
                        )
                        IconButtonWithLabel(
                            icon = Icons.Default.CalendarToday,
                            label = "Год",
                            onClick = onPresetRangeClick,
                            onDismiss = onDismiss
                        )
                        IconButtonWithLabel(
                            icon = Icons.Default.EditCalendar,
                            label = "За период",
                            onClick = onShowDataPicker
                        )

                    }
                }

            }
        }
}

@Composable
fun IconButtonWithLabel(icon: ImageVector, label: String, onClick: (String) -> Unit, onDismiss: (() -> Unit)? = null) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Card(
            shape = CircleShape,
            modifier = Modifier.padding(bottom = 5.dp)
        ) {
            IconButton(
                onClick = {
                    onClick(label)
                    onDismiss?.invoke()
                },
                modifier = Modifier
                    .size(70.dp)
                    .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Text(
            text = label,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.width(100.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategoryItem(
    category: Category,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    amount: Double? = 0.0
) {
    val iconSize = 60.dp
    val itemWidth = 75.dp
    val showDialog = remember { mutableStateOf(false) }

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
                .combinedClickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(),
                    onClick = {
                        onClick()
                    },
                    onLongClick = {
                        showDialog.value = true
                    }
                ),
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
        if (showDialog.value) {
            ConfirmDelete(
                onConfirm = {
                    onLongClick()
                    showDialog.value = false
                },
                onDismiss = {
                    showDialog.value = false
                }
            )
        }
    }
}

@Composable
fun ConfirmDelete(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Подтверждение")
        },
        text = {
            Text("Удалить категорию и связанные с ней операции?")
        },
        confirmButton = {
            Button(
                onClick = onConfirm
            ) {
                Text("Подтвердить")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("Отмена")
            }
        }
    )
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

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

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
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
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




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    categories: List<Category>,
    selectedCategory: Category,
    onCategorySelected: (Category) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        OutlinedTextField(
            value = selectedCategory.name,
            onValueChange = {},
            label = { Text("Категория") },
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
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
}

@Composable
fun IconSelectionGrid(
    icons: List<CategoryIconType>,
    selectedIcon: CategoryIconType,
    onIconSelected: (CategoryIconType) -> Unit
) {
    LazyHorizontalGrid(
        rows = GridCells.Fixed(5),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        items(icons) { iconType ->
            IconButton(
                onClick = { onIconSelected(iconType) }
            ) {
                Icon(
                    imageVector = iconType.icon,
                    contentDescription = null,
                    tint = if (iconType == selectedIcon) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }
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
    isIncome: Boolean,
    doesCategoryExist: (String, (Boolean) -> Unit) -> Unit
) {
    var categoryName by remember { mutableStateOf("") }
    var selectedIconType by remember { mutableStateOf(CategoryIconType.Home) }
    var isError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val iconOptions = CategoryIconType.entries
    val scrollState = rememberScrollState()

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
            onValueChange = {
                categoryName = it
                isError = false
            },
            label = { Text("Название категории") },
            modifier = Modifier.fillMaxWidth(),
            isError = isError,
            trailingIcon = {
                if (isError) {
                    Icon(Icons.Filled.Error, "error", tint = MaterialTheme.colorScheme.error)
                }
            }
        )

        if (isError) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

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
                    doesCategoryExist(categoryName) { exists ->
                        if (exists) {
                            isError = true
                            errorMessage = "Категория уже существует"
                        } else {
                            val newCategory = Category(categoryName, selectedIconType, isIncome)
                            onConfirm(newCategory)
                        }
                    }
                },
                enabled = categoryName.isNotBlank()
            ) {
                Text("Добавить")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerWithButtons(
    state: DateRangePickerState,
    onSaveClick: () -> Unit,
    onDismissClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Top) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DatePickerDefaults.colors().containerColor)
                .padding(start = 12.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onDismissClick) {
                Icon(Icons.Filled.Close, contentDescription = "Localized description")
            }
            TextButton(
                onClick = onSaveClick,
                enabled = state.selectedEndDateMillis != null
            ) {
                Text(text = "Save")
            }
        }
        DateRangePicker(state = state, modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.Start))
    }
}

fun roundUpToTwoDecimalPlaces(value: Double): Double {
    return kotlin.math.ceil(value * 100) / 100
}

