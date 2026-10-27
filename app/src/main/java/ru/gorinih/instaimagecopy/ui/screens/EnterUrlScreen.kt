package ru.gorinih.instaimagecopy.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
    //val insta = koinInject<InstaParser>()
    Column(modifier = modifier) {
        TextField(
            value = url(),
            enabled = currentJob == StatusJob.WAITING,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = onChangeUrl
        )
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onDownloadUrl() },
            enabled = currentJob == StatusJob.WAITING
        ) {
            Text(
                text = stringResource(
                    when (currentJob) {
                        StatusJob.WAITING -> R.string.button_text_waiting
                        StatusJob.DOWNLOAD -> R.string.button_text_download
                        StatusJob.SEPARATE -> R.string.button_text_separate
                    }
                )
            )
        }
        Text(text = result())
    }
}