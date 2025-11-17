package com.example.badcameraapplication.ui.camera.util

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.badcameraapplication.R

@Composable
fun BoxScope.CameraButtonsLayout(
    onNavigateToSettingClick: () -> Unit,
    onCameraClick: () -> Unit,
) {
    val scrollState = rememberScrollState()
    Row(
        modifier = Modifier
            .safeDrawingPadding()
            .align(Alignment.BottomCenter)
            .horizontalScroll(scrollState)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.10f))
            .border(
                width = 2.dp,
                color = Color.White.copy(alpha = 0.5f),
                shape = CircleShape,
            )
    ) {
        Spacer(modifier = Modifier.width(32.dp))
        Button(
            iconResId = R.drawable.ic_camera,
            contentDescription = "写真撮影",
            onClick = onCameraClick,
        )
        Button(
            iconResId = R.drawable.ic_settings,
            contentDescription = "カメラ設定",
            onClick = onNavigateToSettingClick,
        )
        Spacer(modifier = Modifier.width(32.dp))
    }
}

@Composable
private fun Button(
    @DrawableRes iconResId: Int,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(100.dp)
                .border(
                    width = 3.dp,
                    color = Color.Gray,
                    shape = CircleShape,
                ),
            shape = CircleShape,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color.White.copy(alpha = 0.75f),
            ),
        ) {
            Icon(
                painter = painterResource(iconResId),
                contentDescription = contentDescription,
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxSize(),
            )
        }
    }
}

@Preview
@Composable
private fun VerticalPreview() {
    Box(modifier = Modifier.fillMaxSize()) {
        CameraButtonsLayout(
            onNavigateToSettingClick = {},
            onCameraClick = {},
        )
    }
}

@Preview(
    heightDp = 360,
    widthDp = 800,
)
@Composable
private fun HorizontalPreview() {
    Box(modifier = Modifier.fillMaxSize()) {
        CameraButtonsLayout(
            onNavigateToSettingClick = {},
            onCameraClick = {},
        )
    }
}
