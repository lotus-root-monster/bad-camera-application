package com.example.badcameraapplication.ui.camera2

import android.Manifest
import android.content.Context
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.params.OutputConfiguration
import android.hardware.camera2.params.SessionConfiguration
import android.media.ImageReader
import android.os.Handler
import android.os.Looper
import android.view.Surface
import android.view.TextureView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.badcameraapplication.domain.model.CameraState
import com.example.badcameraapplication.ui.components.BackButton
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Camera2Screen(
    onBackClick: () -> Unit,
    context: Context = LocalContext.current,
) {
    val textureView = remember { TextureView(context) }
    val cameraManager = remember {
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }
    val executor = remember { ContextCompat.getMainExecutor(context) }
    val permissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(permissionState) {
        if (!permissionState.status.isGranted) {
            permissionState.launchPermissionRequest()
        }
    }

    val yuvImageReader = remember {
        ImageReader.newInstance(
            CameraState.highSpecification.resolution.width,
            CameraState.highSpecification.resolution.height,
            ImageFormat.YUV_420_888,
            2,
        )
    }

    val jpegImageReader = remember {
        ImageReader.newInstance(
            CameraState.highSpecification.resolution.width,
            CameraState.highSpecification.resolution.height,
            ImageFormat.JPEG,
            2,
        )
    }

    if (permissionState.status.isGranted) {
        DisposableEffect(textureView, executor, cameraManager) {
            var cameraDevice: CameraDevice? = null
            var session: CameraCaptureSession? = null
            val imageReaderHandler = Handler(Looper.getMainLooper())

            val job = CoroutineScope(Dispatchers.Main).launch {
                val surface = textureView.awaitSurface()
                val cameraId = cameraManager.cameraIdList[0]
                val characteristics = cameraManager.getCameraCharacteristics(cameraId)

                cameraDevice = openCamera(cameraManager, cameraId, executor)

                val surfaces = listOf(surface, yuvImageReader.surface, jpegImageReader.surface)
                session = createCaptureSession(
                    cameraDevice,
                    surfaces,
                    executor
                )

                yuvImageReader.setOnImageAvailableListener(
                    { reader -> reader.acquireLatestImage()?.close() },
                    imageReaderHandler,
                )
                jpegImageReader.setOnImageAvailableListener(
                    { reader -> reader.acquireLatestImage()?.close() },
                    imageReaderHandler,
                )

                val captureRequest = session.device
                    .createCaptureRequest(CameraDevice.TEMPLATE_ZERO_SHUTTER_LAG)
                    .apply {
                        addTarget(surface)
                        addTarget(yuvImageReader.surface)
                        addTarget(jpegImageReader.surface)

                        set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO)
                        // High quality settings for various features
                        set(
                            CaptureRequest.NOISE_REDUCTION_MODE,
                            CaptureRequest.NOISE_REDUCTION_MODE_HIGH_QUALITY
                        )
                        set(CaptureRequest.EDGE_MODE, CaptureRequest.EDGE_MODE_HIGH_QUALITY)
                        set(CaptureRequest.TONEMAP_MODE, CaptureRequest.TONEMAP_MODE_HIGH_QUALITY)
                        set(
                            CaptureRequest.COLOR_CORRECTION_MODE,
                            CaptureRequest.COLOR_CORRECTION_MODE_HIGH_QUALITY
                        )
                        set(CaptureRequest.SHADING_MODE, CaptureRequest.SHADING_MODE_HIGH_QUALITY)
                        set(
                            CaptureRequest.HOT_PIXEL_MODE,
                            CaptureRequest.HOT_PIXEL_MODE_HIGH_QUALITY
                        )

                        // Enable stabilization if supported
                        val availableOisModes =
                            characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION)
                        if (availableOisModes?.contains(CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE_ON) == true) {
                            set(
                                CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE,
                                CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE_ON
                            )
                        }
                        val availableVideoStabilizationModes =
                            characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_VIDEO_STABILIZATION_MODES)
                        if (availableVideoStabilizationModes?.contains(CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE_ON) == true) {
                            set(
                                CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE,
                                CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE_ON
                            )
                        }

                        // Enable HDR if supported
                        val availableSceneModes =
                            characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_SCENE_MODES)
                        if (availableSceneModes?.contains(CaptureRequest.CONTROL_SCENE_MODE_HDR) == true) {
                            set(
                                CaptureRequest.CONTROL_SCENE_MODE,
                                CaptureRequest.CONTROL_SCENE_MODE_HDR
                            )
                        }
                    }.build()

                session.setRepeatingRequest(captureRequest, null, null)
            }

            onDispose {
                job.cancel()
                session?.close()
                cameraDevice?.close()
                yuvImageReader.close()
                jpegImageReader.close()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray),
        contentAlignment = Alignment.Center,
    ) {
        AndroidView({ textureView })
        BackButton(
            onBackClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .safeDrawingPadding()
        )
    }
}

private suspend fun TextureView.awaitSurface(): Surface =
    suspendCancellableCoroutine { continuation ->
        if (surfaceTexture != null) {
            continuation.resume(Surface(surfaceTexture))
            return@suspendCancellableCoroutine
        }
        surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(
                texture: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                continuation.resume(Surface(texture))
            }

            override fun onSurfaceTextureSizeChanged(
                texture: SurfaceTexture,
                width: Int,
                height: Int
            ) {
            }

            override fun onSurfaceTextureDestroyed(texture: SurfaceTexture): Boolean = true
            override fun onSurfaceTextureUpdated(texture: SurfaceTexture) {}
        }
        continuation.invokeOnCancellation {}
    }

private suspend fun openCamera(
    cameraManager: CameraManager,
    cameraId: String,
    executor: Executor
): CameraDevice =
    suspendCancellableCoroutine { continuation ->
        try {
            cameraManager.openCamera(cameraId, executor, object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) = continuation.resume(camera)
                override fun onDisconnected(camera: CameraDevice) {
                    camera.close()
                }

                override fun onError(camera: CameraDevice, error: Int) {
                    val exception = RuntimeException("Camera error: $error")
                    camera.close()
                    if (continuation.isActive) {
                        continuation.resumeWithException(exception)
                    }
                }
            })
        } catch (e: SecurityException) {
            if (continuation.isActive) {
                continuation.resumeWithException(e)
            }
        }
    }

private suspend fun createCaptureSession(
    cameraDevice: CameraDevice,
    targets: List<Surface>,
    executor: Executor
): CameraCaptureSession =
    suspendCancellableCoroutine { continuation ->
        val outputConfigurations = targets.map { OutputConfiguration(it) }
        val sessionConfiguration = SessionConfiguration(
            SessionConfiguration.SESSION_REGULAR,
            outputConfigurations,
            executor,
            object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) =
                    continuation.resume(session)

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    val exception = RuntimeException("Camera capture session configuration failed")
                    if (continuation.isActive) {
                        continuation.resumeWithException(exception)
                    }
                }
            }
        )
        cameraDevice.createCaptureSession(sessionConfiguration)
    }
