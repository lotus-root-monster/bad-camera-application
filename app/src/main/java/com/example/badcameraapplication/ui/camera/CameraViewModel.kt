package com.example.badcameraapplication.ui.camera

import androidx.camera.core.SurfaceRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.badcameraapplication.domain.model.CameraMode
import com.example.badcameraapplication.domain.model.Image
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor() : ViewModel() {
    private val surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)
    private val isShowWarningDialog = MutableStateFlow(false)
    private val cameraMode = MutableStateFlow<CameraMode?>(null)
    private val cameraLoadingState = MutableStateFlow<CameraLoadingState>(
        CameraLoadingState.Initial
    )
    private val faceImage = MutableStateFlow<Image?>(null)

    val state = combine(
        surfaceRequest,
        isShowWarningDialog,
        cameraMode,
        cameraLoadingState,
        faceImage,
        ::State
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = State.initialize(),
    )

    fun onStart(initialCameraMode: CameraMode?) {
        if (initialCameraMode != null) {
            cameraMode.value = initialCameraMode
        }
    }

    fun onNewSurfaceRequest(newSurfaceRequest: SurfaceRequest) {
        surfaceRequest.value = newSurfaceRequest
    }

    fun onStartCapture() {
        cameraLoadingState.value = CameraLoadingState.Loading
    }

    fun onCompleteCapture() {
        cameraLoadingState.value = CameraLoadingState.Initial
    }

    fun onSmileDetect(inputFaceImage: Image) {
        faceImage.value = inputFaceImage
    }

    data class State(
        val surfaceRequest: SurfaceRequest?,
        val isShowWarningDialog: Boolean,
        val cameraMode: CameraMode?,
        val cameraLoadingState: CameraLoadingState,
        val faceImage: Image?,
    ) {
        companion object {
            fun initialize() = State(
                surfaceRequest = null,
                isShowWarningDialog = false,
                cameraMode = null,
                cameraLoadingState = CameraLoadingState.Initial,
                faceImage = null,
            )
        }
    }

    sealed interface CameraLoadingState {
        data object Initial : CameraLoadingState
        data object Loading : CameraLoadingState
    }
}