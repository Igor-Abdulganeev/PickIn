package ru.gorinih.instaimagecopy.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import ru.gorinih.instaimagecopy.R
import ru.gorinih.instaimagecopy.ui.models.ResultMessage
import ru.gorinih.instaimagecopy.ui.models.StatusJob
import ru.gorinih.instaimagecopy.ui.models.TypeDownload
import ru.gorinih.instaimagecopy.ui.models.TypeResult

/**
 * Created by Igor Abdulganeev on 21.10.2026
 */

@Composable
fun EnterUrlScreen(
    modifier: Modifier = Modifier,
    url: () -> String,
    job: () -> StatusJob,
    result: () -> ResultMessage,
    typeDownload: () -> TypeDownload,
    onChangeUrl: (String) -> Unit,
    onDownloadUrl: () -> Unit,
    onClear: () -> Unit,
) {
    val currentJob = job()
    val currentMessage = result()
    Column(modifier = modifier) {
        OutlinedTextField(
            value = url(),
            enabled = currentJob == StatusJob.WAITING_JOB,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            onValueChange = onChangeUrl,
            label = { Text(text = stringResource(R.string.edit_text_label)) },
            shape = RoundedCornerShape(size = 10.dp),
        )
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround) {
            ElevatedButton(
                onClick = { onDownloadUrl() },
                enabled = currentJob == StatusJob.WAITING_JOB
            ) {
                Text(
                    text = stringResource(
                        when (currentJob) {
                            StatusJob.WAITING_JOB -> when (typeDownload()) {
                                TypeDownload.AUTO -> R.string.button_text_waiting_auto
                                else -> R.string.button_text_waiting_manual
                            }

                            StatusJob.DOWNLOAD_POST -> R.string.button_text_download_post
                            StatusJob.DOWNLOAD_MEDIA -> R.string.button_text_download_media
                            StatusJob.RESEARCH -> R.string.button_text_research
                        }
                    )
                )
            }
            ElevatedButton(
                onClick = onClear,
                enabled = currentJob == StatusJob.WAITING_JOB
            ) {
                Text(
                    text = stringResource(R.string.button_text_clear)
                )
            }
        }
        Text(
            text = stringResource(R.string.header_status_label),
            modifier = Modifier.fillMaxWidth().padding(start = 24.dp, top = 8.dp, end = 16.dp),
            color = MaterialTheme.colorScheme.onTertiaryContainer,
            fontSize = TextUnit(12f, TextUnitType.Sp)
        )
        Box(
            modifier = Modifier.fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                .border(
                1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(size = 20.dp)
            )
        ) {
            Text(
                text = stringResource(id = currentMessage.message, formatArgs = currentMessage.params),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                color = when (currentMessage.type) {
                    TypeResult.DONE -> MaterialTheme.colorScheme.onSurface
                    TypeResult.WORK -> MaterialTheme.colorScheme.tertiary
                    TypeResult.ERROR -> MaterialTheme.colorScheme.error
                }
            )
        }
    }
}