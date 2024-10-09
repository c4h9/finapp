package com.example.finance.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.finance.presentation.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = MainViewModel(application)
        setContent {
            MainContent(viewModel)
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
