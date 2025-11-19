package com.example.badcameraapplication.ui.camera.util

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.core.content.ContextCompat
import com.example.badcameraapplication.core.image.alignFrame
import com.example.badcameraapplication.core.image.combineFrames

class ImageCapturedCallback(
    private val context: Context,
    private val onStartCapture: () -> Unit,
    private val onSuccessCapture: (Bitmap) -> Unit,
    private val onErrorCapture: (Throwable) -> Unit,
) : ImageCapture.OnImageCapturedCallback() {
    val frameQueue = mutableListOf<ImageProxy>()

    override fun onCaptureStarted() {
        onStartCapture()
    }

    override fun onCaptureSuccess(image: ImageProxy) {
        frameQueue.add(image)
        if (frameQueue.size >= FLAME_QUEUE_SIZE) {
            startNoiseReduction(inputFrameQueue = frameQueue.toList())
            frameQueue.clear()
        }
    }

    private fun startNoiseReduction(inputFrameQueue: List<ImageProxy>) {
        ContextCompat.getMainExecutor(context).execute {
            val referenceFrame = inputFrameQueue.first().toBitmap()
            var accumulatedBitmap = referenceFrame.copy(Bitmap.Config.ARGB_8888, true)
            try {
                inputFrameQueue.forEachIndexed { index, frame ->
                    val currentBitmap = frame.toBitmap()
                    val alignedFrame = alignFrame(
                        currentFrame = referenceFrame,
                        referenceFrame = currentBitmap,
                    )
                    accumulatedBitmap = combineFrames(
                        accumulatedBitmap = accumulatedBitmap,
                        alignedMat = alignedFrame,
                        frameCount = index + 1,
                    )
                    alignedFrame.release()
                    frame.close()
                }
                onSuccessCapture(accumulatedBitmap)
            } catch (e: Exception) {
                onErrorCapture(e)
                Log.e("AppImageCapture", "画像キャプチャをしようとした", e)
            }
        }
    }

    override fun onError(exception: ImageCaptureException) {
        frameQueue.clear()
        onErrorCapture(exception)
    }

    companion object {
        const val FLAME_QUEUE_SIZE = 3
    }
}