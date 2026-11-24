package ru.gorinih.pickin.domain.remote

import kotlinx.coroutines.flow.Flow
import ru.gorinih.pickin.data.remote.models.DownloadState

/**
 * Created by Igor Abdulganeev on 23.11.2026
 */

interface StarterDownloadWorker {
    suspend fun startDownload(
        urls: List<String>,
        authorName: String,
        authorNick: String
    ): Flow<DownloadState>
}