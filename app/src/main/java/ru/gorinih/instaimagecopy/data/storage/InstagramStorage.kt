package ru.gorinih.instaimagecopy.data.storage

import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import io.ktor.client.HttpClient
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.isSuccess
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import ru.gorinih.instaimagecopy.data.storage.models.DownloadResult
import ru.gorinih.instaimagecopy.data.storage.models.FilesResult
import java.io.File
import java.io.OutputStream

/**
 * Created by Igor Abdulganeev on 23.10.2026
 */

class InstagramStorage(
    private val context: Context,
    private val httpClient: HttpClient
) {

    fun saveHtmlToFile(html: String): File {
        val file = File(context.filesDir, "instagram_response.html")
        file.writeText(html, Charsets.UTF_8)
        return file
    }

    suspend fun downloadsFiles(urls: List<String>, authorName: String): List<FilesResult> = withContext(Dispatchers.IO) {
        val resultList = mutableListOf<FilesResult>()
        for (url in urls) {
            ensureActive()
            when(val result = downloadFile(url, authorName)) {
                is DownloadResult.DownloadCancel -> return@withContext emptyList()
                is DownloadResult.DownloadError -> {resultList.add(FilesResult(
                    file = url,
                    uri = null,
                    errorMessage = result.errorMessage ?: ""
                ))}
                is DownloadResult.DownloadSuccess -> {
                    resultList.add(FilesResult(
                        file = url,
                        uri = result.fileUri
                    ))
                }
            }
        }
        return@withContext resultList
    }

    private suspend fun downloadFile(url: String, authorName: String): DownloadResult {
        if (url.isEmpty()) return DownloadResult.DownloadError(errorMessage = "Files path is empty.")
        val contentResolver = context.contentResolver ?: return DownloadResult.DownloadError(
            errorMessage = "Can`t get content resolver."
        )
        val fileName =
            url.substringAfterLast("/").substringBefore("?") + "_" + System.currentTimeMillis()
        val mimeType = getMimeType(url)
        val contentValue = ContentValues().apply {
            put(MediaStore.Downloads.RELATIVE_PATH, "Download/$authorName/")
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, mimeType)
            put(MediaStore.Downloads.IS_PENDING, 1)
        }
        val contentUri = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val fileUri =
            contentResolver.insert(contentUri, contentValue) ?: return DownloadResult.DownloadError(
                "Can`t create file into MediaStore"
            )

        var isDownloaded = false
        return try {
            httpClient.prepareGet(url).execute { response ->
                if (!response.status.isSuccess()) return@execute DownloadResult.DownloadError(
                    errorMessage = "Error request ${response.status.value} / ${response.status.description}"
                )

                contentResolver.openOutputStream(fileUri).use { outputStream ->
                    if (outputStream == null) return@use DownloadResult.DownloadError(errorMessage = "Can`t open file to write")

                    val channelDownload: ByteReadChannel = response.bodyAsChannel()

                    downloadStreamFile(channelDownload, outputStream)
                }
            }

            val contentUpdateValue = ContentValues().apply {
                put(MediaStore.Downloads.IS_PENDING, 0)
            }
            contentResolver.update(fileUri, contentUpdateValue, null)
            isDownloaded = true
            DownloadResult.DownloadSuccess(fileUri = fileUri)
        } catch (_: CancellationException) {
            DownloadResult.DownloadCancel
        } catch (ex: Throwable) {
            DownloadResult.DownloadError(errorMessage = ex.localizedMessage)
        } finally {
            if (!isDownloaded) {
                contentResolver.delete(fileUri,null)
            }
        }
    }

    private suspend fun downloadStreamFile(channel: ByteReadChannel, outStream: OutputStream) {
        val buffer = ByteArray(16254)
        while (!channel.isClosedForRead) {
            val bytes = channel.readAvailable(buffer)
            if(bytes>0)  {
                outStream.write(buffer, 0, bytes)
            }
        }
    }

    private fun getMimeType(url: String): String? =
        MimeTypeMap.getFileExtensionFromUrl(url).run {
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(this)
        }
}