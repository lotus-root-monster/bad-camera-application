package com.example.badcameraapplication.core.image

import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

fun combineFrames(
    accumulatedBitmap: Bitmap,
    alignedMat: Mat,
    frameCount: Int,
): Bitmap {
    // 準備
    val inputMat = Mat()
    val accumulatedMatFloat = Mat()
    Utils.bitmapToMat(accumulatedBitmap, inputMat)
    inputMat.convertTo(accumulatedMatFloat, CvType.CV_32FC4)
    val alignedMatFloat = Mat()
    val alignedMatColor = Mat()
    if (alignedMat.channels() == 1) {
        Imgproc.cvtColor(
            alignedMat,
            alignedMatColor,
            Imgproc.COLOR_GRAY2BGRA,
        )
    } else {
        alignedMat.copyTo(alignedMatColor)
    }
    alignedMatColor.convertTo(alignedMatFloat, CvType.CV_32FC4)

    // 一番重い処理
    Core.add(
        accumulatedMatFloat,
        alignedMatFloat,
        accumulatedMatFloat,
    )
    val normalizedMat = Mat()
    accumulatedMatFloat.convertTo(
        normalizedMat,
        CvType.CV_8UC4,
        1.0 / frameCount.toDouble(),
    )

    // 締め作業
    val resultBitmap = createBitmap(
        accumulatedBitmap.width,
        accumulatedBitmap.height,
        accumulatedBitmap.config!!,
    )
    Utils.matToBitmap(normalizedMat, resultBitmap)

    inputMat.release()
    alignedMatColor.release()
    accumulatedMatFloat.release()
    alignedMatFloat.release()
    normalizedMat.release()

    return resultBitmap
}