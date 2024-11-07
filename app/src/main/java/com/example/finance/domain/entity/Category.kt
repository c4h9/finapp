package com.example.finance.domain.entity

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector

data class Category(
    val name: String,
    val iconType: CategoryIconType,
    val isIncome: Boolean
)

enum class CategoryIconType(val icon: ImageVector) {
    Home(Icons.Default.Home),
    ShoppingCart(Icons.Default.ShoppingCart),
    Fastfood(Icons.Default.Fastfood),
    Work(Icons.Default.Work),
    DirectionsCar(Icons.Default.DirectionsCar),
    FitnessCenter(Icons.Default.FitnessCenter),
    Movie(Icons.Default.Movie),
    School(Icons.Default.School),
    Pets(Icons.Default.Pets),
    TravelExplore(Icons.Default.TravelExplore),
    Add(Icons.Default.Add),
    Salary(Icons.Default.Add),
    Underworking(Icons.Default.Add),
    Deposit(Icons.Default.Add),
    Scholarship(Icons.Default.Add),
    Help(Icons.AutoMirrored.Filled.Help),
}

fun getImageVectorByName(iconName: String): ImageVector {
    return when (iconName) {
        "Home" -> Icons.Default.Home
        "ShoppingCart" -> Icons.Default.ShoppingCart
        "Fastfood" -> Icons.Default.Fastfood
        "Work" -> Icons.Default.Work
        "DirectionsCar" -> Icons.Default.DirectionsCar
        "FitnessCenter" -> Icons.Default.FitnessCenter
        "Movie" -> Icons.Default.Movie
        "School" -> Icons.Default.School
        "Pets" -> Icons.Default.Pets
        "TravelExplore" -> Icons.Default.TravelExplore
        "Add" -> Icons.Default.Add
        "Salary" -> Icons.Default.AccountBalanceWallet
        "Underworking" -> Icons.Default.AttachMoney
        "Deposit" -> Icons.Default.AccountBalance
        "Scholarship" -> Icons.Default.School
        else -> Icons.Default.Help
    }
}

