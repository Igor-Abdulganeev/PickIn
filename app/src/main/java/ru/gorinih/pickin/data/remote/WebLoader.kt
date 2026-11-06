package ru.gorinih.pickin.data.remote

import android.annotation.SuppressLint
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import kotlinx.coroutines.CancellationException
import ru.gorinih.pickin.data.remote.models.NetworkResult
import ru.gorinih.pickin.data.remote.models.PageResult

/**
 * Created by Igor Abdulganeev on 20.10.2026
 */

class WebLoader(
    private val httpClient: HttpClient
) {

    @SuppressLint("SuspiciousIndentation")
    suspend fun loadPage(url: String): NetworkResult<PageResult> = try {
        val response = httpClient.get(url)
        val html = response.bodyAsText()

        Log.i("Instagram", "Final URL: ${response.request.url}")
        Log.i("Instagram", "HTTP: ${response.status.value}")
        Log.i("Instagram", "HTML length: ${html.length}")
        Log.i(
            "Instagram",
            "Contains media JSON: ${
                html.contains("xdt_api__v1__media__shortcode__web_info")
            }"
        )
        Log.i(
            "Instagram",
            "Contains carousel: ${html.contains("carousel_media")}"
        )
        Log.i(
            "Instagram",
            "Contains error page: ${html.contains("PolarisErrorRoot")}"
        )

        if (!response.status.isSuccess()) {
            NetworkResult.HttpError(
            errorCode = response.status.value,
            errorMessage = response.status.description
        ) } else {
            NetworkResult.Success(
                PageResult(
                    requestedUrl = response.request.url.toString(),
                    status = response.status.value,
                    contentType = response.headers[
                        HttpHeaders.ContentType
                    ],
                    html = html,
                )
            )
        }
    } catch (ex: CancellationException) {
        throw ex
    } catch (_: HttpRequestTimeoutException) {
        NetworkResult.Timeout()
    } catch (ex: ConnectTimeoutException) {
        NetworkResult.Timeout(ex.message)
    } catch (ex: SocketTimeoutException) {
        NetworkResult.Timeout(ex.message)
    } catch (ex: ResponseException) {
        NetworkResult.HttpError(
            errorCode = ex.response.status.value,
            errorMessage = ex.localizedMessage
        )
    } catch (ex: Throwable) {
        NetworkResult.Error(
            errorMessage = ex.localizedMessage ?: ex::class.simpleName
        )
    }
}