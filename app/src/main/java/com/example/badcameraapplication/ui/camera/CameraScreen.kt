package com.example.badcameraapplication.ui.camera

import android.content.Context
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.badcameraapplication.domain.model.CameraMode
import com.example.badcameraapplication.ui.camera.util.CameraButtonsLayout
import com.example.badcameraapplication.ui.camera.util.CameraProvider
import com.example.badcameraapplication.ui.components.BackButton
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import androidx.camera.core.Preview as CameraPreview

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    cameraMode: CameraMode?,
    onBackClick: () -> Unit,
    onNavigateToSettingClick: (CameraMode?) -> Unit,
    viewModel: CameraViewModel = hiltViewModel(),
    context: Context = LocalContext.current,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    val cameraProvider = remember {
        CameraProvider(
            cameraMode = cameraMode ?: CameraMode.default,
            context = context,
            lifecycleOwner = lifecycleOwner,
        )
    }
    val preview = remember {
        CameraPreview.Builder()
            .setResolutionSelector(cameraProvider.resolution())
            .build()
            .also { it.setSurfaceProvider(viewModel::onNewSurfaceRequest) }
    }
    val imageCapture = remember {
        ImageCapture.Builder()
            .setResolutionSelector(cameraProvider.resolution())
            .build()
    }
    val imageAnalyzer = remember {
        ImageAnalysis.Builder()
            .setResolutionSelector(cameraProvider.resolution())
            .build()
    }

    LaunchedEffect(cameraPermissionState) {
        snapshotFlow { cameraPermissionState.status.isGranted }.collect {
            if(it) {
                cameraProvider.bindCamera(
                    preview = preview,
                    imageCapture = imageCapture,
                    imageAnalyzer = imageAnalyzer,
                )
            }
        }
    }

    LifecycleEventEffect(event = Lifecycle.Event.ON_START) {
        viewModel.onStart(initialCameraMode = cameraMode)
    }

    CameraScreen(
        state = state,
        isGranted = cameraPermissionState.status.isGranted,
        onBackClick = onBackClick,
        onNavigateToSettingClick = {
            onNavigateToSettingClick(state.cameraMode)
        },
        onLaunchPermissionRequest = cameraPermissionState::launchPermissionRequest,
        onCameraClick = { cameraProvider.takePicture(imageCapture = imageCapture) },
    )
}

@Composable
private fun CameraScreen(
    state: CameraViewModel.State,
    isGranted: Boolean,
    onBackClick: () -> Unit,
    onNavigateToSettingClick: () -> Unit,
    onLaunchPermissionRequest: () -> Unit,
    onCameraClick: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        if (isGranted) {
            CameraXViewFinder(surfaceRequest = state.surfaceRequest)
            CameraButtonsLayout(
                onNavigateToSettingClick = onNavigateToSettingClick,
                onCameraClick = onCameraClick,
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
private fun CameraXViewFinder(surfaceRequest: SurfaceRequest?) {
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
    )
}