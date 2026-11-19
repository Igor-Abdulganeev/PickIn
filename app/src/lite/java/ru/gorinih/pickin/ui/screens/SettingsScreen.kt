package ru.gorinih.pickin.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.collections.immutable.toImmutableList
import ru.gorinih.pickin.R
import ru.gorinih.pickin.ui.models.MediaDownload
import ru.gorinih.pickin.ui.models.TypeDownload
import ru.gorinih.pickin.ui.theme.PickInTheme
import ru.gorinih.pickin.ui.views.ChipPanelSelectTypeList
import ru.gorinih.pickin.ui.views.DividerHorizontalTransparent

/**
 * Created by Igor Abdulganeev on 21.10.2026
 */

@Composable
fun SettingsScreen(
    selectedDownloadType: () -> TypeDownload,
    selectedMediaType: () -> MediaDownload,
    selectedAutoComplete: () -> Boolean,
    onClickDownloadType: (Int) -> Unit,
    onClickMediaType: (Int) -> Unit,
    onClickAutoComplete: (Boolean) -> Unit
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
        DividerHorizontalTransparent()
        AnimatedVisibility(
            visible = selectedDownloadType().index == TypeDownload.AUTO.index
        ) {
            ChipPanelSelectTypeList(
                selected = { selectedMediaType().index },
                textChips = listOfMediaTypes,
                onClick = onClickMediaType,
                text = stringResource(R.string.header_media_download_label)
            )
        }
        AnimatedVisibility(
            visible = selectedDownloadType().index == TypeDownload.MANUAL.index
        ) {
            ChipPanelSelectTypeList(
                selected = {
                    if(selectedAutoComplete()) 0 else 100
                },
                textChips = listOf(stringResource(R.string.setting_key_auto_complete)).toImmutableList(),
                onClick = {
                    onClickAutoComplete(!selectedAutoComplete())
                },
                needIcon = true
            )
        }
    }
}

@Preview
@Composable
fun SettingsScreenPreview() {
    PickInTheme {
        SettingsScreen(
            selectedDownloadType = { TypeDownload.AUTO },
            selectedMediaType = { MediaDownload.IMAGE },
            selectedAutoComplete = { true },
            onClickDownloadType = {},
            onClickMediaType = {},
            onClickAutoComplete = {}
        )
    }
}
