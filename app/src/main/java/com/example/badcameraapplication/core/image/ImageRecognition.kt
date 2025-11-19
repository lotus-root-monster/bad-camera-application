package com.example.badcameraapplication.core.image

import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions

class ImageRecognition : ImageAnalysis.Analyzer {
    val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            labeler.process(image)
                .addOnSuccessListener { labels ->
                    for (label in labels) {
                        val text = label.text
                        val confidence = label.confidence
                        Log.d("ImageAnalyzer", "Label: $text, Confidence: $confidence")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("ImageAnalyzer", "ラベリングに失敗しました", e)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }
}