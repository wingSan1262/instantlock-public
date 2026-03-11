package com.risyan.quickshutdownphone.feature.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.risyan.quickshutdownphone.base.startSingleAllBroadcastStarters

class PhonePowerOnReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            context.startSingleAllBroadcastStarters()
        }
    }
}