package ru.gorinih.instaimagecopy

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.gorinih.instaimagecopy.data.parsers.InstagramHtmlParser
import ru.gorinih.instaimagecopy.data.parsers.models.CarouselSearchResult
import ru.gorinih.instaimagecopy.data.remote.models.NetworkResult
import ru.gorinih.instaimagecopy.data.remote.WebLoader
import ru.gorinih.instaimagecopy.data.storage.DataStoreRepository
import ru.gorinih.instaimagecopy.data.storage.InstagramStorage
import ru.gorinih.instaimagecopy.ui.models.MediaDownload
import ru.gorinih.instaimagecopy.ui.models.MediaItem
import ru.gorinih.instaimagecopy.ui.models.ScreenBlocks
import ru.gorinih.instaimagecopy.ui.models.StatusJob
import ru.gorinih.instaimagecopy.ui.models.TypeDownload
import ru.gorinih.instaimagecopy.ui.models.UiState
import ru.gorinih.instaimagecopy.ui.models.toMediaItem
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
            // url = "https://phet-dev.colorado.edu/html/build-an-atom/0.0.0-3/simple-text-only-test-page.html"
            // url = "https://www.instagram.com/p/DesFNjnDQgL/?img_index=1"
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
                            ?: TypeDownload.NONE
                    )
                }.stateIn(viewModelScope)
            dataStore.loadMediaDownload()
                .catch { }
                .onEach { value ->
                    imageCopyState = imageCopyState.copy(
                        selectedMediaDownload = MediaDownload.entries.firstOrNull { it.index == value }
                            ?: MediaDownload.NONE
                    )
                }.stateIn(viewModelScope)
        }
    }

    fun onChangeUrl(newUrl: String, startIfPossibly: Boolean = false) {
        imageCopyState = imageCopyState.copy(url = newUrl)
        viewModelScope.launch {
            /**
             * задержка нужна прочитать текущие настройки
             */
            delay(300.milliseconds)
            if (startIfPossibly && imageCopyState.selectedTypeDownload == TypeDownload.AUTO) getPostData()
        }
    }

    fun onChangeTypeDownload(index: Int) {
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

    fun onChangeMediaDownload(index: Int) {
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

    fun rotateVisibilitySettings() {
        val visibility = imageCopyState.blocks[ScreenBlocks.SETTINGS] ?: false
        imageCopyState = imageCopyState.copy(
            blocks = imageCopyState.blocks.putting(ScreenBlocks.SETTINGS, !visibility)
        )
    }

    fun rotateVisibilityPreview() {
        val visibility = imageCopyState.blocks[ScreenBlocks.PREVIEW] ?: false
        imageCopyState = imageCopyState.copy(
            blocks = imageCopyState.blocks.putting(ScreenBlocks.PREVIEW, !visibility)
        )
    }

    fun selectData(previewUrl: String) {
        when (selectedData.contains(previewUrl)) {
            true -> selectedData.remove(previewUrl)
            false -> selectedData.add(previewUrl)
        }
    }

    fun getSelectedData() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val listOfMedia: List<String> =
                mediaData.filter { it.previewUrl in selectedData }.map { it.mediaUrl }
            if (listOfMedia.isNotEmpty())
                getMediaData(listOfMedia)
        }
    }

    fun getPostData() {
        if (imageCopyState.url.isNotBlank()) {
            imageCopyState =
                imageCopyState.copy(job = StatusJob.DOWNLOAD_POST, result = "Скачиваем пост")
            viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
                try {
                    when (val result = webLoader.loadPage(url = imageCopyState.url)) {
                        is NetworkResult.Timeout -> {
                            imageCopyState =
                                imageCopyState.copy(result = "Запрос не вернул результат за отведенное время")
                            println("Запрос не вернул результат за отведенное время ${result.errorMessage}")
                        }

                        is NetworkResult.Error -> {
                            val errorText = when {
                                result.errorMessage?.contains("CLEARTEXT") == true -> "введите корректный адрес"
                                else -> result.errorMessage
                            }
                            imageCopyState =
                                imageCopyState.copy(result = "Ошибка: $errorText")
                        }

                        is NetworkResult.Success -> {
                            imageCopyState = imageCopyState.copy(
                                job = StatusJob.RESEARCH,
                                result = "Ищем ссылки на медиа"
                            )
                            val page = result.data
                            println("Страница скачана:")
                            println("URL: ${page.requestedUrl}")
                            println("HTTP: ${page.status}")
                            println("Content-Type: ${page.contentType}")
                            println("HTML length: ${page.html.length}")
                            println("HTML: ${page.html.take(300)}")
                            // Передаём HTML в парсер.
                            //storage.saveHtmlToFile(page.html) // TODO сохранить страничку если надо

                            val postJson =
                                parser.parsePost(url = imageCopyState.url, html = page.html)

                            when (postJson) {
                                CarouselSearchResult.NotFound -> {
                                    imageCopyState =
                                        imageCopyState.copy(result = "Не удалось найти ссылки")
                                    println("Не удалось найти JSON публикации")
                                }

                                CarouselSearchResult.AgeRestricted -> {
                                    imageCopyState =
                                        imageCopyState.copy(result = "У публикации возрастные ограничения")
                                    println("У публикации возрастные ограничения")
                                }

                                is CarouselSearchResult.Found -> {
                                    println("JSON публикации найден:")
                                    mediaData.clear()
                                    mediaData.addAll(
                                        parser.parseCarousel(postJson.media)
                                            .map { it.toMediaItem() })
                                    imageCopyState =
                                        imageCopyState.copy(
                                            result = "Найдено файлов ${mediaData.count()} (" +
                                                    " фото - ${mediaData.count { !it.isVideo }}" +
                                                    " видео - ${mediaData.count { it.isVideo }} )"
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
                                            Log.e("INSTAGRAM", "imageCopyState = $imageCopyState")
                                        }

                                        TypeDownload.NONE -> {}
                                    }
                                }
                            }
                        }

                        is NetworkResult.HttpError -> {
                            imageCopyState =
                                imageCopyState.copy(result = "Ошибка: ${result.errorCode} / ${result.errorMessage}")
                            println("Ошибка http ${result.errorCode} / ${result.errorMessage}")
                        }
                    }
                } finally {
                    imageCopyState =
                        imageCopyState.copy(job = StatusJob.WAITING_JOB) // todo изменить потом, менять в разделке
                }
            }
        } else {
            imageCopyState =
                imageCopyState.copy(result = "Ошибка: введите корректный адрес")
        }
    }

    private suspend fun getMediaData() {
        val listOfMedia: List<String> = when (imageCopyState.selectedMediaDownload) {
            MediaDownload.ALL -> mediaData.map { it.mediaUrl }
            MediaDownload.IMAGE -> mediaData.filter { !it.isVideo }.map { it.mediaUrl }
            MediaDownload.VIDEO -> mediaData.filter { it.isVideo }.map { it.mediaUrl }
            MediaDownload.NONE -> emptyList()
        }
        if (listOfMedia.isNotEmpty())
            getMediaData(listOfMedia)
    }

    private suspend fun getMediaData(listUrl: List<String>) {
        val allMediaCount = mediaData.count()

        imageCopyState =
            imageCopyState.copy(
                job = StatusJob.DOWNLOAD_MEDIA,
                result = "Найдено файлов $allMediaCount, скачиваем ${listUrl.count()}"
            )
        println("Скачиваем файлы")
        try {
            val downloaded = storage.downloadsFiles(
                listUrl,
                mediaData.first().author,
                mediaData.first().nickName,
            )
            Log.d("Instagram", "Результат скачивания файлов ${downloaded.size}")
            val downloadMediaCount = downloaded.count { it.errorMessage.isEmpty() }
            if (allMediaCount == downloadMediaCount) imageCopyState =
                imageCopyState.copy(url = "", listMedia = emptyList())
            imageCopyState = imageCopyState.copy(
                result = "Скачано файлов $downloadMediaCount из $allMediaCount",
            )
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: Throwable) {
            imageCopyState = imageCopyState.copy(
                result = "Ошибка: ${ex.localizedMessage}",
            )
        }
    }
}