package ru.gorinih.instaimagecopy.ui.views

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ElevatedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

/**
 * Created by Igor Abdulganeev on 05.11.2026
 */

@Composable
fun ElevateButton(
    modifier: Modifier = Modifier,
    text: @Composable RowScope.() -> Unit,
    onSavedClick: () -> Unit,
    pause: Int = 200,
    enabled: Boolean = true,
) {
    var delayClick by remember { mutableLongStateOf(0L) }
    ElevatedButton(
        modifier = modifier,
        onClick = {
            val currentTime = System.currentTimeMillis()
            if (currentTime > delayClick) {
                delayClick = currentTime + pause
                onSavedClick()
            }
        },
        enabled = enabled,
        content = text
    )
}

