package ru.gorinih.instaimagecopy.data.remote

/**
 * Created by Igor Abdulganeev on 20.10.2026
 */
 
sealed interface NetworkResult<out T> {
    data class Success<T>(
        val data: T
    ): NetworkResult<T>

    data class HttpError(
        val codeError: Int,
        val messageError: String?
    ): NetworkResult<Nothing>

    data class Error(
        val errorMessage: String?
    ): NetworkResult<Nothing>

    data object Timeout: NetworkResult<Nothing>


}