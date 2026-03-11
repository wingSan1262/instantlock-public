package com.risyan.quickshutdownphone.screen_content_guard.service_utils

import android.graphics.Bitmap
import android.util.Log
import com.risyan.quickshutdownphone.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AINfswTriggerTracker {

    var job : Job? = null

    fun logIssueIfDebugBuild(logContent: String, bitmap: Bitmap) {

        if(!BuildConfig.ENABLE_LOGGING){
            return
        }

        if (job != null) return

        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val fileName = getFileName()

                // Local logging only (Firebase removed for offline use)
                Log.d("NSFW Trigger", "AI NSFW Triggered: $logContent")
                Log.d("NSFW Trigger", "Screenshot: $fileName")

                // Optional: Save screenshot locally if needed
                // saveScreenshotLocally(fileName, bitmap)

            } catch (e: Exception) {
                Log.e("NSFW Trigger", "Error logging NSFW trigger", e)
            }

            delay(30000)
            job?.cancel()
            job = null
        }
    }

    private fun getFileName(): String {
        val minuteBucket = System.currentTimeMillis() / 60000  // round to the minute
        return "nsfw_screenshot_$minuteBucket.png"
    }

    // Uncomment if you want to save screenshots locally
    /*
    private fun saveScreenshotLocally(fileName: String, bitmap: Bitmap) {
        try {
            val appContext = com.risyan.quickshutdownphone.MyApp.getInstance()
            val file = File(appContext.cacheDir, fileName)
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            Log.d("NSFW Trigger", "Screenshot saved locally: ${file.absolutePath}")
        } catch (e: Exception) {
            Log.e("NSFW Trigger", "Error saving screenshot", e)
        }
    }
    */

}