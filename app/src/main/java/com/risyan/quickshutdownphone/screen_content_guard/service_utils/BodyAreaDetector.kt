package com.risyan.quickshutdownphone.screen_content_guard.service_utils

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions
import kotlinx.coroutines.tasks.await

class BodyAreaDetector {
    suspend fun isTorsoVisibleEnoughForNSFWCheck(bitmap: Bitmap): Boolean {
        val options = AccuratePoseDetectorOptions.Builder()
            .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
            .build()
        val poseDetector = PoseDetection.getClient(options)
        val image = InputImage.fromBitmap(bitmap, 0)
        val pose = poseDetector.process(image).await()

        val requiredLandmarks = listOf(
            PoseLandmark.LEFT_SHOULDER,
            PoseLandmark.RIGHT_SHOULDER,
            PoseLandmark.LEFT_HIP,
            PoseLandmark.RIGHT_HIP,
            PoseLandmark.NOSE
        ).mapNotNull { pose.getPoseLandmark(it) }

        // 1. Are most landmarks present?
        if (requiredLandmarks.size < 4) return false

        // 3. Check pose confidence average
        val avgConfidence = requiredLandmarks.map { it.inFrameLikelihood }.average()
        if (avgConfidence < 0.5) return false

        return true
    }

    suspend fun isCrotchVisible(bitmap: Bitmap): Boolean {
        val options = AccuratePoseDetectorOptions.Builder()
            .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
            .build()
        val poseDetector = PoseDetection.getClient(options)
        val image = InputImage.fromBitmap(bitmap, 0)
        val pose = poseDetector.process(image).await()

        val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
        val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
        val leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
        val rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)

        // Check if key lower-body points are detected
        if (leftHip == null || rightHip == null || leftKnee == null || rightKnee == null) return false

        val crotchX = (leftHip.position.x + rightHip.position.x) / 2
        val crotchY = (leftHip.position.y + rightHip.position.y) / 2

        // Check if crotch is in image
        val margin = 20f
        val isInFrame = crotchX > margin && crotchX < bitmap.width - margin &&
                crotchY > margin && crotchY < bitmap.height - margin
        if (!isInFrame) return false

        // Check vertical space between hips and knees — indicates visible upper thighs
        val avgKneeY = (leftKnee.position.y + rightKnee.position.y) / 2
        val avgHipY = (leftHip.position.y + rightHip.position.y) / 2
        val thighHeight = avgKneeY - avgHipY

        if (thighHeight < bitmap.height * 0.1f) return false

        return true
    }

    suspend fun isNsfwBodyArea(bitmap: Bitmap): Boolean {
        return isTorsoVisibleEnoughForNSFWCheck(bitmap) || isCrotchVisible(bitmap)
    }
}