package com.example.finance.presentation.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.finance.presentation.viewmodel.MainViewModel

@Composable
fun FirstLaunchScreen(navController: NavController, viewModel: MainViewModel) {
    val allCategories = viewModel.allCategories
    val selectedCategories = viewModel.selectedCategories

    val navigateToNextScreen by viewModel.navigateToNextScreen.collectAsState()

    if (navigateToNextScreen) {
        LaunchedEffect(Unit) {
            navController.navigate("permission") {
                popUpTo("first_launch") { inclusive = true }
            }
            viewModel.onNavigatedToNextScreen()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Выберите категории для начала:",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(allCategories) { category ->
                Row(
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Checkbox(
                        checked = selectedCategories.contains(category),
                        onCheckedChange = { isChecked ->
                            viewModel.onCategoryCheckedChanged(category, isChecked)
                        }
                    )
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                viewModel.onContinueClicked()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Далее")
        }
    }
}
