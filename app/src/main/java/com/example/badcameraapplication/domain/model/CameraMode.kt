package com.example.badcameraapplication.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CameraMode(
    val isResolutionChecked: Boolean,
    val isZoomLevelChecked: Boolean,
    val isUseImageAnalyzerChecked: Boolean,
    val isUseMFNRImageCaptureChecked: Boolean,
){
    companion object{
        val default = CameraMode(
            isResolutionChecked = false,
            isZoomLevelChecked = false,
            isUseImageAnalyzerChecked = false,
            isUseMFNRImageCaptureChecked = false,
        )
    }
}