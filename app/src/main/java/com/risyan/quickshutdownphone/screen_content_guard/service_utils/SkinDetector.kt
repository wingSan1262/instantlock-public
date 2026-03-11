package com.risyan.quickshutdownphone.screen_content_guard.service_utils

import android.graphics.Bitmap
import android.graphics.Color
import com.risyan.quickshutdownphone.base.cropCenterSquare

class SkinDetector {

    fun isMostlySkin(bitmap: Bitmap, step: Int = 20, threshold: Float = 0.25f): Boolean {

        val hsv = FloatArray(3)
        var skinCount = 0
        var totalCount = 0

        val width = bitmap.width
        val height = bitmap.height

        for (y in 0 until height step step) {
            for (x in 0 until width step step) {
                val pixel = bitmap.getPixel(x, y)
                Color.colorToHSV(pixel, hsv)

                if (isSkinColor(hsv[0], hsv[1], hsv[2])) {
                    skinCount++
                }

                totalCount++

                // 🚨 Early return: if too much skin detected already
                if (skinCount.toFloat() / totalCount > threshold) {
                    return true
                }
            }
        }

        return false
    }

    fun isSkinColor(h: Float, s: Float, v: Float): Boolean {
        return (h in 0f..50f && s in 0.2f..0.68f && v in 0.35f..1f)
    }

}