package com.example.badcameraapplication.ui.setting

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.badcameraapplication.domain.model.CameraState
import kotlinx.serialization.Serializable

@Serializable
data object SettingNavKey : NavKey

fun EntryProviderScope<NavKey>.settingScreen(onNavigateToCameraClick: (CameraState) -> Unit) {
    entry<SettingNavKey> {
        SettingScreen(onNavigateToCamera=onNavigateToCameraClick)
    }
}