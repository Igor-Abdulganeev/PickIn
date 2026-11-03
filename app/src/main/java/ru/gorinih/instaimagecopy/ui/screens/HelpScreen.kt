package ru.gorinih.instaimagecopy.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.gorinih.instaimagecopy.R
import ru.gorinih.instaimagecopy.ui.theme.InstaImageCopyTheme

/**
 * Created by Igor Abdulganeev on 02.11.2026
 */

@Composable
fun HelpScreen() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(size = 20.dp)
            )
    ) {
        Text(
            text = stringResource(R.string.help_text),
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Justify
        )
    }
}

@Preview
@Composable
fun HelpScreenPreview() {
    InstaImageCopyTheme {
        HelpScreen()
    }
}