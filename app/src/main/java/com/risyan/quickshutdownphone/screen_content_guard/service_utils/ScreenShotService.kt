package com.risyan.quickshutdownphone.screen_content_guard.service_utils

import android.accessibilityservice.AccessibilityService
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Display.DEFAULT_DISPLAY
import kotlinx.coroutines.CoroutineScope


interface ScreenShotService {
    fun takeScreenShot(
        onImage: (full: Bitmap, aiOpt: Bitmap) -> Unit
    )
}

class ScreenShotServiceImp(
    val owner: AccessibilityService,
    val ownerScope: CoroutineScope,
    val nsfwImageCropper : NSFWSexyImagePreProcessing = NSFWSexyImagePreProcessing(),
) : ScreenShotService {
    override fun takeScreenShot(onImage: (full: Bitmap, aiOpt: Bitmap) -> Unit) {
        // minSdk is now 30 (Android R), so takeScreenshot() is always available
        takeScreenshotAndroidR(onImage)
    }

    fun takeScreenshotAndroidR(
        onImage: (full: Bitmap, aiOpt: Bitmap) -> Unit
    ) {
        owner.takeScreenshot(
            DEFAULT_DISPLAY,
            { r -> Thread(r).start() },
            object : AccessibilityService.TakeScreenshotCallback {
                override fun onSuccess(screenshot: AccessibilityService.ScreenshotResult) {
                    val bitmap = Bitmap.wrapHardwareBuffer(
                        screenshot.hardwareBuffer,
                        screenshot.colorSpace
                    )?.copy(Bitmap.Config.ARGB_8888, false)
                    screenshot.hardwareBuffer.close()

                    if (bitmap == null) {

                    } else {
                        Handler(Looper.getMainLooper()).post {
                            onImage(bitmap, bitmap.cropWithoutBars());
                        }
                    }
                }

                override fun onFailure(errorCode: Int) {
                    Log.e(
                        "Take screenshot: ",
                        "takeScreenshot() -> onFailure($errorCode), falling back to GLOBAL_ACTION_TAKE_SCREENSHOT"
                    )
                }
            })
    }
}