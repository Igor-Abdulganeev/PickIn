package ru.gorinih.instaimagecopy.data.storage.models

import android.net.Uri

/**
 * Created by Igor Abdulganeev on 27.10.2026
 */

data class MediaResult(
    val file: String,
    val uri: Uri?,
    val errorMessage: String = ""
)
