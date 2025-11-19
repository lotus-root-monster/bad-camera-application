package com.example.badcameraapplication.ui.camera.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.badcameraapplication.core.image.FaceRecognition
import com.example.badcameraapplication.domain.model.CameraMode
import com.example.badcameraapplication.domain.model.CameraState
import com.example.badcameraapplication.domain.model.Image
import com.example.badcameraapplication.ui.camera.util.ImageCapturedCallback.Companion.FLAME_QUEUE_SIZE
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executors

class CameraProvider(
    private val cameraMode: CameraMode,
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val coroutineScope: CoroutineScope,
    private val onStartCapture: () -> Unit,
    private val onCompleteCapture: () -> Unit,
    private val onSmileDetect: (Image) -> Unit,
) {
    private val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    private var captureCompleter: CompletableDeferred<Unit>? = null
    private var imageCapturedCallback: ImageCapturedCallback? = null

    fun takePicture(imageCapture: ImageCapture) {
        val imageName = "Broken-App_" + dateTime()
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, imageName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
        }

        if (cameraMode.isUseMFNRImageCaptureChecked) {
            val imageUri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            ) ?: return

            captureCompleter = CompletableDeferred()
            var isError = false
            imageCapturedCallback = ImageCapturedCallback(
                context = context,
                onStartCapture = onStartCapture,
                onSuccessCapture = { bitmap ->
                    context.contentResolver.openOutputStream(imageUri).use { outstay ->
                        checkNotNull(outstay)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outstay)
                        showToast(text = "写真を保存しました: ${contentValues.get(MediaStore.MediaColumns.DISPLAY_NAME)}")
                    }
                    captureCompleter?.complete(Unit)
                    onCompleteCapture()
                },
                onErrorCapture = {
                    showToast(text = "写真の保存に失敗しました")
                    captureCompleter?.complete(Unit)
                    isError = true
                    onCompleteCapture()
                }
            )

            coroutineScope.launch {
                for (i in 0 until FLAME_QUEUE_SIZE) {
                    imageCapture.takePicture(
                        ContextCompat.getMainExecutor(context),
                        imageCapturedCallback ?: return@launch,
                    )
                    if (isError) break
                }
                captureCompleter?.await()
            }
        } else {
            val outputOptions = ImageCapture.OutputFileOptions.Builder(
                context.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            ).build()
            imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(context),
                ImageSavedCallback(
                    onStartCapture = onStartCapture,
                    onSuccessSave = {
                        showToast(text = "写真を保存しました: ${contentValues.get(MediaStore.MediaColumns.DISPLAY_NAME)}")
                        onCompleteCapture()
                    },
                    onErrorSave = { e ->
                        showToast(text = "写真の保存に失敗しました")
                        Log.e("CameraProvider", "写真撮影をしようとしてエラーになった", e)
                        onCompleteCapture()
                    },
                )
            )
        }
    }

    fun resolution(): ResolutionSelector {
        val resolutionStrategy = ResolutionStrategy(
            if (cameraMode.isResolutionChecked) {
                CameraState.highSpecification.resolution
            } else {
                CameraState.default.resolution
            },
            ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER,
        )
        val aspectRatioStrategy = AspectRatioStrategy(
            if (cameraMode.isCaptureRatioChecked) {
                CameraState.highSpecification.captureRatio
            } else {
                CameraState.default.captureRatio
            },
            AspectRatioStrategy.FALLBACK_RULE_AUTO,
        )
        return ResolutionSelector.Builder()
            .setAspectRatioStrategy(aspectRatioStrategy)
            .setResolutionStrategy(resolutionStrategy)
            .build()
    }

    fun bindCamera(
        preview: Preview?,
        imageCapture: ImageCapture?,
        imageAnalyzer: ImageAnalysis?,
    ) {
        cameraProviderFuture.addListener(
            {
                val cameraProvider = cameraProviderFuture.get()
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                if (cameraMode.isUseImageAnalyzerChecked) {
                    imageAnalyzer?.setAnalyzer(
                        Executors.newSingleThreadExecutor(),
                        FaceRecognition(onSmileDetect = onSmileDetect),
                    )
                }
                try {
                    ContextCompat.getMainExecutor(context).execute {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageCapture,
                            imageAnalyzer,
                        )
                    }
                } catch (e: Exception) {
                    Log.e("CameraProvider", "bindToLifecycleをしようとした", e)
                }
            },
            ContextCompat.getMainExecutor(context),
        )
    }

    private fun dateTime() = SimpleDateFormat(
        "yyyy/MM/dd_hh:mm:ss",
        Locale.JAPAN,
    ).format(System.currentTimeMillis())

    private fun showToast(text: String) {
        Toast.makeText(
            context,
            text,
            Toast.LENGTH_SHORT,
        ).show()
    }
}
