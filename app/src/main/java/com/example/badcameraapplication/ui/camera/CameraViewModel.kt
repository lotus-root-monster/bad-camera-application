package com.example.badcameraapplication.ui.camera

import android.util.Log
import androidx.camera.core.SurfaceRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.badcameraapplication.ui.camera.util.VandalismType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor() : ViewModel() {
    private val surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)
    private val isShowWarningDialog = MutableStateFlow(false)
    private val vandalismType = MutableStateFlow<VandalismType?>(null)

    val state = combine(surfaceRequest, isShowWarningDialog, vandalismType, ::State).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = State.initialize(),
    )

    private val _event = Channel<UiEvent>(Channel.CONFLATED)
    val event = _event.receiveAsFlow()

    fun onNewSurfaceRequest(newSurfaceRequest: SurfaceRequest) {
        surfaceRequest.value = newSurfaceRequest
    }

    fun onBombClick() {
        isShowWarningDialog.value = true
        vandalismType.value = VandalismType.BOMB
    }

    fun onDestructionClick() {
        isShowWarningDialog.value = true
        vandalismType.value = VandalismType.DESTRUCTION
    }

    fun onExplosionClick() {
        isShowWarningDialog.value = true
        vandalismType.value = VandalismType.EXPLOSION
    }

    fun onDismissRequest() {
        isShowWarningDialog.value = false
        vandalismType.value = null
    }

    fun onConfirmClick() {
        isShowWarningDialog.value = false

        when (vandalismType.value) {
            VandalismType.BOMB -> onBomb()
            VandalismType.DESTRUCTION -> onDestruction()
            VandalismType.EXPLOSION -> onExplosion()
            else -> Unit
        }
    }

    fun onResetVandalism() = viewModelScope.launch {
        vandalismType.value = null
        _event.send(UiEvent.ResetVandalism)
    }

    private fun onBomb() = viewModelScope.launch {
        _event.send(UiEvent.Bomb)
    }

    private fun onDestruction() = viewModelScope.launch {
        _event.send(UiEvent.Destruction)
    }

    private fun onExplosion() {
        Log.d("hogehoge", "onExplosion")
    }

    data class State(
        val surfaceRequest: SurfaceRequest?,
        val isShowWarningDialog: Boolean,
        val vandalismType: VandalismType?,
    ) {
        companion object {
            fun initialize() = State(
                surfaceRequest = null,
                isShowWarningDialog = false,
                vandalismType = null,
            )
        }
    }

    sealed interface UiEvent {
        data object Bomb : UiEvent
        data object Destruction : UiEvent
        data object Explosion : UiEvent
        data object ResetVandalism : UiEvent
    }
}