package ru.gorinih.instaimagecopy

import androidx.lifecycle.ViewModel
import ru.gorinih.instaimagecopy.data.remote.NetworkResult
import ru.gorinih.instaimagecopy.data.remote.WebLoader

/**
 * Created by Igor Abdulganeev on 20.10.2026
 */

class MainViewModel(
    private val webLoader: WebLoader
): ViewModel() {

    suspend fun getWebData(url: String) {
        val result = webLoader.loadPage(url = url)
        when(result){
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
    }
}