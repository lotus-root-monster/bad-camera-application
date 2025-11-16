package com.example.badcameraapplication.core.performance

import android.app.ActivityManager
import android.content.Context
import android.os.Handler
import android.os.Looper

class MemoryMonitor(
    private val context: Context,
    private val fetchRamUsage: (Double) -> Unit,
) {
    private lateinit var activityManager: ActivityManager
    private val handler = Handler(Looper.getMainLooper())

    private val memoryUpdateRunnable = object : Runnable {
        override fun run() {
            logDeviceMemoryUsage()
            handler.postDelayed(this, UPDATE_INTERVAL_MS)
        }
    }

    fun startMonitoring() {
        activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        handler.post(memoryUpdateRunnable)
    }

    fun stopMonitoring() {
        handler.removeCallbacks(memoryUpdateRunnable)
    }

    private fun logDeviceMemoryUsage() {
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        val totalRamMb = memoryInfo.totalMem / (KIB * KIB)
        val availableRamMb = memoryInfo.availMem / (KIB * KIB)
        val usedRamMb = totalRamMb - availableRamMb
        val usagePercentage = (usedRamMb / totalRamMb) * 100.0

        fetchRamUsage(usagePercentage)
    }

    companion object {
        private const val UPDATE_INTERVAL_MS = 1000L
        private const val KIB = 1024.0
    }
}