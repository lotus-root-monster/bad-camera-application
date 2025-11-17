package com.example.badcameraapplication.ui.camera

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.badcameraapplication.domain.model.CameraMode
import kotlinx.serialization.Serializable

@Serializable
data class CameraNavKey(
    val cameraMode: CameraMode,
) : NavKey

fun EntryProviderScope<NavKey>.cameraScreen(
    onBackClick: () -> Unit,
    onNavigateToSettingClick: (CameraMode?) -> Unit,
) {
    entry<CameraNavKey> { key ->
        CameraScreen(
            cameraMode = key.cameraMode,
            onBackClick = onBackClick,
            onNavigateToSettingClick = onNavigateToSettingClick,
        )
    }
}