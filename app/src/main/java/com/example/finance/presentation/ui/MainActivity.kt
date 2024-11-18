package com.example.finance.presentation.ui

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.finance.data.service.NotificationService
import com.example.finance.presentation.viewmodel.MainViewModel
import com.example.finance.ui.theme.FinanceAppTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var nfcAdapter: NfcAdapter
    private lateinit var pendingIntent: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {
            setKeepOnScreenCondition { !viewModel.isDataLoaded.value }
        }
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Инициализация NFC адаптера
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {   // Устройство не поддерживает NFC
            return
        }

        // Настройка PendingIntent для обработки событий NFC
        pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE // FLAG_MUTABLE для API 31+
        )

        setContent {
            FinanceAppTheme {
                MainContent(viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::nfcAdapter.isInitialized) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null)
        }
    }

    override fun onPause() {
        super.onPause()
        if (::nfcAdapter.isInitialized) {
            nfcAdapter.disableForegroundDispatch(this)
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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (NfcAdapter.ACTION_TAG_DISCOVERED == intent.action) {
            if (!NotificationService.isServiceRunning) {
                val serviceIntent = Intent(this, NotificationService::class.java)
                startService(serviceIntent)
            } else {
                Log.i("MainActivity", "Service is already running")
            }
        }
    }
}
