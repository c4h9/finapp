// NavigationGraph.kt
package com.example.finance.presentation.ui

import android.content.Intent
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.finance.Screen
import com.example.finance.presentation.ui.screens.CategoriesScreen
import com.example.finance.presentation.ui.screens.FirstLaunchScreen
import com.example.finance.presentation.ui.screens.OperationsScreen
import com.example.finance.presentation.ui.screens.PermissionScreen
import com.example.finance.presentation.ui.screens.SettingsScreen
import com.example.finance.presentation.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun NavigationGraph(
    navController: NavHostController,
    viewModel: MainViewModel,
    notificationTextValue: String,
    onGetNotificationAccess: () -> Unit
) {
    // CategoriesScreen
    val categories by viewModel.categories.collectAsState()
    val budget by viewModel.budget.collectAsState()
    val incomes by viewModel.incomesForCurrentMonth.collectAsState()
    val outcomes by viewModel.outcomesForCurrentMonth.collectAsState()

    // PermissionScreen
    val allCategories = viewModel.allCategories
    val selectedCategories = viewModel.selectedCategories
    val navigateToNextScreen by viewModel.navigateToNextScreen.collectAsState()

    //OperationsScreen
    val operationsValue = viewModel.operations.collectAsState().value
    val categoriesValue = viewModel.categories.collectAsState().value

    val isFirstLaunch by viewModel.isFirstLaunch.collectAsState()

    val startDestination = if (isFirstLaunch == true) {
        Screen.FirstLaunch.route
    } else {
        Screen.Categories.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.FirstLaunch.route) {
            FirstLaunchScreen(
                navController = navController,
                toTheNextScreen = {
                    viewModel.onNavigatedToNextScreen()
                },
                onClickFilterChip = { category, isSelected ->
                    viewModel.onCategoryCheckedChanged(category, isSelected)
                },
                onContinueClicked = { viewModel.onContinueClicked() },
                allCategories,
                selectedCategories,
                navigateToNextScreen,
                onUpdateBudget = { value -> viewModel.setBudget(value) },
                budget
            )
        }
        composable(Screen.Categories.route) {
            CategoriesScreen(
                categories = categories,
                addCategory = { newCategory, isIncome -> viewModel.addCategory(newCategory, isIncome) },
                onConfirmAddAmountBottomSheetContent = { category, amount -> viewModel.addOperation(category, amount, "") },
                onDeleteCategory = { categoryName -> viewModel.deleteCategory(categoryName) },
                budget = budget,
                outcomes = outcomes,
                incomes = incomes,
                onPresetRangeClick = { period -> viewModel.setSelectedPeriod(period) },
                categorySums = viewModel.categorySums.collectAsState().value,
                toProfileScreen = { navController.navigate("settings") },
                doesCategoryExist = { categoryName, callback ->
                    viewModel.doesCategoryExist(categoryName, callback)
                }
            )
        }
        composable(Screen.Operations.route) {
            OperationsScreen(
                categoriesValue,
                operations = viewModel.operations.collectAsState().value,
                onDeleteOperations = { operationIds ->
                    viewModel.viewModelScope.launch {
                        viewModel.deleteOperationsByIds(operationIds)
                    }
                },
                onEditConfirm = { updatedOperation ->
                    viewModel.updateOperation(updatedOperation)
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                notificationTextValue = notificationTextValue,
                onGetNotificationAccess = onGetNotificationAccess,
                viewModel = viewModel
            )
        }
        composable(Screen.Permission.route) {
            PermissionScreen(
                onDontShowAgainChanged = { dontShowAgain ->
                    viewModel.viewModelScope.launch {
                        viewModel.setDontShowPermissionScreen(dontShowAgain)
                    }
                },
                onRequestNotificationAccess = {
                    val context = navController.context
                    val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                },
                onPermissionGranted = {
                    navController.navigate("categories") {
                        popUpTo("permission") { inclusive = true }
                    }
                }
            )
        }
    }
}
