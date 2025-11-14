package com.example.badcameraapplication.ui.camera.model
import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color

data class CameraButton(
    @param:DrawableRes val iconResId: Int,
    val contentDescription: String,
    val backgroundColor: Color,
    val onClick: () -> Unit,
)