package com.coloplast.olympus.bluetooth.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler

class ResetReceiver: BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    if (intent.action == BluetoothConnectionService.INTENT_FILTER_RESET_SERVICE) {
      context.sendBroadcast(Intent(BluetoothConnectionService.INTENT_FILTER_STOP_SERVICE))
      Handler().postDelayed({ context.startService(Intent(context, BluetoothConnectionService::class.java)) }, 2000)
    }
  }
}