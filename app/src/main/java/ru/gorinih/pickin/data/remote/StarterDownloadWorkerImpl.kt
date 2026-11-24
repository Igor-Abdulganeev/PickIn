package ru.gorinih.pickin.data.remote

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.transformWhile
import kotlinx.serialization.json.Json
import ru.gorinih.pickin.ImageCopyViewModel.Companion.KEY_POST_AUTHOR
import ru.gorinih.pickin.ImageCopyViewModel.Companion.KEY_POST_NICK
import ru.gorinih.pickin.ImageCopyViewModel.Companion.KEY_POST_URLS
import ru.gorinih.pickin.ImageCopyViewModel.Companion.RESULT_DOWNLOAD_FILES
import ru.gorinih.pickin.ImageCopyViewModel.Companion.RESULT_KEY_FILE_IS_DONE
import ru.gorinih.pickin.ImageCopyViewModel.Companion.RESULT_KEY_FILE_URL
import ru.gorinih.pickin.data.remote.models.DownloadState
import ru.gorinih.pickin.data.storage.models.MediaResult
import ru.gorinih.pickin.domain.remote.StarterDownloadWorker

/**
 * Created by Igor Abdulganeev on 23.11.2026
 */

class StarterDownloadWorkerImpl(
    context: Context,
    private val json: Json
) : StarterDownloadWorker {
    private val workManager = WorkManager.getInstance(context)

    override suspend fun startDownload(
        urls: List<String>,
        authorName: String,
        authorNick: String
    ): Flow<DownloadState> {
        val request = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setInputData(
                workDataOf(
                    KEY_POST_AUTHOR to authorName,
                    KEY_POST_NICK to authorNick,
                    KEY_POST_URLS to urls.toTypedArray()
                )
            )
            .setConstraints(
                Constraints(
                    requiredNetworkType = NetworkType.CONNECTED
                )
            )
            .build()
        workManager.enqueue(request = request)
        return workManager.getWorkInfoByIdFlow(request.id)
            .filterNotNull()
            .transformWhile { workInfo ->
                when (workInfo.state) {
                    WorkInfo.State.FAILED, WorkInfo.State.CANCELLED -> {
                        val message = workInfo.outputData.getString(RESULT_DOWNLOAD_FILES)
                            ?: "Unknown error download files."
                        emit(DownloadState.Error(message))
                        false
                    }

                    WorkInfo.State.RUNNING -> {
                        val url = workInfo.progress.getString(RESULT_KEY_FILE_URL)
                        val isDone = workInfo.progress.getBoolean(RESULT_KEY_FILE_IS_DONE, false)
                        if (!url.isNullOrEmpty()) emit(DownloadState.Progress(url, isDone))
                        true
                    }

                    WorkInfo.State.SUCCEEDED -> {
                        val rawJson = workInfo.outputData.getString(RESULT_DOWNLOAD_FILES)
                        val files = if (!rawJson.isNullOrEmpty()) runCatching {
                            json.decodeFromString<List<MediaResult>>(
                                rawJson
                            )
                        }.getOrDefault(emptyList())
                        else emptyList()
                        emit(DownloadState.Success(files))
                        false
                    }

                    else -> true
                }
            }
    }
}