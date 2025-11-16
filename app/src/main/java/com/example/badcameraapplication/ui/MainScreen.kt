package com.example.badcameraapplication.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.badcameraapplication.ui.camera.CameraNavKey
import com.example.badcameraapplication.ui.camera.cameraScreen
import kotlinx.serialization.Serializable

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val backStack = rememberNavBackStack(RootNavKey)

    LifecycleStartEffect(Unit) {
        viewModel.onStart()
        onStopOrDispose { viewModel.onStop() }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        NavDisplay(
            backStack = backStack,
            onBack = backStack::removeLast,
            entryProvider = entryProvider {
                rootScreen(onNavigateToCameraClick = { backStack.add(CameraNavKey) })
                cameraScreen(onBackClick = backStack::removeLast)
            }
        )
        DisplayPerformance(
            latestFps = state.latestFps,
            latestCpuUsage = state.latestCpuUsage,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .safeDrawingPadding(),
        )
    }
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

@Composable
private fun DisplayPerformance(
    latestFps: Double,
    latestCpuUsage: Long,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .width(140.dp)
            .background(
                color = Color.White.copy(alpha = 0.5f),
                shape = RoundedCornerShape(
                    topStartPercent = 50,
                    bottomStartPercent = 50,
                    topEndPercent = 0,
                    bottomEndPercent = 0,
                )
            )
            .padding(
                start = 26.dp,
                end = 16.dp
            )
            .padding(vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = "FPS:")
            Text(text = "%2.1f".format(latestFps))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = "CPU:")
            Text(text = "%3d%%".format(latestCpuUsage))
        }
    }
}

@Preview
@Composable
private fun DisplayPerformancePreview() {
    DisplayPerformance(
        latestFps = 12.345678,
        latestCpuUsage = 99,
    )
}