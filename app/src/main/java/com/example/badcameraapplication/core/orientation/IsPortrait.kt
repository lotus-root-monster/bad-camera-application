package com.example.badcameraapplication.core.orientation

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

/**
 * 現在の画面が縦向きかどうかを判定する
 */
@Composable
fun isPortrait(): Boolean =
    LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
