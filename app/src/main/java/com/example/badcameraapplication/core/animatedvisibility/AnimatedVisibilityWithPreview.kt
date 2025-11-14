package com.example.badcameraapplication.core.animatedvisibility

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode

@Composable
fun AnimatedVisibilityWithPreview(
    visible: Boolean,
    modifier: Modifier = Modifier,
    enter: EnterTransition = fadeIn() + expandHorizontally(),
    exit: ExitTransition = fadeOut() + shrinkHorizontally(),
    content: @Composable () -> Unit,
) {
    if (LocalInspectionMode.current) {
        Box(modifier = modifier) { content() }
    } else {
        AnimatedVisibility(
            visible = visible,
            modifier = modifier,
            enter = enter,
            exit = exit,
        ) {
            content()
        }
    }
}