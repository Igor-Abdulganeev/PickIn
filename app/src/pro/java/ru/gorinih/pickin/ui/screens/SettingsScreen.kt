package ru.gorinih.pickin.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import ru.gorinih.pickin.R
import ru.gorinih.pickin.data.remote.AuthWebView
import ru.gorinih.pickin.data.remote.InstagramSessionHandler
import ru.gorinih.pickin.data.remote.models.InstagramSession
import ru.gorinih.pickin.ui.models.MediaDownload
import ru.gorinih.pickin.ui.models.TypeDownload
import ru.gorinih.pickin.ui.theme.PickInTheme
import ru.gorinih.pickin.ui.views.ChipPanelSelectTypeList
import ru.gorinih.pickin.ui.views.DividerHorizontalTransparent
import ru.gorinih.pickin.ui.views.ElevateButton

/**
 * Created by Igor Abdulganeev on 21.10.2026
 * расширяем настройки для авторизации
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
    val sessionManager = koinInject<InstagramSessionHandler>()
    val scope = rememberCoroutineScope()
    val listOfDownloadTypes = listOf(
        stringResource(R.string.list_type_download_first),
        stringResource(R.string.list_type_download_second),
    ).toImmutableList()
    val listOfMediaTypes = listOf(
        stringResource(R.string.list_media_download_first),
        stringResource(R.string.list_media_download_second),
        stringResource(R.string.list_media_download_third)
    ).toImmutableList()
    var authWindow by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(size = 20.dp)
                ),
        ) {
            Text(
                text = stringResource(R.string.header_auth_label),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
             Text(
                text = if (sessionManager.isSession) stringResource(R.string.text_auth_done)
                 else stringResource(R.string.text_auth_cancel),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (sessionManager.isSession) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold
                ),
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                ElevateButton(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp),
                    text = {
                        Text(
                            text = stringResource(R.string.list_auth_first),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    onSavedClick = {
                        authWindow = !authWindow
                    },
                )
                ElevateButton(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp),
                    text = {
                        Text(
                            text = stringResource(R.string.list_auth_second),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    enabled = sessionManager.isSession,
                    onSavedClick = {
                        scope.launch {
                            sessionManager.update(null)
                        }
                    }
                )
            }
            AnimatedVisibility(
                visible = authWindow,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                DividerHorizontalTransparent()
                AuthWebView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    onTakeSession = { session ->
                        scope.launch {
                            sessionManager.update(session)
                            authWindow = session == null
                        }
                    }
                )
            }
        }

        DividerHorizontalTransparent()
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
