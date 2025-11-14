package com.example.badcameraapplication.ui.camera

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object CameraNavKey : NavKey

fun EntryProviderScope<NavKey>.cameraScreen() {
    entry<CameraNavKey> {
        CameraScreen()
    }
}