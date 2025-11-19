package com.example.badcameraapplication.domain.model

import android.graphics.Rect

data class Image(
    val faces: List<Rect>,
    val width: Int,
    val height: Int
)