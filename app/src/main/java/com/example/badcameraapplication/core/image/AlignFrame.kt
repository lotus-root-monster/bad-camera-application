package com.example.badcameraapplication.core.image

import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.calib3d.Calib3d
import org.opencv.core.Mat
import org.opencv.core.MatOfDMatch
import org.opencv.core.MatOfKeyPoint
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.features2d.DescriptorMatcher
import org.opencv.features2d.ORB
import org.opencv.imgproc.Imgproc

fun alignFrame(
    currentFrame: Bitmap,
    referenceFrame: Bitmap,
): Mat {
    // 画像準備
    val matReference = Mat()
    val matCurrent = Mat()
    Utils.bitmapToMat(referenceFrame, matReference)
    Utils.bitmapToMat(currentFrame, matCurrent)
    Imgproc.cvtColor(matReference, matReference, Imgproc.COLOR_RGB2GRAY)
    Imgproc.cvtColor(matCurrent, matCurrent, Imgproc.COLOR_RGB2GRAY)

    // 特徴点抽出・マッチング
    val orb = ORB.create()
    val keypointsReference = MatOfKeyPoint()
    val keypointsCurrent = MatOfKeyPoint()
    val descriptorsReference = Mat()
    val descriptorsCurrent = Mat()
    orb.detectAndCompute(matReference, Mat(), keypointsReference, descriptorsReference)
    orb.detectAndCompute(matCurrent, Mat(), keypointsCurrent, descriptorsCurrent)
    val matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING)
    val matches = MatOfDMatch()
    matcher.match(descriptorsCurrent, descriptorsReference, matches)

    // 対応点取得
    val referencePoints = mutableListOf<Point>()
    val currentPoints = mutableListOf<Point>()
    val keyPointsReferenceList = keypointsReference.toList()
    val keyPointsCurrentList = keypointsCurrent.toList()
    val sortMatches = matches.toList().sortedBy { it.distance }
    val goodMatches = MatOfDMatch(*sortMatches.take(30).toTypedArray())
    goodMatches.toList().forEach { match ->
        referencePoints.add(keyPointsReferenceList[match.trainIdx].pt)
        currentPoints.add(keyPointsCurrentList[match.queryIdx].pt)
    }

    // 変形・位置合わせ
    val referenceMat = MatOfPoint2f(*referencePoints.toTypedArray())
    val currentMat = MatOfPoint2f(*currentPoints.toTypedArray())
    if (currentPoints.size < 4) {
        descriptorsReference.release()
        descriptorsCurrent.release()
        keypointsReference.release()
        keypointsCurrent.release()
        matches.release()
        goodMatches.release()
        matReference.release()
        referenceMat.release()
        currentMat.release()
        return matCurrent
    }
    val homography = Calib3d.findHomography(
        currentMat,
        referenceMat,
        Calib3d.RANSAC,
        5.0,
    )
    if (homography.empty() || homography.rows() != 3 || homography.cols() != 3) {
        descriptorsReference.release()
        descriptorsCurrent.release()
        keypointsReference.release()
        keypointsCurrent.release()
        matches.release()
        goodMatches.release()
        matReference.release()
        referenceMat.release()
        currentMat.release()
        homography.release()
        return matCurrent
    }
    val alignedMat = Mat()
    Imgproc.warpPerspective(
        matCurrent,
        alignedMat,
        homography,
        matReference.size()
    )

    descriptorsReference.release()
    descriptorsCurrent.release()
    keypointsReference.release()
    keypointsCurrent.release()
    matches.release()
    goodMatches.release()
    matReference.release()
    matCurrent.release()
    referenceMat.release()
    currentMat.release()
    homography.release()

    return alignedMat
}
