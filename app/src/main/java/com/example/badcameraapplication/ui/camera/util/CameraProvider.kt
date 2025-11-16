package com.example.badcameraapplication.ui.camera.util

import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executors

class CameraProvider(
    private val onNewSurfaceRequest: (SurfaceRequest) -> Unit,
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner
) {
    private val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    private var cameraProvider: ProcessCameraProvider? = null

    private val imageCapture = ImageCapture.Builder().build()
    private val imageName: String = "Broken_App" + dateTime()
    private val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, imageName)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
    }
    private val outputOptions = ImageCapture.OutputFileOptions.Builder(
        context.contentResolver,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        contentValues
    ).build()

    private val imageAnalyzer = ImageAnalysis.Builder()
        .setResolutionSelector(
            ResolutionSelector.Builder()
                .setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
                .build()
        )
        .setImageQueueDepth(3)
        .build()

    init {
        cameraProviderFuture.addListener(
            {
                cameraProvider = cameraProviderFuture.get()
                bindCamera(
                    imageCaptureUseCase = imageCapture,
                    imageAnalysisUseCase = imageAnalyzer,
                )
            },
            ContextCompat.getMainExecutor(context),
        )
    }

    fun takePicture() {
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(error: ImageCaptureException) {
                    Log.e("CameraProvider", "写真撮影をしようとしてエラーになった", error)
                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Toast.makeText(
                        context,
                        "写真を保存しました: ${contentValues.get(MediaStore.MediaColumns.DISPLAY_NAME)}",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
        )
    }

    fun onDestruction() {
        imageAnalyzer.setAnalyzer(
            Executors.newSingleThreadExecutor(),
            ImageAnalyzer(),
        )
    }

    fun onCancelVandalism() {
        imageAnalyzer.clearAnalyzer()
    }

    private fun bindCamera(
        imageCaptureUseCase: ImageCapture? = null,
        imageAnalysisUseCase: ImageAnalysis? = null,
    ) {
        val provider = cameraProvider ?: return
        val previewUseCase = Preview.Builder().build().also {
            it.setSurfaceProvider { newSurfaceRequest ->
                onNewSurfaceRequest(newSurfaceRequest)
            }
        }
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        try {
            provider.unbindAll()
            provider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                previewUseCase,
                imageCaptureUseCase,
                imageAnalysisUseCase,
            )
        } catch (e: Exception) {
            Log.e("CameraProvider", "bindToLifecycleをしようとした", e)
        }
    }

    private fun dateTime() = SimpleDateFormat(
        "yyyy/MM/dd_hh:mm:ss",
        Locale.JAPAN,
    ).format(System.currentTimeMillis())
}
