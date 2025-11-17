package com.example.badcameraapplication.ui.camera

import android.content.Context
import android.widget.Toast
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.SurfaceRequest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.badcameraapplication.core.orientation.isPortrait
import com.example.badcameraapplication.domain.model.CameraState
import com.example.badcameraapplication.ui.camera.util.CameraButtonsLayout
import com.example.badcameraapplication.ui.camera.util.CameraProvider
import com.example.badcameraapplication.ui.camera.util.CameraWarningDialog
import com.example.badcameraapplication.ui.components.BackButton
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    cameraState: CameraState,
    onBackClick: () -> Unit,
    onNavigateToSettingClick: () -> Unit,
    viewModel: CameraViewModel = hiltViewModel(),
    context: Context = LocalContext.current,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    val cameraProvider = remember {
        CameraProvider(
            cameraState = cameraState,
            onNewSurfaceRequest = viewModel::onNewSurfaceRequest,
            context = context,
            lifecycleOwner = lifecycleOwner,
        )
    }

    LaunchedEffect(viewModel.event) {
        viewModel.event.collect { event ->
            when (event) {
                is CameraViewModel.UiEvent.Bomb -> cameraProvider.onBomb()
                is CameraViewModel.UiEvent.Destruction -> cameraProvider.onDestruction()
                is CameraViewModel.UiEvent.Explosion -> Unit

                is CameraViewModel.UiEvent.ResetVandalism -> {
                    Toast.makeText(
                        context,
                        "リセットされました",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
        }
    }

    CameraScreen(
        state = state,
        isGranted = cameraPermissionState.status.isGranted,
        onBackClick = onBackClick,
        onNavigateToSettingClick = onNavigateToSettingClick,
        onLaunchPermissionRequest = cameraPermissionState::launchPermissionRequest,
        onCameraClick = cameraProvider::takePicture,
        onBombClick = viewModel::onBombClick,
        onDestructionClick = viewModel::onDestructionClick,
        onExplosionClick = viewModel::onExplosionClick,
        onCancelVandalism = {
            cameraProvider.onCancelVandalism()
            viewModel.onResetVandalism()
        },
    )

    if (state.isShowWarningDialog) {
        Dialog(onDismissRequest = viewModel::onDismissRequest) {
            CameraWarningDialog(
                onCancelClick = viewModel::onDismissRequest,
                onConfirmClick = viewModel::onConfirmClick,
            )
        }
    }
}

@Composable
private fun CameraScreen(
    state: CameraViewModel.State,
    isGranted: Boolean,
    onBackClick: () -> Unit,
    onNavigateToSettingClick: () -> Unit,
    onLaunchPermissionRequest: () -> Unit,
    onCameraClick: () -> Unit,
    onBombClick: () -> Unit,
    onDestructionClick: () -> Unit,
    onExplosionClick: () -> Unit,
    onCancelVandalism: () -> Unit,
) {
    val isVertical = isPortrait()
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        if (isGranted) {
            CameraXViewFinder(surfaceRequest = state.surfaceRequest)
            CameraButtonsLayout(
                currentVandalismType = state.vandalismType,
                isVertical = isVertical,
                onNavigateToSettingClick = onNavigateToSettingClick,
                onCameraClick = onCameraClick,
                onBombClick = onBombClick,
                onDestructionClick = onDestructionClick,
                onExplosionClick = onExplosionClick,
                onCancelVandalism = onCancelVandalism,
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Button(onClick = onLaunchPermissionRequest) {
                    Text(text = "カメラの権限をリクエストする")
                }
                Text(text = "権限を許可したらカメラが使用できます")
            }
        }
        BackButton(
            onBackClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .safeDrawingPadding()
        )
    }
}

@Composable
private fun CameraXViewFinder(
    surfaceRequest: SurfaceRequest?,
) {
    surfaceRequest?.let { request ->
        CameraXViewfinder(
            surfaceRequest = request,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
    }
}


@Preview
@Composable
private fun Preview() {
    CameraScreen(
        state = CameraViewModel.State.initialize(),
        isGranted = true,
        onBackClick = {},
        onNavigateToSettingClick = {},
        onLaunchPermissionRequest = {},
        onCameraClick = {},
        onBombClick = {},
        onDestructionClick = {},
        onExplosionClick = {},
        onCancelVandalism = {},
    )
}