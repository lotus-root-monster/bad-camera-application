package com.example.badcameraapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.badcameraapplication.R

@Composable
fun BackButton(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
){
    IconButton(
        onClick = onBackClick,
        modifier = modifier
            .padding(4.dp)
            .size(56.dp)
            .background(
                color = Color.White.copy(alpha = 0.75f),
                shape = CircleShape,
            )
            .border(
                width = 2.dp,
                color = Color.Black,
                shape = CircleShape,
            )
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_back),
            contentDescription = "戻る",
        )
    }
}