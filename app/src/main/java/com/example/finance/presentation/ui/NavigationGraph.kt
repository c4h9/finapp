package com.example.finance.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

@Composable
fun NavigationGraph(
    navController: NavHostController,
    viewModel: MainViewModel,
    notificationTextValue: String,
    onGetNotificationAccess: () -> Unit
) {
    //CategoriesScreen
    val categories by viewModel.categories.collectAsState()
    val budget by viewModel.budget.collectAsState()
    val incomes by viewModel.incomesForCurrentMonth.collectAsState()
    val outcomes by viewModel.outcomesForCurrentMonth.collectAsState()

    //PermissionScreen
    val allCategories = viewModel.allCategories
    val selectedCategories = viewModel.selectedCategories
    val navigateToNextScreen by viewModel.navigateToNextScreen.collectAsState()

    val isFirstLaunch by viewModel.isFirstLaunch.collectAsState()

    val startDestination = if (isFirstLaunch == true) {
        Screen.FirstLaunch.route
    } else {
        Screen.Categories.route
    }

    //FirstLaunchScreen

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
                onUpdateBudget = { value -> viewModel.setBudget(value)},
                budget
            )
        }
        composable(Screen.Categories.route) {
            CategoriesScreen(
                categories = categories,
                addCategory = { newCategory, isIncome -> viewModel.addCategory(newCategory, isIncome) },
                onConfirmAddAmountBottomSheetContent = { category, amount -> viewModel.addOperation(category, amount) },
                budget = budget,
                outcomes = outcomes,
                incomes = incomes
//                selectedPeriod = viewModel.selectedPeriod.collectAsState().value,
//                onPeriodSelected = { period -> viewModel.setSelectedPeriod(period) }
            )
        }
        composable(Screen.Operations.route) {
            OperationsScreen(
                operations = viewModel.operations.collectAsState().value,
                categories = viewModel.categories.collectAsState().value,
                viewModel = viewModel
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
                navController = navController,
                viewModel = viewModel,
                onPermissionGranted = {
                    onGetNotificationAccess()
                }
            )
        }
    }
}

