package com.example.badcameraapplication.ui.camera.util

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
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
    currentVandalismType: VandalismType?,
    isVertical: Boolean,
    onCameraClick: () -> Unit,
    onBombClick: () -> Unit,
    onDestructionClick: () -> Unit,
    onExplosionClick: () -> Unit,
    onCancelVandalism: () -> Unit,
) {
    val scrollState = rememberScrollState()
    val canScrollBackward by remember { derivedStateOf { scrollState.canScrollBackward } }
    val canScrollForward by remember { derivedStateOf { scrollState.canScrollForward } }

    if (isVertical) {
        ForVerticalLayout(
            currentVandalismType = currentVandalismType,
            scrollState = scrollState,
            canScrollBackward = canScrollBackward,
            canScrollForward = canScrollForward,
            onCameraClick = onCameraClick,
            onBombClick = onBombClick,
            onDestructionClick = onDestructionClick,
            onExplosionClick = onExplosionClick,
            onCancelVandalism = onCancelVandalism,
        )
    } else {
        ForHorizontalLayout(
            currentVandalismType = currentVandalismType,
            scrollState = scrollState,
            canScrollBackward = canScrollBackward,
            canScrollForward = canScrollForward,
            onCameraClick = onCameraClick,
            onBombClick = onBombClick,
            onDestructionClick = onDestructionClick,
            onExplosionClick = onExplosionClick,
            onCancelVandalism = onCancelVandalism,
        )
    }
}

@Composable
private fun BoxScope.ForVerticalLayout(
    currentVandalismType: VandalismType?,
    scrollState: ScrollState,
    canScrollBackward: Boolean,
    canScrollForward: Boolean,
    onCameraClick: () -> Unit,
    onBombClick: () -> Unit,
    onDestructionClick: () -> Unit,
    onExplosionClick: () -> Unit,
    onCancelVandalism: () -> Unit,
) {
    Box(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .safeDrawingPadding(),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
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
            CameraButtons(
                currentVandalismType = currentVandalismType,
                onCameraClick = onCameraClick,
                onBombClick = onBombClick,
                onDestructionClick = onDestructionClick,
                onExplosionClick = onExplosionClick,
                onCancelVandalism = onCancelVandalism,
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
    currentVandalismType: VandalismType?,
    scrollState: ScrollState,
    canScrollBackward: Boolean,
    canScrollForward: Boolean,
    onCameraClick: () -> Unit,
    onBombClick: () -> Unit,
    onDestructionClick: () -> Unit,
    onExplosionClick: () -> Unit,
    onCancelVandalism: () -> Unit,
) {
    Box(
        modifier = Modifier
            .align(Alignment.CenterEnd)
            .fillMaxHeight()
            .safeDrawingPadding(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.10f))
                .border(
                    width = 2.dp,
                    color = Color.White.copy(alpha = 0.5f),
                    shape = CircleShape,
                )
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            CameraButtons(
                currentVandalismType = currentVandalismType,
                onCameraClick = onCameraClick,
                onBombClick = onBombClick,
                onDestructionClick = onDestructionClick,
                onExplosionClick = onExplosionClick,
                onCancelVandalism = onCancelVandalism,
            )
            Spacer(modifier = Modifier.height(32.dp))
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
    currentVandalismType: VandalismType?,
    onCameraClick: () -> Unit,
    onBombClick: () -> Unit,
    onDestructionClick: () -> Unit,
    onExplosionClick: () -> Unit,
    onCancelVandalism: () -> Unit,
) {
    val cancelButton = remember {
        CameraButton(
            currentVandalismType = null,
            iconResId = R.drawable.ic_cancel,
            contentDescription = "停止",
            backgroundColor = Color(0xFF80FF80),
            onClick = onCancelVandalism,
        )
    }

    cameraButtons(
        onCameraCLick = onCameraClick,
        onBombClick = onBombClick,
        onDestructionClick = onDestructionClick,
        onExplosionClick = onExplosionClick,
    ).forEach {
        val isSelected = currentVandalismType != null &&
                it.currentVandalismType == currentVandalismType
        val buttonParam = if (isSelected) cancelButton else it

        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            IconButton(
                onClick = buttonParam.onClick,
                modifier = Modifier
                    .size(100.dp)
                    .border(
                        width = 3.dp,
                        color = Color.Gray,
                        shape = CircleShape,
                    )
                    .dropShadow(
                        shape = CircleShape,
                        shadow = Shadow(
                            radius = 10.dp,
                            color = Color.Black.copy(alpha = 0.25f),
                        )
                    ),
                shape = CircleShape,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = buttonParam.backgroundColor,
                    contentColor = Color(0xFF323232),
                    disabledContainerColor = Color.DarkGray,
                    disabledContentColor = Color.Gray,
                ),
                enabled = currentVandalismType == null || isSelected
            ) {
                Icon(
                    painter = painterResource(buttonParam.iconResId),
                    contentDescription = buttonParam.contentDescription,
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxSize(),
                )
            }
            Text(text = it.contentDescription)
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
        currentVandalismType = null,
        iconResId = R.drawable.ic_camera,
        contentDescription = "写真撮影",
        backgroundColor = Color.White,
        onClick = onCameraCLick,
    ),
    CameraButton(
        currentVandalismType = VandalismType.BOMB,
        iconResId = R.drawable.ic_bomb,
        contentDescription = "爆弾",
        backgroundColor = Color(0xFFFF8080),
        onClick = onBombClick,
    ),
    CameraButton(
        currentVandalismType = VandalismType.DESTRUCTION,
        iconResId = R.drawable.ic_destruction,
        contentDescription = "破壊",
        backgroundColor = Color(0xFFFF8080),
        onClick = onDestructionClick,
    ),
    CameraButton(
        currentVandalismType = VandalismType.EXPLOSION,
        iconResId = R.drawable.ic_explosion,
        contentDescription = "最後の輝き",
        backgroundColor = Color(0xFFFF8080),
        onClick = onExplosionClick,
    )
)

@Preview
@Composable
private fun ForVerticalPreview() {
    Box {
        ForVerticalLayout(
            currentVandalismType = null,
            scrollState = ScrollState(0),
            canScrollBackward = true,
            canScrollForward = true,
            onCameraClick = {},
            onBombClick = {},
            onDestructionClick = {},
            onExplosionClick = {},
            onCancelVandalism = {},
        )
    }
}

@Preview(heightDp = 400, showBackground = true)
@Composable
private fun ForHorizontalPreview() {
    Box {
        ForHorizontalLayout(
            currentVandalismType = null,
            scrollState = ScrollState(0),
            canScrollBackward = true,
            canScrollForward = true,
            onCameraClick = {},
            onBombClick = {},
            onDestructionClick = {},
            onExplosionClick = {},
            onCancelVandalism = {},
        )
    }
}

@Preview
@Composable
private fun SelectedPreview() {
    Box {
        ForVerticalLayout(
            currentVandalismType = VandalismType.BOMB,
            scrollState = ScrollState(0),
            canScrollBackward = true,
            canScrollForward = true,
            onCameraClick = {},
            onBombClick = {},
            onDestructionClick = {},
            onExplosionClick = {},
            onCancelVandalism = {},
        )
    }
}