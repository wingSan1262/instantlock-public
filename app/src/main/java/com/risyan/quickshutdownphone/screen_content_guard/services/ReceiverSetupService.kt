package com.risyan.quickshutdownphone.screen_content_guard.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.risyan.quickshutdownphone.MyApp
import com.risyan.quickshutdownphone.base.data.SharedPrefApi
import com.risyan.quickshutdownphone.base.reLockAndNotifyOrRemoveIfExpired
import com.risyan.quickshutdownphone.screen_content_guard.receivers.UserUnlockReceiver
import com.risyan.quickshutdownphone.base.startUnlockReceiverByStartForeground
import com.risyan.quickshutdownphone.screen_content_guard.service_utils.AccessibilityTemporaryShutoffChecker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReceiverSetupService(
    val myApp: MyApp = MyApp.getInstance(),
    val sharedPrefApi: SharedPrefApi = myApp.sharedPrefApi,
): Service() {

    val accService by lazy {
        AccessibilityTemporaryShutoffChecker(sharedPrefApi, this)
    }

    var userUnlockReceiver: UserUnlockReceiver? = null
    override fun onCreate() {
        super.onCreate()
        userUnlockReceiver = startUnlockReceiverByStartForeground()
        if(sharedPrefApi.getAccessibilityReminder() == null){
            stopSelf()
            return
        }
        CoroutineScope(Dispatchers.Main).launch {
            reLockAndNotifyOrRemoveIfExpired(sharedPrefApi)
        }

        accService.initiateAccessibilityTemporaryShutoffChecker()
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        if(sharedPrefApi.getAccessibilityReminder() == null){
            stopSelf()
            return
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        if(userUnlockReceiver != null){
            unregisterReceiver(userUnlockReceiver) // Unregister the UserUnlockReceiver
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}