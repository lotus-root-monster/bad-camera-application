package com.example.badcameraapplication.core.startup

import android.content.Context
import androidx.startup.Initializer
import org.opencv.android.OpenCVLoader

class OpenCVInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        OpenCVLoader.initLocal()
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> = mutableListOf()
}