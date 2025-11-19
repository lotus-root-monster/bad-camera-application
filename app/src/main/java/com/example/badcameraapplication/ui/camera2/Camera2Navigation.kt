package com.example.badcameraapplication.ui.camera2

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object Camera2NavKey : NavKey

fun EntryProviderScope<NavKey>.camera2Screen(onBackClick: () -> Unit) {
    entry<Camera2NavKey> {
        Camera2Screen(onBackClick = onBackClick)
    }
}