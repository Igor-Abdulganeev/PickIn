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
import kotlinx.coroutines.launch
import ru.gorinih.instaimagecopy.data.parsers.InstagramHtmlParser
import ru.gorinih.instaimagecopy.data.remote.models.NetworkResult
import ru.gorinih.instaimagecopy.data.remote.WebLoader
import ru.gorinih.instaimagecopy.data.storage.InstagramStorage
import ru.gorinih.instaimagecopy.ui.models.ScreenBlocks
import ru.gorinih.instaimagecopy.ui.models.StatusJob
import ru.gorinih.instaimagecopy.ui.models.UiState
import ru.gorinih.instaimagecopy.ui.models.toMediaItem

/**
 * Created by Igor Abdulganeev on 20.10.2026
 */

class ImageCopyViewModel(
    private val webLoader: WebLoader,
    private val parser: InstagramHtmlParser,
    private val storage: InstagramStorage,
): ViewModel() {

    var imageCopyState by mutableStateOf(UiState(
       // url = "https://phet-dev.colorado.edu/html/build-an-atom/0.0.0-3/simple-text-only-test-page.html"
        url = "https://www.instagram.com/p/DesFNjnDQgL/?img_index=1"
    ))
        private set

    val coroutineExceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        when(throwable) {
            is CancellationException -> throw throwable
            else -> println("Глобальная корутина $throwable")
        }
    }

    fun onChangeUrl(newUrl: String) {
        imageCopyState = imageCopyState.copy(url = newUrl)
    }

    fun rotateVisibilitySettings() {
        val visibility = imageCopyState.blocks[ScreenBlocks.SETTINGS] ?: false
        imageCopyState = imageCopyState.copy(
            blocks = imageCopyState.blocks.putting(ScreenBlocks.SETTINGS, !visibility)
        )
    }

    fun getWebData() {
        imageCopyState = imageCopyState.copy(job = StatusJob.DOWNLOAD)
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            try {
                 when (val result = webLoader.loadPage(url = imageCopyState.url)) {
                    is NetworkResult.Timeout -> {
                        println("Запрос не вернул результат за отведенное время ${result.errorMessage}")
                    }

                    is NetworkResult.Error -> {
                        println("Ошибка ${result.errorMessage}")
                    }

                    is NetworkResult.Success -> {
                         val page = result.data
                        println("Страница скачана:")

                        println("URL: ${page.requestedUrl}")
                        println("HTTP: ${page.status}")
                        println("Content-Type: ${page.contentType}")
                        println("HTML length: ${page.html.length}")
                        println("HTML: ${page.html.take(300)}")

                        // Передаём HTML в парсер.
                       // storage.saveHtmlToFile(page.html) // сохранить страничку если надо
                        val postJson = parser.parsePost(url = imageCopyState.url, html =  page.html)

                        if (postJson != null) {
                            println("JSON публикации найден:")
                            val items = parser.parseCarousel(postJson).map { it.toMediaItem() }
                            Log.i("Instagram", "Изображений найдено: ${items.size}")
                            val listOfMedia = mutableListOf<String>()
                            items.forEachIndexed { index, item ->
                                listOfMedia.add(item.previewUrl) // TODO  для скорости сохраняем превьюшки
                                Log.d("Instagram", "автор=${item.author}")
                                Log.i(
                                    "Instagram",
                                    "Элемент ${index + 1}: " +
                                            "video=${item.isVideo}, " +
                                            "preview=${item.previewUrl}, " +
                                            "media=${item.mediaUrl}"
                                )
                            }

                            println("Скачиваем файлы")
                            val downloaded = storage.downloadsFiles(listOfMedia, items.first().author)
                            Log.d("Instagram", "Результат скачивания файлов ${downloaded.size}")
                            downloaded.forEach { item ->
                                Log.i("Instagram", "Файл ${item.file}")
                                Log.i("Instagram", "    uri: ${item.uri}")
                                Log.i("Instagram", "    ошибка: ${item.errorMessage}")
                                Log.i("Instagram", "*********************************")
                            }

                        }else {
                            println("Не удалось найти JSON публикации")
                        }
                    }

                    is NetworkResult.HttpError -> {
                        println("Ошибка http ${result.errorCode} / ${result.errorMessage}")
                    }
                }
            } finally {
                imageCopyState = imageCopyState.copy(job = StatusJob.WAITING) // todo изменить потом, менять в разделке
            }
        }
    }
}