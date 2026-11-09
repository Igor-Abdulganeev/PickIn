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
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.toImmutableList
import ru.gorinih.pickin.R
import ru.gorinih.pickin.ui.models.EventImageCopy
import ru.gorinih.pickin.ui.models.ScreenBlocks
import ru.gorinih.pickin.ui.models.UiState
import ru.gorinih.pickin.ui.views.ElevateButton

/**
 * Created by Igor Abdulganeev on 06.11.2026
 */

@Composable
fun LandscapeActivityScreen(
    imageCopyState: UiState,
    event: (EventImageCopy) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .weight(1f)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                ElevateButton(
                    text = { Text(text = stringResource(R.string.button_text_settings)) },
                    onSavedClick = {
                        event(EventImageCopy.RotateVisibilitySettings)
                    },
                )
                ElevateButton(
                    text = { Text(text = stringResource(R.string.button_text_help)) },
                    onSavedClick = {
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
        }
        Column(modifier = Modifier
            .fillMaxWidth()
            .weight(1f)) {
            AnimatedVisibility(
                visible = imageCopyState.blocks[ScreenBlocks.PREVIEW] == true,
            ) {
                PreviewScreen(
                    job = { imageCopyState.job },
                    listMedia = imageCopyState.listMedia.toImmutableList(),
                    selectedList = { imageCopyState.selectedList },
                    onStartDownload = {
                        event(EventImageCopy.GetSelectedData)
                    },
                    onClickMedia = {
                        event(EventImageCopy.SelectData(previewUrl = it))
                    }
                )
            }
        }
    }
}