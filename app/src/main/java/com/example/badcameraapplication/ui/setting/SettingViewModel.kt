package com.example.badcameraapplication.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.badcameraapplication.domain.model.CameraMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor() : ViewModel() {
    private val isLensFacingChecked = MutableStateFlow(false)
    private val isCaptureRatioChecked = MutableStateFlow(false)
    private val isResolutionChecked = MutableStateFlow(false)
    private val isZoomLevelChecked = MutableStateFlow(false)
    private val isUseImageAnalyzerChecked = MutableStateFlow(false)

    val state = combine(
        isLensFacingChecked,
        isCaptureRatioChecked,
        isResolutionChecked,
        isZoomLevelChecked,
        isUseImageAnalyzerChecked,
        ::State
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = State.initialize(),
    )

    fun onStart(initialCameraMode: CameraMode) {
        isLensFacingChecked.value = initialCameraMode.isLensFacingChecked
        isCaptureRatioChecked.value = initialCameraMode.isCaptureRatioChecked
        isResolutionChecked.value = initialCameraMode.isResolutionChecked
        isZoomLevelChecked.value = initialCameraMode.isZoomLevelChecked
        isUseImageAnalyzerChecked.value = initialCameraMode.isUseImageAnalyzerChecked
    }

    fun onCheckLensFacingClick(isCheck: Boolean) {
        isLensFacingChecked.value = isCheck
    }

    fun onCheckAspectRatioClick(isCheck: Boolean) {
        isCaptureRatioChecked.value = isCheck
    }

    fun onCheckResolutionClick(isCheck: Boolean) {
        isResolutionChecked.value = isCheck
    }

    fun onCheckZoomClick(isCheck: Boolean) {
        isZoomLevelChecked.value = isCheck
    }

    fun onCheckRecognizeClick(isCheck: Boolean) {
        isUseImageAnalyzerChecked.value = isCheck
    }

    data class State(
        val isLensFacingChecked: Boolean,
        val isCaptureRatioChecked: Boolean,
        val isResolutionChecked: Boolean,
        val isZoomLevelChecked: Boolean,
        val isUseImageAnalyzerChecked: Boolean,
    ) {
        companion object {
            fun initialize() = State(
                isLensFacingChecked = false,
                isCaptureRatioChecked = false,
                isResolutionChecked = false,
                isZoomLevelChecked = false,
                isUseImageAnalyzerChecked = false,
            )
        }
    }
}