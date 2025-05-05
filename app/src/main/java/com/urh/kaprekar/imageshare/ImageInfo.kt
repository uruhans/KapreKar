package com.urh.kaprekar.imageshare

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Density

data class ImageInfo (
    val currentActivity: Activity,
    val screenDensity: Density,
    val composableView: @Composable () -> Unit
)