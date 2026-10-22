package ru.gorinih.instaimagecopy

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import ru.gorinih.instaimagecopy.data.remote.NetworkResult
import ru.gorinih.instaimagecopy.data.remote.WebLoader
import ru.gorinih.instaimagecopy.ui.models.ScreenBlocks
import ru.gorinih.instaimagecopy.ui.models.StatusJob
import ru.gorinih.instaimagecopy.ui.models.UiState

/**
 * Created by Igor Abdulganeev on 20.10.2026
 */

class ImageCopyViewModel(
    private val webLoader: WebLoader
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
                        println("Запрос не вернул результат за отведенное время")
                    }

                    is NetworkResult.Error -> {
                        println("Ошибка ${result.errorMessage}")
                    }

                    is NetworkResult.Success<String> -> {
                        println("Страница скачана:")
                        println(result.data)
                    }

                    is NetworkResult.HttpError -> {
                        println("Ошибка http ${result.codeError} / ${result.messageError}")
                    }
                }
            } finally {
                imageCopyState = imageCopyState.copy(job = StatusJob.WAITING) // todo убрать потом, менять в разделке
            }
        }
    }
}