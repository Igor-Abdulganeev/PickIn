package ru.gorinih.instaimagecopy.data.storage.models

import android.net.Uri

/**
 * Created by Igor Abdulganeev on 27.10.2026
 */

sealed class DownloadResult {

    data object DownloadCancel: DownloadResult()

    data class DownloadError(val errorMessage: String? = null): DownloadResult()

    data class DownloadSuccess(val fileUri: Uri): DownloadResult()
}