package com.example.badcameraapplication.ui

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.badcameraapplication.core.performance.MemoryMonitor
import com.example.badcameraapplication.domain.model.CameraMode
import com.example.badcameraapplication.ui.camera.CameraNavKey
import com.example.badcameraapplication.ui.camera.cameraScreen
import com.example.badcameraapplication.ui.camera2.Camera2NavKey
import com.example.badcameraapplication.ui.camera2.camera2Screen
import com.example.badcameraapplication.ui.setting.SettingNavKey
import com.example.badcameraapplication.ui.setting.settingScreen
import kotlinx.serialization.Serializable

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
    context: Context = LocalContext.current,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val memoryMonitor = remember {
        MemoryMonitor(
            context = context,
            fetchRamUsage = viewModel::fetchRamUsage,
        )
    }
    val backStack = rememberNavBackStack(RootNavKey)

    LifecycleStartEffect(Unit) {
        viewModel.onStart()
        memoryMonitor.startMonitoring()
        onStopOrDispose {
            viewModel.onStop()
            memoryMonitor.stopMonitoring()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray),
        contentAlignment = Alignment.Center,
    ) {
        NavDisplay(
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            backStack = backStack,
            onBack = backStack::removeLast,
            entryProvider = entryProvider {
                rootScreen(
                    onNavigateToCameraClick = { backStack.add(CameraNavKey(CameraMode.default)) },
                    onNavigateToCamera2Click = { backStack.add(Camera2NavKey) }
                )
                cameraScreen(
                    onBackClick = backStack::removeLast,
                    onNavigateToSettingClick = { key ->
                        backStack.add(SettingNavKey(key ?: CameraMode.default))
                    },
                )
                settingScreen(
                    onBackClick = backStack::removeLast,
                    onNavigateToCameraClick = { key ->
                        backStack.removeAll { it != RootNavKey }
                        backStack.add(CameraNavKey(key))
                    }
                )
                camera2Screen(onBackClick = backStack::removeLast)
            }
        )
        DisplayPerformance(
            latestFps = state.latestFps,
            latestCpuUsage = state.latestCpuUsage,
            latestRam = state.latestRam,
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
    onNavigateToCamera2Click: () -> Unit,
) {
    entry<RootNavKey> {
        RootScreen(
            onNavigateToCameraClick = onNavigateToCameraClick,
            onNavigateToCamera2Click = onNavigateToCamera2Click,
        )
    }
}

@Composable
private fun RootScreen(
    onNavigateToCameraClick: () -> Unit,
    onNavigateToCamera2Click: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(
            onClick = onNavigateToCameraClick,
            modifier = Modifier.fillMaxWidth(0.75f),
        ) {
            Text(text = "カメラを起動")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onNavigateToCamera2Click,
            modifier = Modifier.fillMaxWidth(0.75f),
        ) {
            Text(text = "カメラ2を起動")
        }
    }
}

@Composable
private fun DisplayPerformance(
    latestFps: Double,
    latestCpuUsage: Long,
    latestRam: Double,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.weight(1f))
        Column(
            modifier = Modifier
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
                    start = 32.dp,
                    end = 16.dp
                )
                .padding(vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row {
                Column {
                    Text(text = "FPS:")
                    Text(text = "CPU 使用率:")
                    Text(text = "RAM 使用率:")
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "%2.1f".format(latestFps))
                    Text(text = "%3d%%".format(latestCpuUsage))
                    Text(text = "%2.1f%%".format(latestRam))
                }
            }
        }
    }
}

@Preview
@Composable
private fun DisplayPerformancePreview() {
    DisplayPerformance(
        latestFps = 12.345678,
        latestCpuUsage = 99,
        latestRam = 99.9,
    )
}