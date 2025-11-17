package com.example.badcameraapplication.domain.model

import android.util.Size
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector

data class CameraState(
    val lensFacing: Int,
    val captureRatio: Int,
    val resolution: Size,
    val zoomLevel: Float,
    val useImageAnalyzer: Boolean,
) {
    companion object {
        val default = CameraState(
            lensFacing = CameraSelector.LENS_FACING_BACK,
            captureRatio = AspectRatio.RATIO_4_3,
            resolution = Size(1920, 1080),
            zoomLevel = 1.0f,
            useImageAnalyzer = false,
        )
        val highSpecification = CameraState(
            lensFacing = CameraSelector.LENS_FACING_FRONT,
            captureRatio = AspectRatio.RATIO_16_9,
            resolution = Size(3840, 2160),
            zoomLevel = 10f,
            useImageAnalyzer = true,
        )
    }
}