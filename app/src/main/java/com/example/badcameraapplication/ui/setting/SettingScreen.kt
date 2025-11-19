package com.example.badcameraapplication.ui.setting

import android.util.Size
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.badcameraapplication.domain.model.CameraMode
import com.example.badcameraapplication.domain.model.CameraState
import com.example.badcameraapplication.ui.components.BackButton
import com.example.badcameraapplication.ui.components.ScrollBar

@Composable
fun SettingScreen(
    cameraMode: CameraMode,
    onBackClick: () -> Unit,
    onNavigateToCamera: (CameraMode) -> Unit,
    viewModel: SettingViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LifecycleEventEffect(event = Lifecycle.Event.ON_START) {
        viewModel.onStart(initialCameraMode = cameraMode)
    }

    SettingScreen(
        state = state,
        onCheckLensFacingClick = viewModel::onCheckLensFacingClick,
        onCheckAspectRatioClick = viewModel::onCheckAspectRatioClick,
        onCheckResolutionClick = viewModel::onCheckResolutionClick,
        onCheckRecognizeClick = viewModel::onCheckRecognizeClick,
        onCheckMFNRClick = viewModel::onCheckMFNRClick,
        onSaveClick = { onNavigateToCamera(state.cameraMode) },
        onBackClick = onBackClick,
    )
}

@Composable
private fun SettingScreen(
    state: SettingViewModel.State,
    onCheckLensFacingClick: (Boolean) -> Unit,
    onCheckAspectRatioClick: (Boolean) -> Unit,
    onCheckResolutionClick: (Boolean) -> Unit,
    onCheckRecognizeClick: (Boolean) -> Unit,
    onCheckMFNRClick: (Boolean) -> Unit,
    onSaveClick: (CameraState) -> Unit,
    onBackClick: () -> Unit,
) {
    val scrollState = rememberScrollState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .safeDrawingPadding(),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    SettingItem(
                        title = "前面カメラ",
                        isChecked = state.cameraMode.isLensFacingChecked,
                        onCheckedChange = onCheckLensFacingClick,
                    )
                    SettingItem(
                        title = "アスペクト比16:9",
                        isChecked = state.cameraMode.isCaptureRatioChecked,
                        onCheckedChange = onCheckAspectRatioClick,
                    )
                    SettingItem(
                        title = "高解像度",
                        isChecked = state.cameraMode.isResolutionChecked,
                        onCheckedChange = onCheckResolutionClick,
                    )
                    SettingItem(
                        title = "画像認識",
                        isChecked = state.cameraMode.isUseImageAnalyzerChecked,
                        onCheckedChange = onCheckRecognizeClick,
                    )
                    SettingItem(
                        title = "キャプチャノイズ軽減",
                        isChecked = state.cameraMode.isUseMFNRImageCaptureChecked,
                        onCheckedChange = onCheckMFNRClick,
                    )
                }
                ScrollBar(scrollState = scrollState)
            }
            Button(
                modifier = Modifier.padding(16.dp),
                onClick = {
                    onSaveClick(
                        CameraState.default.copy(resolution = Size(720, 1280))
                    )
                },
            ) {
                Text(text = "設定を保存する")
            }
        }
        BackButton(
            onBackClick = onBackClick,
            modifier = Modifier.align(alignment = Alignment.TopStart)
        )
    }
}

@Composable
private fun SettingItem(
    isChecked: Boolean,
    title: String,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(8.dp),
            )
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = title)
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
        )
    }
}

@Preview
@Composable
private fun VerticalPreview() {
    SettingScreen(
        state = SettingViewModel.State.initialize(),
        onCheckLensFacingClick = {},
        onCheckAspectRatioClick = {},
        onCheckResolutionClick = {},
        onCheckRecognizeClick = {},
        onCheckMFNRClick = {},
        onSaveClick = {},
        onBackClick = {},
    )
}

@Preview(heightDp = 360, widthDp = 800)
@Composable
private fun HorizontalPreview() {
    SettingScreen(
        state = SettingViewModel.State.initialize(),
        onCheckLensFacingClick = {},
        onCheckAspectRatioClick = {},
        onCheckResolutionClick = {},
        onCheckRecognizeClick = {},
        onCheckMFNRClick = {},
        onSaveClick = {},
        onBackClick = {},
    )
}
