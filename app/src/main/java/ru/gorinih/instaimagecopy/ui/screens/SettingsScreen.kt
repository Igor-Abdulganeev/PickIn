package ru.gorinih.instaimagecopy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Created by Igor Abdulganeev on 21.10.2026
 */

@Composable
fun SettingsScreen() {
    Column(
        modifier = Modifier.fillMaxWidth()
            .background(color = Color.Cyan)
    ) {
        Text(text = "Настройки")
    }
}