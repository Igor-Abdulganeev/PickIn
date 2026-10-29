package ru.gorinih.instaimagecopy.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.gorinih.instaimagecopy.R
import ru.gorinih.instaimagecopy.ui.models.StatusJob

/**
 * Created by Igor Abdulganeev on 21.10.2026
 */

@Composable
fun EnterUrlScreen(
    modifier: Modifier = Modifier,
    url: () -> String,
    job: () -> StatusJob,
    result: () -> String,
    onChangeUrl: (String) -> Unit,
    onDownloadUrl: () -> Unit,
) {
    val currentJob = job()
    Column(modifier = modifier) {
        TextField(
            value = url(),
            enabled = currentJob == StatusJob.WAITING_JOB,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            onValueChange = onChangeUrl
        )
        Button(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            onClick = { onDownloadUrl() },
            enabled = currentJob == StatusJob.WAITING_JOB
        ) {
            Text(
                text = stringResource(
                    when (currentJob) {
                        StatusJob.WAITING_JOB -> R.string.button_text_waiting
                        StatusJob.DOWNLOAD_POST -> R.string.button_text_download_post
                        StatusJob.DOWNLOAD_MEDIA -> R.string.button_text_download_media
                        StatusJob.RESEARCH -> R.string.button_text_research
                    }
                )
            )
        }
        Text(text = result())
    }
}