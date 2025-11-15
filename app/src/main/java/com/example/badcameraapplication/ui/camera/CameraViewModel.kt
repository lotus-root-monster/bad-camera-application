package com.example.badcameraapplication.ui.camera

import android.widget.Toast
import androidx.camera.core.SurfaceRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    val state = combine(surfaceRequest, isShowWarningDialog, ::State).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = State.initialize(),
    )

    fun onNewSurfaceRequest(newSurfaceRequest: SurfaceRequest) {
        surfaceRequest.value = newSurfaceRequest
    }

    fun onBombClick() {
        isShowWarningDialog.value = true
    }

    fun onDestructionClick() {
        isShowWarningDialog.value = true
    }

    fun onExplosionClick() {
        isShowWarningDialog.value = true
    }

    fun onDismissRequest() {
        isShowWarningDialog.value = false
    }

    fun onConfirmClick(){
        isShowWarningDialog.value = false
    }

    data class State(
        val surfaceRequest: SurfaceRequest?,
        val isShowWarningDialog: Boolean,
    ) {
        companion object {
            fun initialize() = State(
                surfaceRequest = null,
                isShowWarningDialog = false,
            )
        }
    }
}