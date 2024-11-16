package com.example.finance.domain.entity

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.FaceRetouchingNatural
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.WorkOutline
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
    Salary(Icons.Default.AttachMoney),
    Underworking(Icons.Default.WorkOutline),
    Deposit(Icons.Default.Savings),
    Scholarship(Icons.Default.School),
    Help(Icons.Default.Help),
    Entertainment(Icons.Default.EmojiEmotions),
    Groceries(Icons.Default.LocalGroceryStore),
    Health(Icons.Default.LocalHospital),
    Bills(Icons.Default.Receipt),
    Gifts(Icons.Default.CardGiftcard),
    Rent(Icons.Default.HomeWork),
    Utilities(Icons.Default.Power),
    Insurance(Icons.Default.Security),
    Dining(Icons.Default.Restaurant),
    Clothing(Icons.Default.Checkroom),
    Education(Icons.Default.MenuBook),
    Savings(Icons.Default.AccountBalance),
    Investments(Icons.Default.TrendingUp),
    Charity(Icons.Default.VolunteerActivism),
    Miscellaneous(Icons.Default.MoreHoriz),
    Beauty(Icons.Default.FaceRetouchingNatural),
    Technology(Icons.Default.Computer),
    Transportation(Icons.Default.Train),
    Communication(Icons.Default.Phone),
    Gardening(Icons.Default.Grass),
    Photography(Icons.Default.CameraAlt),
    Music(Icons.Default.MusicNote),
    Books(Icons.Default.Book),
    Art(Icons.Default.Palette),
    Sports(Icons.Default.SportsSoccer),
    Travel(Icons.Default.Flight),
    Cooking(Icons.Default.Kitchen),
    HomeImprovement(Icons.Default.Build),
    PersonalCare(Icons.Default.Spa),
    Social(Icons.Default.People),
    PetsCare(Icons.Default.Pets),
    Fitness(Icons.Default.FitnessCenter),
    Outdoor(Icons.Default.Terrain),
    Finance(Icons.Default.AccountBalanceWallet)
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

