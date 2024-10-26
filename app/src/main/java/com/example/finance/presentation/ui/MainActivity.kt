package com.example.finance.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.finance.presentation.viewmodel.MainViewModel
import com.example.finance.ui.theme.FinanceAppTheme

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        viewModel = MainViewModel(application)
        setContent {
            FinanceAppTheme {
                MainContent(viewModel)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.bindNotificationService()
    }

    override fun onStop() {
        super.onStop()
        viewModel.unbindNotificationService()
    }
}
