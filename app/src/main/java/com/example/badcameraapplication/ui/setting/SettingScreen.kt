package com.example.badcameraapplication.ui.setting

import android.util.Size
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.badcameraapplication.domain.model.CameraState


@Composable
fun SettingScreen(
    onNavigateToCamera: (CameraState) -> Unit,
    viewModel: SettingViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SettingScreen(
        onSaveClick = onNavigateToCamera,
    )
}

@Composable
private fun SettingScreen(
    onSaveClick: (CameraState) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = {
            onSaveClick(
                CameraState.default.copy(
                    resolution = Size(
                        720,
                        1280,
                    )
                )
            )
        }) {
            Text(text = "設定を保存する")
        }
    }
}