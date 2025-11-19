package com.example.badcameraapplication.ui.camera.util

import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException

class ImageSavedCallback(
    private val onStartCapture: () -> Unit,
    private val onSuccessSave: () -> Unit,
    private val onErrorSave: (Throwable) -> Unit,
) : ImageCapture.OnImageSavedCallback {
    override fun onCaptureStarted() {
        onStartCapture()
    }

    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
        onSuccessSave()
    }

    override fun onError(error: ImageCaptureException) {
        onErrorSave(error)
    }
}