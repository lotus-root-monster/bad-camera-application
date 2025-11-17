package com.example.badcameraapplication.ui.camera

import androidx.camera.core.SurfaceRequest
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
class CameraViewModel @Inject constructor() : ViewModel() {
    private val surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)
    private val isShowWarningDialog = MutableStateFlow(false)
    private val cameraMode = MutableStateFlow<CameraMode?>(null)

    val state = combine(
        surfaceRequest,
        isShowWarningDialog,
        cameraMode,
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

    data class State(
        val surfaceRequest: SurfaceRequest?,
        val isShowWarningDialog: Boolean,
        val cameraMode: CameraMode?
    ) {
        companion object {
            fun initialize() = State(
                surfaceRequest = null,
                isShowWarningDialog = false,
                cameraMode = null,
            )
        }
    }
}