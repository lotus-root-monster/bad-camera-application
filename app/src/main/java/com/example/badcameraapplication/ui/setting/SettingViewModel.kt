package com.example.badcameraapplication.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.badcameraapplication.domain.model.CameraMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor() : ViewModel() {
    private val cameraMode = MutableStateFlow(CameraMode.default)

    val state = cameraMode.map(::State).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = State.initialize(),
    )

    fun onStart(initialCameraMode: CameraMode) {
        cameraMode.value = initialCameraMode
    }

    fun onCheckResolutionClick(isCheck: Boolean) {
        cameraMode.value = cameraMode.value.copy(
            isResolutionChecked = isCheck
        )
    }

    fun onCheckRecognizeClick(isCheck: Boolean) {
        cameraMode.value = cameraMode.value.copy(
            isUseImageAnalyzerChecked = isCheck
        )
    }

    fun onCheckMFNRClick(isCheck: Boolean) {
        cameraMode.value = cameraMode.value.copy(
            isUseMFNRImageCaptureChecked = isCheck
        )
    }

    data class State(
        val cameraMode: CameraMode
    ) {
        companion object {
            fun initialize() = State(
                cameraMode = CameraMode.default
            )
        }
    }
}