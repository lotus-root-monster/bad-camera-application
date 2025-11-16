package com.example.badcameraapplication.core.performance

import android.os.Handler
import android.os.Looper
import android.os.Process
import android.util.Log
import java.io.File

class CpuMonitor(private val fetchCpuUsage: (Long) -> Unit) {
    private var lastAppCpuTime: Long = 0
    private val handler = Handler(Looper.getMainLooper())
    private val numCores = Runtime.getRuntime().availableProcessors()

    fun startMonitoring() {
        handler.post(cpuUpdateRunnable)
    }

    fun stopMonitoring() {
        handler.removeCallbacks(cpuUpdateRunnable)
        lastAppCpuTime = 0
    }

    private val cpuUpdateRunnable = object : Runnable {
        override fun run() {
            try {
                val appCpuTime = getAppCpuTime()
                if (lastAppCpuTime > 0) {
                    val jiffiesPerSecond = 100L
                    val totalJiffiesInInterval =
                        (UPDATE_INTERVAL_MS / 1000) * jiffiesPerSecond * numCores

                    if (totalJiffiesInInterval > 0) {
                        val appDiff = appCpuTime - lastAppCpuTime
                        val usage = (appDiff * 100) / totalJiffiesInInterval
                        fetchCpuUsage(usage)
                    }
                }
                lastAppCpuTime = appCpuTime
            } catch (e: Exception) {
                Log.e("CpuMonitor", "CPU使用率を計算しようとしたけどエラーになった", e)
            } finally {
                handler.postDelayed(this, UPDATE_INTERVAL_MS)
            }
        }
    }

    private fun getAppCpuTime(): Long {
        return try {
            val pid = Process.myPid()
            val statText = File("/proc/$pid/stat").bufferedReader().use { it.readLine() }
            val tokens = statText.split("\\s+".toRegex())

            val uTime = tokens[13].toLongOrNull() ?: 0
            val sTime = tokens[14].toLongOrNull() ?: 0
            val cUTime = tokens[15].toLongOrNull() ?: 0
            val cSTime = tokens[16].toLongOrNull() ?: 0

            uTime + sTime + cUTime + cSTime
        } catch (e: Exception) {
            0
        }
    }

    companion object {
        private const val UPDATE_INTERVAL_MS = 1000L
    }
}
