package com.example.badcameraapplication.ui.camera

import android.content.Context
import android.graphics.Rect
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.SurfaceRequest
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.badcameraapplication.domain.model.CameraMode
import com.example.badcameraapplication.domain.model.Image
import com.example.badcameraapplication.ui.camera.util.CameraButtonsLayout
import com.example.badcameraapplication.ui.camera.util.CameraProvider
import com.example.badcameraapplication.ui.components.BackButton
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.CoroutineScope
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
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    val cameraProvider = remember {
        CameraProvider(
            cameraMode = cameraMode ?: CameraMode.default,
            context = context,
            lifecycleOwner = lifecycleOwner,
            coroutineScope = coroutineScope,
            onStartCapture = viewModel::onStartCapture,
            onCompleteCapture = viewModel::onCompleteCapture,
            onSmileDetect = viewModel::onSmileDetect,
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
            if (it) {
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
            FaceBoundingBox(faceImage = state.faceImage)
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

    if (state.cameraLoadingState is CameraViewModel.CameraLoadingState.Loading) {
        Dialog(onDismissRequest = {}) {
            CircularProgressIndicator(modifier = Modifier.size(100.dp))
        }
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

@Composable
private fun FaceBoundingBox(faceImage: Image?) {
    var previewViewSize by remember { mutableStateOf(IntSize.Zero) }
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { size -> previewViewSize = size },
    ) {
        faceImage?.faces?.forEach { face ->
            val rect = face.rescale(
                viewSize = previewViewSize,
                imageWidth = faceImage.width.toFloat(),
                imageHeight = faceImage.height.toFloat(),
            )
            drawRect(
                color = Color.Green,
                topLeft = rect.topLeft,
                size = rect.size,
                style = Stroke(width = 2.dp.toPx())
            )
        }
    }
}

private fun Rect.rescale(
    viewSize: IntSize,
    imageWidth: Float,
    imageHeight: Float,
): androidx.compose.ui.geometry.Rect {
    val scaleX = viewSize.width / imageWidth
    val scaleY = viewSize.height / imageHeight
    val scale = minOf(scaleX, scaleY)
    val offsetX = (viewSize.width - imageWidth * scale) / 2
    val offsetY = (viewSize.height - imageHeight * scale) / 2
    val composeRect = this.toComposeRect()
    return androidx.compose.ui.geometry.Rect(
        left = composeRect.left * scale + offsetX,
        top = composeRect.top * scale + offsetY,
        right = composeRect.right * scale + offsetX,
        bottom = composeRect.bottom * scale + offsetY,
    )
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