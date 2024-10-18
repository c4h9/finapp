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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.finance.domain.entity.Category
import com.example.finance.domain.entity.CategoryIconType
import com.example.finance.presentation.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun FirstLaunchScreen(navController: NavController, viewModel: MainViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val allCategories = listOf(
        Category("Еда", CategoryIconType.Fastfood),
        Category("Транспорт", CategoryIconType.DirectionsCar),
        Category("Дом", CategoryIconType.Home),
        Category("Работа", CategoryIconType.Work),
        Category("Спорт", CategoryIconType.FitnessCenter),
        Category("Покупки", CategoryIconType.ShoppingCart),
        Category("Развлечения", CategoryIconType.Movie)
    )

    // State to hold selected categories
    val selectedCategories = remember { mutableStateListOf<Category>() }

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
                            if (isChecked) {
                                selectedCategories.add(category)
                            } else {
                                selectedCategories.remove(category)
                            }
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
                coroutineScope.launch {
                    viewModel.setInitialCategories(selectedCategories)
                    viewModel.setFirstLaunch(false)
                    navController.navigate("permission") {
                        popUpTo("first_launch") { inclusive = true }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Далее")
        }
    }
}
