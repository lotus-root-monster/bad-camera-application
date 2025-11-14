package com.example.badcameraapplication.ui.camera.model

import androidx.annotation.DrawableRes

data class CameraButton(
    @param:DrawableRes val iconResId: Int,
    val contentDescription: String,
    val onClick: () -> Unit,
)