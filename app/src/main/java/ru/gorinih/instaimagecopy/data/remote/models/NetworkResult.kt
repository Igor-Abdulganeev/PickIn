package ru.gorinih.instaimagecopy.data.remote.models

/**
 * Created by Igor Abdulganeev on 20.10.2026
 */

sealed interface NetworkResult<out T> {
    data class Success<T>(
        val data: T
    ) : NetworkResult<T>

    data class HttpError(
        val errorCode: Int,
        val errorMessage: String?
    ) : NetworkResult<Nothing>

    data class Error(
        val errorMessage: String?
    ) : NetworkResult<Nothing>

    data class Timeout(val errorMessage: String? = null) : NetworkResult<Nothing>


}