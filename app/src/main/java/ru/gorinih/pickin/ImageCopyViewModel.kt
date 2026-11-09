package ru.gorinih.pickin

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.gorinih.pickin.data.parsers.InstagramHtmlParser
import ru.gorinih.pickin.data.parsers.models.CarouselSearchResult
import ru.gorinih.pickin.data.remote.WebLoader
import ru.gorinih.pickin.data.remote.models.NetworkResult
import ru.gorinih.pickin.data.storage.DataStoreRepository
import ru.gorinih.pickin.data.storage.InstagramStorage
import ru.gorinih.pickin.ui.models.EventImageCopy
import ru.gorinih.pickin.ui.models.MediaDownload
import ru.gorinih.pickin.ui.models.MediaItem
import ru.gorinih.pickin.ui.models.ResultMessage
import ru.gorinih.pickin.ui.models.ScreenBlocks
import ru.gorinih.pickin.ui.models.StatusJob
import ru.gorinih.pickin.ui.models.TypeDownload
import ru.gorinih.pickin.ui.models.TypeResult
import ru.gorinih.pickin.ui.models.UiState
import ru.gorinih.pickin.ui.models.toMediaItem
import kotlin.time.Duration.Companion.milliseconds

/**
 * Created by Igor Abdulganeev on 20.10.2026
 */

