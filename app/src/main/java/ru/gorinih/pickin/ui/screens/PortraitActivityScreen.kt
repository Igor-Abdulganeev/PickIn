package ru.gorinih.pickin.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.toImmutableList
import ru.gorinih.pickin.R
import ru.gorinih.pickin.ui.models.EventImageCopy
import ru.gorinih.pickin.ui.models.ResultMessage
import ru.gorinih.pickin.ui.models.ScreenBlocks
import ru.gorinih.pickin.ui.models.TypeResult
import ru.gorinih.pickin.ui.models.UiState
import ru.gorinih.pickin.ui.theme.PickInTheme
import ru.gorinih.pickin.ui.views.ChipItem
import ru.gorinih.pickin.ui.views.ChipPanelSelectTypeList
import ru.gorinih.pickin.ui.views.ElevateButton

/**
 * Created by Igor Abdulganeev on 06.11.2026
 */

@Composable
fun PortraitActivityScreen(
    imageCopyState: UiState,
    event: (EventImageCopy) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            ChipItem(
                text = stringResource(R.string.button_text_settings),
                index = 0,
                isSelected = imageCopyState.blocks[ScreenBlocks.SETTINGS] == true,
                leadIcon = null,
                onClick = {
                    if (
                        imageCopyState.blocks[ScreenBlocks.HELP] == true &&
                        imageCopyState.blocks[ScreenBlocks.SETTINGS] == false
                    ) event(EventImageCopy.RotateVisibilityHelp)
                    event(EventImageCopy.RotateVisibilitySettings)
                }
            )
            ChipItem(
                text = stringResource(R.string.button_text_help),
                index = 1,
                isSelected = imageCopyState.blocks[ScreenBlocks.HELP] == true,
                leadIcon = null,
                onClick = {
                    if (
                        imageCopyState.blocks[ScreenBlocks.SETTINGS] == true &&
                        imageCopyState.blocks[ScreenBlocks.HELP] == false
                    ) event(EventImageCopy.RotateVisibilitySettings)
                    event(EventImageCopy.RotateVisibilityHelp)
                }
            )
        }
        if (imageCopyState.blocks[ScreenBlocks.INPUT] == true)
            EnterUrlScreen(
                url = { imageCopyState.url },
                job = { imageCopyState.job },
                result = { imageCopyState.result },
                typeDownload = { imageCopyState.selectedTypeDownload },
                onChangeUrl = { newUrl ->
                    event(EventImageCopy.OnChangeUrl(newUrl = newUrl))
                },
                onDownloadUrl = { event(EventImageCopy.GetPostData) },
                onClear = { event(EventImageCopy.ClearData()) }
            )
        AnimatedVisibility(
            visible = imageCopyState.blocks[ScreenBlocks.HELP] == true
        ) {
            HelpScreen()
        }
        AnimatedVisibility(
            visible = imageCopyState.blocks[ScreenBlocks.SETTINGS] == true
        ) {
            SettingsScreen(
                selectedDownloadType = { imageCopyState.selectedTypeDownload },
                selectedMediaType = { imageCopyState.selectedMediaDownload },
                selectedAutoComplete = { imageCopyState.selectedAutoComplete },
                onClickDownloadType = {
                    event(EventImageCopy.OnChangeTypeDownload(index = it))
                },
                onClickMediaType = {
                    event(EventImageCopy.OnChangeMediaDownload(index = it))
                },
                onClickAutoComplete = {
                    event(EventImageCopy.OnChangeAutoComplete(value = it))
                }
            )
        }
        AnimatedVisibility(
            visible = imageCopyState.blocks[ScreenBlocks.PREVIEW] == true
        ) {
            PreviewScreen(
                job = { imageCopyState.job },
                listMedia = imageCopyState.listMedia.toImmutableList(),
                selectedList = { imageCopyState.selectedList },
                downloadedList = { imageCopyState.downloadedList },
                onStartDownload = {
                    event(EventImageCopy.GetSelectedData)
                },
                onClickMedia = {
                    event(EventImageCopy.SelectData(previewUrl = it))
                },
                onShareMedia = when (imageCopyState.isShare) {
                        true -> { url -> event(EventImageCopy.ShareMedia(url)) }
                        false -> null
                }
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun PortraitActivityScreenPreview(
) {
    PickInTheme {
        PortraitActivityScreen(
            imageCopyState = UiState(
                url = "тестовый url",
                result = ResultMessage(R.string.result_message_start, TypeResult.DONE),
                selectedList = emptyList<String>().toImmutableList(),
                downloadedList = emptyList<String>().toImmutableList(),
            )
        ) {}
    }
}