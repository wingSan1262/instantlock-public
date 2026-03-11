package com.risyan.quickshutdownphone.screen_content_guard.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.risyan.quickshutdownphone.MyApp
import com.risyan.quickshutdownphone.base.data.SharedPrefApi
import com.risyan.quickshutdownphone.screen_content_guard.model.ShutdownType
import com.risyan.quickshutdownphone.base.startLockingAndNotify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NightTimeReceiver(
    val myApp: MyApp = MyApp.getInstance(),
    val sharedPrefApi: SharedPrefApi = myApp.sharedPrefApi,
): BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        sharedPrefApi.removeLockStatus()
        CoroutineScope(Dispatchers.Main).launch {
            context.startLockingAndNotify(
                sharedPrefApi, ShutdownType.NIGHT_4HOUR_TIME
            )
        }
    }
}