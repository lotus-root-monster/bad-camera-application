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
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.badcameraapplication.domain.model.CameraMode
import com.example.badcameraapplication.domain.model.CameraState
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executors

class CameraProvider(
    private val cameraMode: CameraMode,
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
) {
    private val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

    fun takePicture(imageCapture: ImageCapture) {
        val imageName = "Broken-App_" + dateTime()
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, imageName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
        }
        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            context.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()

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
                        ImageAnalyzer(),
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
}
