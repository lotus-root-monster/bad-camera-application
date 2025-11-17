package com.example.badcameraapplication.domain.model

import android.util.Size
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import kotlinx.serialization.Serializable

data class CameraState(
    val lensFacing: Int,
    val captureRatio: Int,
    val resolution: Size,
    val isRecording: Boolean,
    val zoomLevel: Float,
    val useImageAnalyzer: Boolean,
) {

    companion object {
        val default = CameraState(
            lensFacing = CameraSelector.LENS_FACING_BACK,
            captureRatio = AspectRatio.RATIO_4_3,
            resolution = Size(1920, 1080),
            isRecording = false,
            zoomLevel = 1.0f,
            useImageAnalyzer = false,
        )
    }
}

fun CameraState.serialize() = SerializeCameraState(
    lensFacing = lensFacing,
    captureRatio = captureRatio,
    resolution = resolution.width to resolution.height,
    isRecording = isRecording,
    zoomLevel = zoomLevel,
    useImageAnalyzer = useImageAnalyzer,
)

@Serializable
data class SerializeCameraState(
    val lensFacing: Int,
    val captureRatio: Int,
    val resolution: Pair<Int, Int>,
    val isRecording: Boolean,
    val zoomLevel: Float,
    val useImageAnalyzer: Boolean,
)