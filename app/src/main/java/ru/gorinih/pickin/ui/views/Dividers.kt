package ru.gorinih.pickin.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Created by Igor Abdulganeev on 29.10.2026
 */

@Composable
fun DividerHorizontalTransparent(modifier: Modifier = Modifier) {
    val brush = Brush.linearGradient(
        colors = listOf(
            Color.Transparent,
            MaterialTheme.colorScheme.outlineVariant,
            Color.Transparent,
        )
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(2.dp)
            .background(brush = brush)
            .padding(horizontal = 32.dp)
    )

}