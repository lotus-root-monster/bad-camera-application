package com.example.badcameraapplication.ui.setting

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.badcameraapplication.domain.model.CameraMode
import kotlinx.serialization.Serializable

@Serializable
data class SettingNavKey(
    val cameraMode: CameraMode,
) : NavKey

fun EntryProviderScope<NavKey>.settingScreen(onNavigateToCameraClick: (CameraMode) -> Unit) {
    entry<SettingNavKey> { key ->
        SettingScreen(
            cameraMode = key.cameraMode,
            onNavigateToCamera = onNavigateToCameraClick
        )
    }
}