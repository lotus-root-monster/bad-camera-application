package com.example.badcameraapplication.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.badcameraapplication.ui.camera.CameraNavKey
import com.example.badcameraapplication.ui.camera.cameraScreen
import kotlinx.serialization.Serializable

@Composable
fun MainScreen() {
    val backStack = rememberNavBackStack(RootNavKey)

    NavDisplay(
        backStack = backStack,
        onBack = backStack::removeLast,
        entryProvider = entryProvider {
            rootScreen(onNavigateToCameraClick = { backStack.add(CameraNavKey) })
            cameraScreen()
        }
    )
}

@Serializable
private data object RootNavKey : NavKey

private fun EntryProviderScope<NavKey>.rootScreen(
    onNavigateToCameraClick: () -> Unit,
) {
    entry<RootNavKey> {
        RootScreen(onLaunchCameraClick = onNavigateToCameraClick)
    }
}

@Composable
private fun RootScreen(
    onLaunchCameraClick: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Button(
            onClick = onLaunchCameraClick,
            modifier = Modifier.fillMaxWidth(0.75f),
        ) {
            Text(text = "カメラを起動")
        }
    }
}