package ru.gorinih.instaimagecopy.data.remote

import android.annotation.SuppressLint
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.CancellationException

/**
 * Created by Igor Abdulganeev on 20.10.2026
 */

class WebLoader(
    private val httpClient: HttpClient
) {

    @SuppressLint("SuspiciousIndentation")
    suspend fun loadPage(url: String): NetworkResult<String> = try {
        val response = httpClient.get(url)
        println("RESPONSE STATUS = ${response.status.isSuccess()}")
        if (!response.status.isSuccess()) NetworkResult.HttpError(
            codeError = response.status.value,
            messageError = response.status.description
        ) else
        NetworkResult.Success(response.bodyAsText())
    } catch (ex: CancellationException) {
        throw ex
    } catch (_: HttpRequestTimeoutException) {
        NetworkResult.Timeout
    } catch (ex: ResponseException) {
        NetworkResult.HttpError(
            codeError = ex.response.status.value,
            messageError = ex.localizedMessage
        )
    } catch (ex: Throwable) {
        NetworkResult.Error(
            errorMessage = ex.localizedMessage
        )
    }
}