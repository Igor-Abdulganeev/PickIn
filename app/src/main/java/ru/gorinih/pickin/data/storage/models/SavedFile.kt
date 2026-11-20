package ru.gorinih.pickin.data.storage.models

import android.net.Uri

/**
 * Created by Igor Abdulganeev on 20.11.2026
 */

data class SavedFile(
    val uri: Uri,
    val type: String?
)
