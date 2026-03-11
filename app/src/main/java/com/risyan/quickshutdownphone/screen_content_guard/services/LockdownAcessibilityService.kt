package com.risyan.quickshutdownphone.screen_content_guard.services

import android.accessibilityservice.AccessibilityService
import android.graphics.Bitmap
import android.os.Build
import android.view.accessibility.AccessibilityEvent
import com.risyan.quickshutdownphone.MyApp
import com.risyan.quickshutdownphone.R
import com.risyan.quickshutdownphone.base.checkJobAndSaveLockStatus
import com.risyan.quickshutdownphone.base.isAnyActiveLock
import com.risyan.quickshutdownphone.base.hasStorageAccessNeededInstantLock
import com.risyan.quickshutdownphone.base.reLockAndNotifyOrRemoveIfExpired
import com.risyan.quickshutdownphone.screen_content_guard.model.ShutdownType
import com.risyan.quickshutdownphone.screen_content_guard.service_utils.AiNsfwGraderImp
import com.risyan.quickshutdownphone.screen_content_guard.service_utils.NonScreenShotImageGraderImp
import com.risyan.quickshutdownphone.screen_content_guard.service_utils.ScreenShotServiceImp
import com.risyan.quickshutdownphone.base.showSystemAnnouncement
import com.risyan.quickshutdownphone.base.startUnlockReceiverByStartForeground
import com.risyan.quickshutdownphone.screen_content_guard.extensions.gradeFuzzyOccurrence
import com.risyan.quickshutdownphone.screen_content_guard.receivers.UserUnlockReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LockdownAcessibilityService : AccessibilityService() {

    val scope = CoroutineScope(Dispatchers.IO)
    val aiNsfwGrader = AiNsfwGraderImp(this, scope)
    val blankImageChecker = NonScreenShotImageGraderImp(scope)
    val screenShotService = ScreenShotServiceImp(this, scope)
    val sharedPrefApi = MyApp.getInstance().sharedPrefApi
    val userSetting = MyApp.getInstance().userSetting

    var periodicScreenShotJob: Job? = null

    val SCREENSHOT_INTERVAL = 5000L

    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {}

    override fun onInterrupt() {}

    var unlockReceiver: UserUnlockReceiver? = null
    override fun onServiceConnected() {
        super.onServiceConnected()
        initPeriodicScreenShot()
        unlockReceiver = startUnlockReceiverByStartForeground()
    }

    fun initPeriodicScreenShot() {
        periodicScreenShotJob?.cancel();
        periodicScreenShotJob = scope.launch {
            delay(250L)
            if (!hasStorageAccessNeededInstantLock() && Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                delay(SCREENSHOT_INTERVAL * 20)
                this@LockdownAcessibilityService.showSystemAnnouncement(
                    getString(R.string.please_grant_storage_permission),
                    getString(R.string.you_don_t_have_storage_permission),
                    "OK"
                )
                initPeriodicScreenShot()
                return@launch
            }
            delay(SCREENSHOT_INTERVAL)
            doScreenShot()
            if(periodicScreenShotJob?.isCancelled == true){
                return@launch
            }
            initPeriodicScreenShot()
        }
    }


    override fun onDestroy() {
        unregisterReceiver(unlockReceiver)
        periodicScreenShotJob?.cancel();
        super.onDestroy()
    }

    fun doNsfwCheck(bitmap: Bitmap, onResult : (safe: Int, nsfw: Int, blank: Int) -> Unit) {
        aiNsfwGrader.runCheckNsfw(bitmap, { isNsfw -> })
        { safe: Int, nsfw: Int, bitmap ->
            scope.launch(Dispatchers.Main) {
                onResult(
                    safe,
                    nsfw,
                    0
                )
            }
        }
    }

    private fun resetCounters() {
        sharedPrefApi.setCurrentSafeCounter(0)
        sharedPrefApi.setCurrentNsfwCounter(0)
        sharedPrefApi.setCurrentBlankImageCounter(0)
    }

    fun doScreenShot() {

        if(sharedPrefApi.isAnyActiveLock()){
            scope.launch(Dispatchers.Main){
                this@LockdownAcessibilityService
                    .reLockAndNotifyOrRemoveIfExpired(sharedPrefApi)
            }
            return
        }

        screenShotService.takeScreenShot { _, aiOptCropedBm ->
            doNsfwCheck(aiOptCropedBm){ safe, nsfw, blank ->
                if (gradeFuzzyOccurrence(
                    safe, nsfw, blank, ::resetCounters
                )){
                    checkJobAndSaveLockStatus(ShutdownType.QUICK_3_MINUTES_NFSW, sharedPrefApi)
                }
            }
        }
    }

}



