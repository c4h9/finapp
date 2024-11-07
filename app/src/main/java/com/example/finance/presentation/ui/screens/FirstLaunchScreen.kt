package com.example.finance.presentation.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.finance.domain.entity.Category


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FirstLaunchScreen(
    navController: NavController,
    toTheNextScreen: () -> Unit,
    onClickFilterChip: (Category, Boolean) -> Unit,
    onContinueClicked: () -> Unit,
    allCategories: List<Category>,
    selectedCategories: List<Category>,
    navigateToNextScreen: Boolean,
    onUpdateBudget: (Double) -> Unit,
    budget: Double
    ) {
    if (navigateToNextScreen) {
        LaunchedEffect(Unit) {
            navController.navigate("permission") {
                popUpTo("first_launch") { inclusive = true }
            }
            toTheNextScreen()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Text(
            text = "Выберите категории расходов:",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))
        FlowRow(modifier = Modifier.fillMaxWidth()) {
            allCategories.filter { !it.isIncome }.forEach { category ->
                val isSelected = selectedCategories.contains(category)
                FilterChip(
                    selected = isSelected,
                    onClick = { onClickFilterChip(category, !isSelected) },
                    label = { Text(text = category.name) },
                    shape = CircleShape,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Выберите категории доходов:",
            style = MaterialTheme.typography.titleMedium
        )

        FlowRow(modifier = Modifier.fillMaxWidth()) {
            allCategories.filter { it.isIncome }.forEach { category ->
                val isSelected = selectedCategories.contains(category)
                FilterChip(
                    selected = isSelected,
                    onClick = { onClickFilterChip(category, !isSelected) },
                    label = { Text(text = category.name) },
                    shape = CircleShape,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }

        Text("Введите месячный бюджет:", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = budget.toString(),
            onValueChange = { newText ->
                val regex = """^\d+(\.\d{0,2})?$""".toRegex()
                if (regex.matches(newText.replace(",", "."))) {
                    val newBudget = newText.replace(",", ".").toDoubleOrNull()
                    if (newBudget != null) {
                        onUpdateBudget(newBudget)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            label = { Text("Бюджет") }
        )

        Button(
            onClick = {
                onContinueClicked()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Далее")
        }
    }
}
