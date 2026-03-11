package com.risyan.quickshutdownphone.screen_content_guard.receivers

import android.app.admin.DeviceAdminReceiver
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.risyan.quickshutdownphone.MyApp
import com.risyan.quickshutdownphone.base.data.SharedPrefApi
import com.risyan.quickshutdownphone.base.reLockAndNotifyOrRemoveIfExpired
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MyAdmin : DeviceAdminReceiver()

class UserUnlockReceiver(
    val myApp: MyApp = MyApp.getInstance(),
    val sharedPrefApi: SharedPrefApi = myApp.sharedPrefApi,
): BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_USER_PRESENT == intent.action) {
            job?.cancel()
            job = CoroutineScope(Dispatchers.Main).launch {
                sharedPrefApi.setPhoneActiveStatus(true)
                context.reLockAndNotifyOrRemoveIfExpired(sharedPrefApi)
            }
            return
        }


        if(Intent.ACTION_SCREEN_OFF == intent.action) {
            sharedPrefApi.setPhoneActiveStatus(false)
            return;
        }

        if(Intent.ACTION_SCREEN_ON == intent.action) {
            sharedPrefApi.setPhoneActiveStatus(true)
            return;
        }
    }

    companion object {
        var job: Job? = null
    }
}