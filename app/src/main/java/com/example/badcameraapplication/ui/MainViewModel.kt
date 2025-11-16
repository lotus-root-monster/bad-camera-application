package com.example.badcameraapplication.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.badcameraapplication.core.performance.CpuMonitor
import com.example.badcameraapplication.core.performance.FpsMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    private val fpsMonitor = FpsMonitor(
        fetchCurrentFps = { fetchCurrentFps(it) }
    )
    private val cpuMonitor = CpuMonitor(
        fetchCpuUsage = { fetchCpuUsage(it) }
    )
    private val latestFps = MutableStateFlow(0.0)
    private val latestCpuUsage = MutableStateFlow(0L)
    val state = combine(latestFps, latestCpuUsage, ::State).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = State.initialize()
    )

    fun onStart() {
        fpsMonitor.startMonitoring()
        cpuMonitor.startMonitoring()
    }

    fun onStop() {
        fpsMonitor.stopMonitoring()
        cpuMonitor.stopMonitoring()
    }

    private fun fetchCurrentFps(currentFps: Double) {
        latestFps.value = currentFps
    }

    private fun fetchCpuUsage(currentCpuUsage: Long) {
        latestCpuUsage.value = currentCpuUsage
    }

    data class State(
        val latestFps: Double,
        val latestCpuUsage: Long,
    ) {
        companion object {
            fun initialize() = State(
                latestFps = 0.0,
                latestCpuUsage = 0L,
            )
        }
    }
}