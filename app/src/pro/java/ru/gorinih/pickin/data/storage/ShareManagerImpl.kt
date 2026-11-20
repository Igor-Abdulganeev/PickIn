package ru.gorinih.pickin.data.storage

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.gorinih.pickin.data.storage.models.DownloadResult
import ru.gorinih.pickin.data.storage.models.SavedFile

/**
 * Created by Igor Abdulganeev on 19.11.2026
 */

class ShareManagerImpl(
    private val context: Context,
    private val storage: InstagramStorage,
) : ShareManager {
    override val accessAllowed: Boolean
        get() = true

    override suspend fun getAndShareMedia(url: String): SavedFile? {
        val fileResult = storage.takeFileForShare(url = url)
        if (fileResult is DownloadResult.DownloadSuccess) {
            val contentUri = fileResult.fileUri
            val type = fileResult.fileType
            return shareUri(contentUri, type)
        } else {
            errorShow()
            return null
        }
    }

    override suspend fun shareMedia(url: String, contentUri: Uri, typeMime: String?): SavedFile? =
        when(checkUri(contentUri)) {
            true -> shareUri(contentUri, typeMime)
            false -> getAndShareMedia(url)
        }

    private fun shareUri(contentUri: Uri, typeMime: String?): SavedFile? {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_STREAM, contentUri)
            type = typeMime
            clipData = ClipData.newRawUri("", contentUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooserIntent = Intent.createChooser(shareIntent, "Поделиться").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(chooserIntent)
            return SavedFile(contentUri, typeMime)
        } catch (_: Throwable) {
            errorShow()
            return null
        }
    }

    private suspend fun checkUri(contentUri: Uri): Boolean = withContext(Dispatchers.IO){
        val contentResolver = context.contentResolver ?: return@withContext false
        return@withContext try {
            val protectionKeys = arrayOf(
                MediaStore.MediaColumns.SIZE,
                MediaStore.MediaColumns.IS_PENDING,
                MediaStore.MediaColumns.IS_TRASHED
            )
            val isMetadataValid: Boolean = contentResolver.query(
                contentUri,
                protectionKeys,
                null,
                null,
                null
            )?.use { cursor ->
                if (!cursor.moveToFirst()) return@use false

                val indexSize = cursor.getColumnIndex(MediaStore.MediaColumns.SIZE)
                val indexTrash = cursor.getColumnIndex(MediaStore.MediaColumns.IS_TRASHED)
                val indexPending = cursor.getColumnIndex(MediaStore.MediaColumns.IS_PENDING)

                val size = if (indexSize!=-1) cursor.getLong(indexSize) else 0L
                val isTrash = indexTrash!= -1 && cursor.getInt(indexTrash) == 1
                val isPending = indexPending!= -1 && cursor.getInt(indexPending) == 1

                size > 0L && !isTrash && !isPending
            } ?: false

            if (!isMetadataValid) return@withContext false

            contentResolver.openAssetFileDescriptor(contentUri, "r")?.use { descriptor ->
                descriptor.length > 0
            } ?: false

        } catch (_: Throwable) {
            false
        }
     }

    private fun errorShow() {
        Toast.makeText(
            context,
            "Поделиться не удалось, что то пошло не так",
            Toast.LENGTH_SHORT
        ).show()
    }
}