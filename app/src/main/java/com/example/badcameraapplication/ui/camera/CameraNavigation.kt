package com.example.badcameraapplication.ui.camera

import android.util.Size
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.badcameraapplication.domain.model.CameraState
import com.example.badcameraapplication.domain.model.SerializeCameraState
import kotlinx.serialization.Serializable

@Serializable
data class CameraNavKey(
    val cameraState: SerializeCameraState,
) : NavKey

fun EntryProviderScope<NavKey>.cameraScreen(
    onBackClick: () -> Unit,
    onNavigateToSettingClick: () -> Unit,
) {
    entry<CameraNavKey> { key ->
        CameraScreen(
            cameraState = CameraState(
                lensFacing = key.cameraState.lensFacing,
                captureRatio = key.cameraState.captureRatio,
                resolution = Size(
                    key.cameraState.resolution.first,
                    key.cameraState.resolution.second
                ),
                isRecording = key.cameraState.isRecording,
                zoomLevel = key.cameraState.zoomLevel,
                useImageAnalyzer = key.cameraState.useImageAnalyzer
            ),
            onBackClick = onBackClick,
            onNavigateToSettingClick = onNavigateToSettingClick,
        )
    }
}