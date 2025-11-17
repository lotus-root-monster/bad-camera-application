package com.example.badcameraapplication.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

private val scrollBarWidth = 4.dp

@Composable
fun BoxScope.ScrollBar(
    modifier: Modifier = Modifier,
    scrollState: ScrollState,
    isAlwaysShowScrollBar: Boolean = false,
) {
    var isVisible by remember { mutableStateOf(isAlwaysShowScrollBar) }

    LaunchedEffect(isAlwaysShowScrollBar, scrollState.isScrollInProgress) {
        isVisible = if (isAlwaysShowScrollBar || scrollState.isScrollInProgress) {
            true
        } else {
            delay(800)
            false
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Canvas(
            modifier = modifier
                .align(Alignment.CenterEnd)
                .fillMaxSize()
        ) {
            val totalScrollDistance = scrollState.maxValue.toFloat()
            val viewHeight = size.height
            val scrollRatio = scrollState.value.toFloat() / totalScrollDistance
            val scrollbarHeight = viewHeight * (viewHeight / (totalScrollDistance + viewHeight))
            val scrollbarTopOffset = scrollRatio * (viewHeight - scrollbarHeight)

            drawRect(
                color = Color.Gray,
                topLeft = Offset(size.width - scrollBarWidth.toPx(), scrollbarTopOffset),
                size = Size(scrollBarWidth.toPx(), scrollbarHeight)
            )
        }
    }
}
