package ru.gorinih.instaimagecopy.ui.models

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Created by Igor Abdulganeev on 03.11.2026
 */

data class ImagePreview(
    val sharedTransitionScope: SharedTransitionScope,
    val animatedVisibilityScope: AnimatedVisibilityScope,
    val selectedPreviewUrl: String,
    val onLongClick: (String) -> Unit
)

val LocalImagePreview = staticCompositionLocalOf<ImagePreview?> { null }
