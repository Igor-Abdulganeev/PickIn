package ru.gorinih.pickin.data.storage.models

import android.net.Uri
import kotlinx.serialization.Serializable
import ru.gorinih.pickin.domain.utils.UriSerialize

/**
 * Created by Igor Abdulganeev on 27.10.2026
 */

@Serializable
data class MediaResult(
    val file: String,
    @Serializable(UriSerialize::class)
    val uri: Uri?,
    val errorMessage: String = "",
    val type: String?
)
