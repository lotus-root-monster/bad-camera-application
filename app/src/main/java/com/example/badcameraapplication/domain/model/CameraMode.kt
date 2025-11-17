package com.example.badcameraapplication.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CameraMode(
    val isLensFacingChecked: Boolean,
    val isCaptureRatioChecked: Boolean,
    val isResolutionChecked: Boolean,
    val isZoomLevelChecked: Boolean,
    val isUseImageAnalyzerChecked: Boolean,
){
    companion object{
        val default = CameraMode(
            isLensFacingChecked = false,
            isCaptureRatioChecked = false,
            isResolutionChecked = false,
            isZoomLevelChecked = false,
            isUseImageAnalyzerChecked = false,
        )
    }
}