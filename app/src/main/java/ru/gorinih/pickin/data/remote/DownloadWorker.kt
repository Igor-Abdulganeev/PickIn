package ru.gorinih.pickin.data.remote

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.serialization.json.Json
import ru.gorinih.pickin.ImageCopyViewModel.Companion.KEY_POST_AUTHOR
import ru.gorinih.pickin.ImageCopyViewModel.Companion.KEY_POST_NICK
import ru.gorinih.pickin.ImageCopyViewModel.Companion.KEY_POST_URLS
import ru.gorinih.pickin.ImageCopyViewModel.Companion.RESULT_DOWNLOAD_FILES
import ru.gorinih.pickin.ImageCopyViewModel.Companion.RESULT_KEY_FILE_IS_DONE
import ru.gorinih.pickin.ImageCopyViewModel.Companion.RESULT_KEY_FILE_URL
import ru.gorinih.pickin.data.storage.InstagramStorage
import ru.gorinih.pickin.data.storage.models.DownloadResult
import ru.gorinih.pickin.data.storage.models.MediaResult

/**
 * Created by Igor Abdulganeev on 23.11.2026
 */

class DownloadWorker(
    context: Context,
    params: WorkerParameters,
    private val storage: InstagramStorage,
    private val json: Json,
) : CoroutineWorker(appContext = context, params = params) {
    override suspend fun doWork(): Result {

        val author = inputData.getString(KEY_POST_AUTHOR) ?: ""
        val nick = inputData.getString(KEY_POST_NICK) ?: ""
        val urls = inputData.getNullableStringArray(KEY_POST_URLS)?.requireNoNulls()
        if (urls.isNullOrEmpty()) return Result.success()
        try {
            val downloads = mutableListOf<MediaResult>()
            for (url in urls) {
                val result = storage.downloadFile(url, author, nick)
                val isDone = result is DownloadResult.DownloadSuccess
                val file: MediaResult =
                    when (result) {
                        is DownloadResult.DownloadCancel -> MediaResult(
                            file = url,
                            uri = null,
                            type = null
                        )

                        is DownloadResult.DownloadError -> MediaResult(
                            file = url,
                            uri = null,
                            errorMessage = result.errorMessage ?: "",
                            type = null
                        )

                        is DownloadResult.DownloadSuccess -> MediaResult(
                            file = url,
                            uri = result.fileUri,
                            type = result.fileType
                        )
                    }
                downloads.add(file)
                setProgress(
                    workDataOf(
                        RESULT_KEY_FILE_URL to url,
                        RESULT_KEY_FILE_IS_DONE to isDone
                    )
                )
            }
            val jsonData = json.encodeToString(downloads)
            return Result.success(
                workDataOf(
                    RESULT_DOWNLOAD_FILES to jsonData
                )
            )
        } catch (ex: Throwable) {
            return Result.failure(
                workDataOf(
                    RESULT_DOWNLOAD_FILES to ex.localizedMessage
                )
            )
        }
    }
}