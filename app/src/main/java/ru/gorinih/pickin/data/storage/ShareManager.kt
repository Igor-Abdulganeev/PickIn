package ru.gorinih.pickin.data.storage

import android.net.Uri
import ru.gorinih.pickin.data.storage.models.SavedFile

/**
 * Created by Igor Abdulganeev on 19.11.2026
 */

interface ShareManager {
    val accessAllowed: Boolean
    suspend fun getAndShareMedia(url: String): SavedFile?
    suspend fun shareMedia(url: String, contentUri: Uri, typeMime: String?): SavedFile?
}