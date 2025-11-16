package com.example.badcameraapplication.core.performance

import android.view.Choreographer

class FpsMonitor(private val fetchCurrentFps: (Double) -> Unit) {
    private var lastFrameTimeNanos: Long = 0
    private var frameCount = 0
    private var lastLogTimeMillis: Long = 0

    private val frameCallback = object : Choreographer.FrameCallback {
        override fun doFrame(frameTimeNanos: Long) {
            frameCount++
            if (lastLogTimeMillis == 0L) {
                lastLogTimeMillis = System.currentTimeMillis()
            }

            if (System.currentTimeMillis() - lastLogTimeMillis >= 1000) {
                val fps = frameCount / ((System.currentTimeMillis() - lastLogTimeMillis) / 1000.0)
                fetchCurrentFps(fps)
                frameCount = 0
                lastLogTimeMillis = System.currentTimeMillis()
            }
            Choreographer.getInstance().postFrameCallback(this)
        }
    }

    fun startMonitoring() {
        Choreographer.getInstance().postFrameCallback(frameCallback)
    }

    fun stopMonitoring() {
        Choreographer.getInstance().removeFrameCallback(frameCallback)
        lastFrameTimeNanos = 0
        frameCount = 0
        lastLogTimeMillis = 0
    }
}