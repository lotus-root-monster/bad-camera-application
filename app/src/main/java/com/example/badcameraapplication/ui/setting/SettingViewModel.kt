package com.example.badcameraapplication.ui.setting

import android.util.Log
import androidx.camera.core.AspectRatio
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.badcameraapplication.domain.model.CameraState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor() : ViewModel() {
    private val cameraState = MutableStateFlow(CameraState.default)
    val state: StateFlow<CameraState> = cameraState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CameraState.default,
    )

    init {
        viewModelScope.launch {
            while (isActive) {
                delay(3000L)
                Log.d("hogehoge", "ｄｋんｃｆんｊｄ")
                updateSettings(
                    cameraState.value.copy(
                        useImageAnalyzer = true,
                    )
                )
            }
        }
    }

    // 設定を変更する関数（UIから呼び出される）
    fun updateSettings(newState: CameraState) {
        cameraState.value = newState
    }

    // 例: アスペクト比を切り替える
    fun toggleAspectRatio() {
        val currentRatio = cameraState.value.captureRatio
        val newRatio =
            if (currentRatio == AspectRatio.RATIO_4_3) AspectRatio.RATIO_16_9 else AspectRatio.RATIO_4_3
        cameraState.update { it.copy(captureRatio = newRatio) }
    }

    // 例: ズームレベルを変更
    fun setZoom(level: Float) {
        cameraState.update { it.copy(zoomLevel = level.coerceIn(1.0f, 5.0f)) } // 範囲制限
    }
}