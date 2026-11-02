package ru.gorinih.instaimagecopy.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import ru.gorinih.instaimagecopy.R
import ru.gorinih.instaimagecopy.ui.models.MediaDownload
import ru.gorinih.instaimagecopy.ui.models.TypeDownload
import ru.gorinih.instaimagecopy.ui.theme.InstaImageCopyTheme
import ru.gorinih.instaimagecopy.ui.views.DividerHorizontalTransparent

/**
 * Created by Igor Abdulganeev on 21.10.2026
 */

@Composable
fun SettingsScreen(
    selectedDownloadType: () -> TypeDownload,
    selectedMediaType: () -> MediaDownload,
    onClickDownloadType: (Int) -> Unit,
    onClickMediaType: (Int) -> Unit,
) {
    val listOfDownloadTypes = listOf(
        stringResource(R.string.list_type_download_first),
        stringResource(R.string.list_type_download_second)
    ).toImmutableList()
    val listOfMediaTypes = listOf(
        stringResource(R.string.list_media_download_first),
        stringResource(R.string.list_media_download_second),
        stringResource(R.string.list_media_download_third)
    ).toImmutableList()
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        ChipPanelSelectTypeList(
            selected = { selectedDownloadType().index },
            textChips = listOfDownloadTypes,
            onClick = onClickDownloadType,
            text = stringResource(R.string.header_type_download_label)
        )
        AnimatedVisibility(
            visible =  selectedDownloadType().index == TypeDownload.AUTO.index
        ) {
            DividerHorizontalTransparent()
            ChipPanelSelectTypeList(
                selected = { selectedMediaType().index },
                textChips = listOfMediaTypes,
                onClick = onClickMediaType,
                text = stringResource(R.string.header_media_download_label)
            )
        }
    }
}

@Preview
@Composable
fun SettingsScreenPreview() {
    InstaImageCopyTheme {
        SettingsScreen(
            selectedDownloadType = { TypeDownload.MANUAL },
            selectedMediaType = { MediaDownload.IMAGE },
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
    text: String = "",
) {
    val select = selected()
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(size = 20.dp)
            ),
        content = {
            if (text.isNotEmpty()) {
                Text(
                    text = text,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    textAlign = TextAlign.Center
                    )
            }
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
