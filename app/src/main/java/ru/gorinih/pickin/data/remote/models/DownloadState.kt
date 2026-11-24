package ru.gorinih.pickin.data.remote.models

import ru.gorinih.pickin.data.storage.models.MediaResult

/**
 * Created by Igor Abdulganeev on 23.11.2026
 */

sealed interface DownloadState {

    data class Progress(
        val url: String,
        val isDone: Boolean
    ): DownloadState

    data class Success(
        val files: List<MediaResult>
    ): DownloadState

    data class Error(
        val errorMessage: String?
    ): DownloadState
}