package ru.gorinih.instaimagecopy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import ru.gorinih.instaimagecopy.ui.theme.InstaImageCopyTheme
import java.nio.file.WatchEvent

/**
 * Created by Igor Abdulganeev on 21.10.2026
 */

@Composable
fun SettingsScreen(
    selectedDownloadType: () -> Int,
    selectedMediaType: () -> Int,
    onClickDownloadType: (Int) -> Unit,
    onClickMediaType: (Int) -> Unit,
) {
    val listOfDownloadTypes = listOf("Автоскачивание", "Предпросмотр").toImmutableList()
    val listOfMediaTypes = listOf("Все", "Фото", "Видео").toImmutableList()
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "Настройки",
            modifier = Modifier.fillMaxWidth().background(color = Color.Cyan)
        )
        ChipPanelSelectTypeList(
            selected = selectedDownloadType,
            textChips = listOfDownloadTypes,
            onClick = onClickDownloadType
        )
        ChipPanelSelectTypeList(
            selected = selectedMediaType,
            textChips = listOfMediaTypes,
            onClick = onClickMediaType
        )
    }
    /**
     * автоскачивание
     * превью с выбором
     * только картинки
     * только видео
     * все
     *
     */
}

@Preview
@Composable
fun SettingsScreenPreview() {
    InstaImageCopyTheme {
        SettingsScreen(
            selectedDownloadType = { 0 },
            selectedMediaType = { 0 },
            onClickDownloadType = {},
            onClickMediaType = {}
        )
    }
}

@Composable
fun ChipPanelSelectTypeList(
    selected: () -> Int,
    textChips: ImmutableList<String>,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val select = selected()
    FlowColumn(
        modifier = modifier,
        itemHorizontalAlignment = Alignment.Start,
        content = {
            textChips.forEachIndexed { index, text ->
                val colorTypeTextButton = when {
                    index == select -> MaterialTheme.colorScheme.onSurface
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
                FilterChip(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    onClick = { onClick(index) },
                    label = {
                        Text(
                            text = text,
                            style = MaterialTheme.typography.labelMedium,
                        )
                    },
                    /*
                                        leadingIcon = {
                                            if (select) Icon(Icons.Default.Done, contentDescription = null)
                                        },
                    */
                    enabled = true,
                    selected = index == select,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedLabelColor = colorTypeTextButton,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        selectedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                            .copy(alpha = 0.2f),
                        disabledSelectedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                            .copy(alpha = 0.5f),
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                            .copy(alpha = 0.4f)
                    ),
                    border = null
                )
            }
        }
    )
}

@Preview
@Composable
fun PreviewChipPanelSelectTypeList() {
    val listChips = listOf("Чип 1", "Новый чип", "Чипец")
    InstaImageCopyTheme() {
        ChipPanelSelectTypeList(
            selected = { 1 },
            textChips = listChips.toImmutableList(),
            onClick = {},
        )
    }
}
