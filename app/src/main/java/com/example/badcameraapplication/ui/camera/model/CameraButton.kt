package com.example.badcameraapplication.ui.camera.model
import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.example.badcameraapplication.ui.camera.util.VandalismType

data class CameraButton(
    val currentVandalismType: VandalismType?,
    @param:DrawableRes val iconResId: Int,
    val contentDescription: String,
    val backgroundColor: Color,
    val onClick: () -> Unit,
)