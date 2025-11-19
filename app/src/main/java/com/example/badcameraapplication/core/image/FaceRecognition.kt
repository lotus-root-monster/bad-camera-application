package com.example.badcameraapplication.core.image

import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.badcameraapplication.domain.model.Image
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

class FaceRecognition(
    private val onSmileDetect: (Image) -> Unit,
) : ImageAnalysis.Analyzer {
    private val highAccuracyOpts = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .build()
    private val detector = FaceDetection.getClient(highAccuracyOpts)

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, 90)

            detector.process(image).addOnSuccessListener { faces ->
                val smileFaces = faces
                    .filter { (it.smilingProbability ?: 0f) > 0.9f }
                    .map { it.boundingBox }
                    .takeIf { it.isNotEmpty() }
                if (smileFaces != null) {
                    onSmileDetect(
                        Image(
                            faces = smileFaces,
                            width = image.width,
                            height = image.height,
                        )
                    )
                }
            }.addOnFailureListener {
                Log.e("FaceRecognition", "顔の検出に失敗しました", it)
            }.addOnCompleteListener {
                imageProxy.close()
            }
        }
    }
}