class ImageCopyViewModel(
    private val webLoader: WebLoader,
    private val parser: InstagramHtmlParser,
    private val storage: InstagramStorage,
    private val dataStore: DataStoreRepository
) : ViewModel() {

    var imageCopyState by mutableStateOf(
        UiState(
            result = ResultMessage(R.string.result_message_start, TypeResult.DONE),
            selectedList = emptyList<String>().toImmutableList()
        )
    )
        private set

    val coroutineExceptionHandler: CoroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            when (throwable) {
                is CancellationException -> throw throwable
                else -> println("Глобальная корутина $throwable")
            }
        }
    private val mediaData: MutableList<MediaItem> = mutableListOf()
    private val selectedData: MutableList<String> = mutableListOf()

    fun sHtml(html: String) {
        val file = storage.saveHtmlToFile(html)
        Log.d("InstagramWeb", "HTML saved: ${file.absolutePath}")
        Log.d("InstagramWeb", "HTML size: ${file.length()} bytes")
    }

    init {
        viewModelScope.launch(Dispatchers.Main + coroutineExceptionHandler) {
            dataStore.loadTypeDownload()
                .catch { }
                .onEach { value ->
                    imageCopyState = imageCopyState.copy(
                        selectedTypeDownload = TypeDownload.entries.firstOrNull { it.index == value }
                            ?: TypeDownload.MANUAL
                    )
                }.stateIn(viewModelScope)
            dataStore.loadMediaDownload()
                .catch { }
                .onEach { value ->
                    imageCopyState = imageCopyState.copy(
                        selectedMediaDownload = MediaDownload.entries.firstOrNull { it.index == value }
                            ?: MediaDownload.ALL
                    )
                }.stateIn(viewModelScope)
            dataStore.loadKeyAutoComplete()
                .catch {  }
                .onEach { value ->
                    imageCopyState = imageCopyState.copy(
                        selectedAutoComplete = value
                    )
                }.stateIn(viewModelScope)
        }
    }

    fun onEvent(event: EventImageCopy) {
        when(event) {
            is EventImageCopy.OnChangeUrl -> onChangeUrl(event.newUrl, event.startIfPossibly)
            is EventImageCopy.OnChangeTypeDownload -> onChangeTypeDownload(event.index)
            is EventImageCopy.OnChangeMediaDownload -> onChangeMediaDownload(event.index)
            is EventImageCopy.OnChangeAutoComplete -> onChangeAutoComplete(event.value)
            is EventImageCopy.SelectData -> selectData(event.previewUrl)
            is EventImageCopy.ClearData -> clear(event.url)
            is EventImageCopy.GetPostData -> getPostData()
            is EventImageCopy.GetSelectedData -> getSelectedData()
            is EventImageCopy.RotateVisibilitySettings -> rotateVisibilitySettings()
            is EventImageCopy.RotateVisibilityHelp -> rotateVisibilityHelp()
            is EventImageCopy.RotateVisibilityPreview -> rotateVisibilityPreview()
        }
    }

    private fun onChangeUrl(newUrl: String, startIfPossibly: Boolean) {

        if (startIfPossibly) {
            clear(newUrl)
        } else {
            imageCopyState = imageCopyState.copy(url = newUrl)
        }
        viewModelScope.launch {
            /**
             * задержка нужно прочитать текущие настройки
             */
            delay(300.milliseconds)
            when{
                startIfPossibly && imageCopyState.selectedTypeDownload == TypeDownload.AUTO -> getPostData()
                startIfPossibly && imageCopyState.selectedTypeDownload == TypeDownload.MANUAL && imageCopyState.selectedAutoComplete -> getPostData()
            }
        }
    }

    private fun onChangeTypeDownload(index: Int) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                TypeDownload.entries.firstOrNull { it.index == index }?.let { type ->
                    dataStore.saveTypeDownload(type)
                }
            } catch (ex: CancellationException) {
                throw ex
            } catch (_: Throwable) {
            }
        }
    }

    private fun onChangeMediaDownload(index: Int) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                MediaDownload.entries.firstOrNull { it.index == index }?.let { media ->
                    dataStore.saveMediaDownload(media)
                }
            } catch (ex: CancellationException) {
                throw ex
            } catch (_: Throwable) {
            }
        }
    }

    private fun onChangeAutoComplete(value: Boolean) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                dataStore.saveKeyAutoComplete(value)
            } catch (ex: CancellationException) {
                throw ex
            } catch (_: Throwable) {
            }
        }
    }

    private fun rotateVisibilitySettings() {
        rotateVisibilityScreen(ScreenBlocks.SETTINGS)
    }

    private fun rotateVisibilityPreview() {
        rotateVisibilityScreen(ScreenBlocks.PREVIEW)
    }

    private fun rotateVisibilityHelp() {
        rotateVisibilityScreen(ScreenBlocks.HELP)
    }

    private fun selectData(previewUrl: String) {
        when (selectedData.contains(previewUrl)) {
            true -> selectedData.remove(previewUrl)
            false -> selectedData.add(previewUrl)
        }
        imageCopyState = imageCopyState.copy(
            selectedList = selectedData.toImmutableList()
        )
    }

    private fun clear(url: String) {
        mediaData.clear()
        selectedData.clear()
        imageCopyState = imageCopyState.copy(
            url = url,
            job = StatusJob.WAITING_JOB,
            result = ResultMessage(R.string.result_message_start, TypeResult.DONE),
            listMedia = emptyList()
        )
    }

    private fun getSelectedData() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val listOfMedia: List<String> =
                mediaData.filter { it.previewUrl in selectedData }.map { it.mediaUrl }
            if (listOfMedia.isNotEmpty())
                getMediaData(listOfMedia)
        }
    }

    private fun getPostData() {
        if (imageCopyState.url.isNotBlank()) {
            imageCopyState =
                imageCopyState.copy(
                    job = StatusJob.DOWNLOAD_POST, result = ResultMessage(
                        R.string.result_message_download_post,
                        TypeResult.DONE
                    )
                )
            viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
                try {
                    when (val result = webLoader.loadPage(url = imageCopyState.url)) {
                        is NetworkResult.Timeout -> {
                            imageCopyState =
                                imageCopyState.copy(
                                    result = ResultMessage(
                                        R.string.result_message_error_time,
                                        TypeResult.ERROR
                                    )
                                )
                        }

                        is NetworkResult.Error -> {
                            val errorId: Int = when {
                                result.errorMessage?.contains("CLEARTEXT") == true -> R.string.result_message_error_url
                                else -> R.string.result_message_error
                            }
                            imageCopyState =
                                imageCopyState.copy(
                                    result = ResultMessage(
                                        errorId,
                                        TypeResult.ERROR,
                                        arrayOf(result.errorMessage ?: "")
                                    )
                                )
                        }

                        is NetworkResult.Success -> {
                            imageCopyState = imageCopyState.copy(
                                job = StatusJob.RESEARCH,
                                result = ResultMessage(
                                    R.string.result_message_looking_for_media,
                                    TypeResult.WORK
                                )
                            )
                            val page = result.data
                            println("Страница скачана:")
                            println("URL: ${page.requestedUrl}")
                            println("HTTP: ${page.status}")
                            println("Content-Type: ${page.contentType}")
                            println("HTML length: ${page.html.length}")
                            println("HTML: ${page.html.take(300)}")
                            // Передаём HTML в парсер.
                           // storage.saveHtmlToFile(page.html) // TODO сохранить страничку если надо

                            val postJson =
                                parser.parsePost(url = imageCopyState.url, html = page.html)

                            when (postJson) {
                                CarouselSearchResult.NotFound -> {
                                    imageCopyState =
                                        imageCopyState.copy(
                                            result = ResultMessage(
                                                R.string.result_message_not_founded_media,
                                                TypeResult.ERROR
                                            )
                                        )
                                }

                                CarouselSearchResult.AgeRestricted -> {
                                    imageCopyState =
                                        imageCopyState.copy(
                                            result = ResultMessage(
                                                R.string.result_message_adult,
                                                TypeResult.ERROR
                                            )
                                        )
                                }

                                is CarouselSearchResult.Found -> {
                                    println("JSON публикации найден:")
                                    mediaData.clear()
                                    mediaData.addAll(
                                        parser.parseCarousel(postJson.media)
                                            .map { it.toMediaItem() })
                                    imageCopyState =
                                        imageCopyState.copy(
                                            result = ResultMessage(
                                                R.string.result_message_founded_media,
                                                TypeResult.DONE,
                                                arrayOf(
                                                    "${mediaData.count()}",
                                                    "${mediaData.count { !it.isVideo }}",
                                                    "${mediaData.count { it.isVideo }}"
                                                )
                                            )
                                        )
                                    when (imageCopyState.selectedTypeDownload) {
                                        TypeDownload.AUTO -> getMediaData()
                                        TypeDownload.MANUAL -> {
                                            imageCopyState = imageCopyState.copy(
                                                listMedia = mediaData,
                                                blocks = imageCopyState.blocks.putting(
                                                    ScreenBlocks.PREVIEW, true
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        is NetworkResult.HttpError -> {
                            imageCopyState =
                                imageCopyState.copy(
                                    result = ResultMessage(
                                        R.string.result_message_error,
                                        TypeResult.ERROR,
                                        arrayOf(" ${result.errorCode} / ${result.errorMessage}"),
                                        )
                                )
                        }
                    }
                } finally {
                    imageCopyState =
                        imageCopyState.copy(job = StatusJob.WAITING_JOB)
                }
            }
        } else {
            imageCopyState =
                imageCopyState.copy(
                    result = ResultMessage(
                        R.string.result_message_error_url,
                        TypeResult.ERROR
                    )
                )
        }
    }

    private fun rotateVisibilityScreen(screen: ScreenBlocks) {
        val visibility = imageCopyState.blocks[screen] ?: false
        imageCopyState = imageCopyState.copy(
            blocks = imageCopyState.blocks.putting(screen, !visibility)
        )
    }

    private suspend fun getMediaData() {
        val listOfMedia: List<String> = when (imageCopyState.selectedMediaDownload) {
            MediaDownload.ALL -> mediaData.map { it.mediaUrl }
            MediaDownload.IMAGE -> mediaData.filter { !it.isVideo }.map { it.mediaUrl }
            MediaDownload.VIDEO -> mediaData.filter { it.isVideo }.map { it.mediaUrl }
        }
        if (listOfMedia.isNotEmpty())
            getMediaData(listOfMedia)
    }

    private suspend fun getMediaData(listUrl: List<String>) {
        val allMediaCount = mediaData.count()

        imageCopyState =
            imageCopyState.copy(
                job = StatusJob.DOWNLOAD_MEDIA,
                result = ResultMessage(
                    R.string.result_message_founded,
                    TypeResult.WORK,
                    arrayOf("$allMediaCount", "${listUrl.count()}")
                )
            )
        try {
            val downloaded = storage.downloadsFiles(
                listUrl,
                mediaData.first().author,
                mediaData.first().nickName,
            )
            val downloadMediaCount = downloaded.count { it.errorMessage.isEmpty() }
            if (allMediaCount == downloadMediaCount) imageCopyState =
                imageCopyState.copy(
                    url = "",
                    listMedia = emptyList()
                )
            imageCopyState = imageCopyState.copy(
                result = ResultMessage(
                    R.string.result_message_downloaded,
                    TypeResult.DONE,
                    arrayOf("$downloadMediaCount", "$allMediaCount")
                ),
                job = StatusJob.WAITING_JOB
            )
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: Throwable) {
            imageCopyState = imageCopyState.copy(
                result = ResultMessage(
                    R.string.result_message_error,
                    TypeResult.ERROR,
                    arrayOf("${ex.localizedMessage}")
                )
            )
        }
    }
}