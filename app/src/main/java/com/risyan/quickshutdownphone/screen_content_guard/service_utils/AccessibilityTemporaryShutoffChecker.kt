package com.risyan.quickshutdownphone.screen_content_guard.service_utils

import android.app.Dialog
import android.app.Service
import android.content.Context
import com.risyan.quickshutdownphone.R
import com.risyan.quickshutdownphone.base.data.SharedPrefApi
import com.risyan.quickshutdownphone.base.hasAccessibilityService
import com.risyan.quickshutdownphone.base.requestAccessibilityService
import com.risyan.quickshutdownphone.screen_content_guard.widget.CustomBonkDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AccessibilityTemporaryShutoffChecker(
    private val sharedPrefApi: SharedPrefApi,
    private val context : Context
) {

    fun initiateAccessibilityTemporaryShutoffChecker(isStart : Boolean = false) {
        coroutineScope.launch {
            if(isStart){
                sharedPrefApi.saveAccessibilityReminder()
            }
            delay(5000)
            runAccessibilityTemporaryShutoffChecker()
        }
    }

    fun isReminderBlockActive(): Boolean {
        return sharedPrefApi.getAccessibilityReminder()?.isCurrentTimeAfterReminderActiveDeadline()
            ?: false
    }

    var reminderJob : Job? = null
    val coroutineScope = CoroutineScope(Dispatchers.Main)
    fun stopAccessibilityTemporaryShutoffChecker() {
        sharedPrefApi.removeAccessibilityReminder()
        reminderJob?.cancel()
        reminderJob = null
    }

    var dialog : Dialog? = null
    fun runAccessibilityTemporaryShutoffChecker() {
        val delayTime : Long = if (reminderJob == null) 30000 else 5000
        reminderJob?.cancel()
        reminderJob = coroutineScope.launch {
            delay(delayTime)
            val isAccessibilityEnabled = context.hasAccessibilityService()
            if(isAccessibilityEnabled) {
                stopAccessibilityTemporaryShutoffChecker()
                return@launch
            }

            val isReminderBlock = isReminderBlockActive()
            if(!isReminderBlock) {
                delay(10000)
                runAccessibilityTemporaryShutoffChecker()
                return@launch
            }

            delay(5000)

            dialog?.dismiss()
            dialog = CustomBonkDialog(
                context = context,
                title = context.getString(R.string.your_free_reign_has_ended),
                message = context.getString(R.string.sorry_please_enable_the_accessibility_service_to_continue_using_the_app),
                imageResource = R.drawable.lock_meme,
                onCancel = {
                    context.requestAccessibilityService()
                }
            )
            dialog?.show()

            delay(5000)
            runAccessibilityTemporaryShutoffChecker()

            if(context is Service){
                context.stopSelf()
            }
        }
    }
}