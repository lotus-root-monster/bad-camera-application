package com.example.badcameraapplication.ui.camera

import android.content.Context
import android.util.Log
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.SurfaceRequest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.badcameraapplication.R
import com.example.badcameraapplication.ui.camera.model.CameraButton
import com.example.badcameraapplication.ui.camera.util.CameraProvider
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(viewModel: CameraViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    CameraScreen(
        state = state,
        isGranted = cameraPermissionState.status.isGranted,
        onLaunchPermissionRequest = cameraPermissionState::launchPermissionRequest,
        onNewSurfaceRequest = viewModel::onNewSurfaceRequest,
    )
}

@Composable
private fun CameraScreen(
    state: CameraViewModel.State,
    isGranted: Boolean,
    onLaunchPermissionRequest: () -> Unit,
    onNewSurfaceRequest: (SurfaceRequest) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        if (isGranted) {
            CameraXViewFinder(
                surfaceRequest = state.surfaceRequest,
                onNewSurfaceRequest = onNewSurfaceRequest
            )
            CameraButtons(
                onCameraCLick = {},
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .safeDrawingPadding()
            )
        } else {
            Button(onClick = onLaunchPermissionRequest) {
                Text(text = "カメラの権限をリクエストする")
            }
        }
    }
}

@Composable
private fun CameraXViewFinder(
    surfaceRequest: SurfaceRequest?,
    onNewSurfaceRequest: (SurfaceRequest) -> Unit,
    context: Context = LocalContext.current,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
) {
    if (LocalInspectionMode.current) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Preview用です",
                fontSize = 50.sp
            )
        }
        return
    }

    val cameraProvider = remember {
        CameraProvider(
            onNewSurfaceRequest = onNewSurfaceRequest,
            context = context,
            lifecycleOwner = lifecycleOwner,
        )
    }
    Log.d("hogehoge", "$cameraProvider")
    surfaceRequest?.let { request ->
        CameraXViewfinder(
            surfaceRequest = request,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
private fun CameraButtons(
    onCameraCLick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    cameraButtons(
        onCameraCLick = onCameraCLick
    ).forEach {
        IconButton(
            onClick = it.onClick,
            modifier = modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.5f))
        ) {
            Icon(
                painter = painterResource(it.iconResId),
                contentDescription = it.contentDescription,
                modifier = Modifier.fillMaxSize(),
                tint = Color.Black.copy(alpha = 0.5f),
            )
        }
    }
}

private fun cameraButtons(
    onCameraCLick: () -> Unit,
) = listOf(
    CameraButton(
        iconResId = R.drawable.ic_camera,
        contentDescription = "カメラ",
        onClick = onCameraCLick,
    )
)

@Preview
@Composable
private fun Preview() {
    CameraScreen(
        state = CameraViewModel.State.initialize(),
        isGranted = true,
        onLaunchPermissionRequest = {},
        onNewSurfaceRequest = {},
    )
}