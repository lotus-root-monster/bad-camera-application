package com.example.badcameraapplication.ui.camera.util

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.badcameraapplication.R
import com.example.badcameraapplication.core.animatedvisibility.AnimatedVisibilityWithPreview
import com.example.badcameraapplication.ui.camera.model.CameraButton

@Composable
fun BoxScope.CameraButtonsLayout(
    isVertical: Boolean,
    onCameraClick: () -> Unit,
    onBombClick: () -> Unit,
    onDestructionClick: () -> Unit,
    onExplosionClick: () -> Unit,
) {
    val scrollState = rememberScrollState()
    val canScrollBackward by remember {
        derivedStateOf { scrollState.canScrollBackward }
    }
    val canScrollForward by remember {
        derivedStateOf { scrollState.canScrollForward }
    }

    if (isVertical) {
        ForVerticalLayout(
            scrollState = scrollState,
            canScrollBackward = canScrollBackward,
            canScrollForward = canScrollForward,
            onCameraClick = onCameraClick,
            onBombClick = onBombClick,
            onDestructionClick = onDestructionClick,
            onExplosionClick = onExplosionClick,
        )
    } else {
        ForHorizontalLayout(
            scrollState = scrollState,
            canScrollBackward = canScrollBackward,
            canScrollForward = canScrollForward,
            onCameraClick = onCameraClick,
            onBombClick = onBombClick,
            onDestructionClick = onDestructionClick,
            onExplosionClick = onExplosionClick,
        )
    }
}

@Composable
private fun BoxScope.ForVerticalLayout(
    scrollState: ScrollState,
    canScrollBackward: Boolean,
    canScrollForward: Boolean,
    onCameraClick: () -> Unit,
    onBombClick: () -> Unit,
    onDestructionClick: () -> Unit,
    onExplosionClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .safeDrawingPadding(),
        contentAlignment = Alignment.Center,
    ) {
        Row(modifier = Modifier.horizontalScroll(scrollState)) {
            Spacer(modifier = Modifier.width(32.dp))
            CameraButtons(
                onCameraClick = onCameraClick,
                onBombClick = onBombClick,
                onDestructionClick = onDestructionClick,
                onExplosionClick = onExplosionClick,
            )
            Spacer(modifier = Modifier.width(32.dp))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AnimatedVisibilityWithPreview(
                visible = canScrollBackward,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_left),
                    contentDescription = "左にスクロール可能",
                    modifier = Modifier.arrowModifier()
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            AnimatedVisibilityWithPreview(
                visible = canScrollForward,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_right),
                    contentDescription = "右にスクロール可能",
                    modifier = Modifier.arrowModifier()
                )
            }
        }
    }
}

@Composable
private fun BoxScope.ForHorizontalLayout(
    scrollState: ScrollState,
    canScrollBackward: Boolean,
    canScrollForward: Boolean,
    onCameraClick: () -> Unit,
    onBombClick: () -> Unit,
    onDestructionClick: () -> Unit,
    onExplosionClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .align(Alignment.CenterEnd)
            .fillMaxHeight()
            .safeDrawingPadding(),
        contentAlignment = Alignment.Center,
    ) {
        Column(modifier = Modifier.verticalScroll(scrollState)) {
            Spacer(modifier = Modifier.width(32.dp))
            CameraButtons(
                onCameraClick = onCameraClick,
                onBombClick = onBombClick,
                onDestructionClick = onDestructionClick,
                onExplosionClick = onExplosionClick,
            )
            Spacer(modifier = Modifier.width(32.dp))
        }
        Column(
            modifier = Modifier.fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AnimatedVisibilityWithPreview(
                visible = canScrollBackward,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_up),
                    contentDescription = "上にスクロール可能",
                    modifier = Modifier.arrowModifier()
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            AnimatedVisibilityWithPreview(
                visible = canScrollForward,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_down),
                    contentDescription = "下にスクロール可能",
                    modifier = Modifier.arrowModifier()
                )
            }
        }
    }
}

private fun Modifier.arrowModifier() = this
    .size(64.dp)
    .dropShadow(
        shape = CircleShape,
        shadow = Shadow(
            radius = 10.dp,
            color = Color.White.copy(0.25f)
        )
    )

@Composable
private fun CameraButtons(
    onCameraClick: () -> Unit,
    onBombClick: () -> Unit,
    onDestructionClick: () -> Unit,
    onExplosionClick: () -> Unit,
) {
    cameraButtons(
        onCameraCLick = onCameraClick,
        onBombClick = onBombClick,
        onDestructionClick = onDestructionClick,
        onExplosionClick = onExplosionClick,
    ).forEach {
        IconButton(
            onClick = it.onClick,
            modifier = Modifier
                .padding(16.dp)
                .size(100.dp)
                .clip(CircleShape)
                .background(it.backgroundColor)
        ) {
            Icon(
                painter = painterResource(it.iconResId),
                contentDescription = it.contentDescription,
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxSize(),
                tint = Color.Black.copy(alpha = 0.5f),
            )
        }
    }
}

private fun cameraButtons(
    onCameraCLick: () -> Unit,
    onBombClick: () -> Unit,
    onDestructionClick: () -> Unit,
    onExplosionClick: () -> Unit,
) = listOf(
    CameraButton(
        iconResId = R.drawable.ic_camera,
        contentDescription = "カメラ",
        backgroundColor = Color.White.copy(alpha = 0.5f),
        onClick = onCameraCLick,
    ),
    CameraButton(
        iconResId = R.drawable.ic_bomb,
        contentDescription = "爆弾",
        backgroundColor = Color.Red.copy(alpha = 0.25f),
        onClick = onBombClick,
    ),
    CameraButton(
        iconResId = R.drawable.ic_destruction,
        contentDescription = "破壊",
        backgroundColor = Color.Red.copy(alpha = 0.25f),
        onClick = onDestructionClick,
    ),
    CameraButton(
        iconResId = R.drawable.ic_explosion,
        contentDescription = "最後の輝き",
        backgroundColor = Color.Red.copy(alpha = 0.25f),
        onClick = onExplosionClick,
    )
)

@Preview
@Composable
private fun ForVerticalPreview() {
    Box {
        ForVerticalLayout(
            scrollState = ScrollState(0),
            canScrollBackward = true,
            canScrollForward = true,
            onCameraClick = {},
            onBombClick = {},
            onDestructionClick = {},
            onExplosionClick = {},
        )
    }
}

@Preview(heightDp = 400)
@Composable
private fun ForHorizontalPreview() {
    Box {
        ForHorizontalLayout(
            scrollState = ScrollState(0),
            canScrollBackward = true,
            canScrollForward = true,
            onCameraClick = {},
            onBombClick = {},
            onDestructionClick = {},
            onExplosionClick = {},
        )
    }
}