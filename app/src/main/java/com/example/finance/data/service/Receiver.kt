package com.example.finance.data.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter

class NfcReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == NfcAdapter.ACTION_TAG_DISCOVERED) {
            val serviceIntent = Intent(context, NotificationService::class.java)
            context.startService(serviceIntent)
        }
    }
}
