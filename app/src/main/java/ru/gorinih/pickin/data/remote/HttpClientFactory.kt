package ru.gorinih.pickin.data.remote

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Created by Igor Abdulganeev on 20.10.2026
 */

object HttpClientFactory {
    private const val DESKTOP_UA =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/131.0.0.0 Safari/537.36"

    fun create(
        isEnabledLogging: Boolean = false
    ): HttpClient = HttpClient(OkHttp) {

        expectSuccess = false
        followRedirects = true

        install(DefaultRequest) {
            header(HttpHeaders.UserAgent, DESKTOP_UA)

            header("Sec-CH-UA", "\"Chromium\";v=\"131\", \"Not_A Brand\";v=\"24\", \"Google Chrome\";v=\"131\"")
            header("Sec-CH-UA-Mobile", "?0")
            header("Sec-CH-UA-Platform", "\"Windows\"")
            header("Sec-CH-UA-Platform-Version", "\"15.0.0\"")
            header("Sec-CH-UA-Arch", "\"x86\"")
            header("Sec-CH-UA-Bitness", "\"64\"")
            header("Sec-CH-UA-Full-Version-List",
                "\"Chromium\";v=\"131.0.6778.86\", \"Not_A Brand\";v=\"24.0.0.0\", \"Google Chrome\";v=\"131.0.6778.86\"")

            header("Sec-Fetch-Dest", "document")
            header("Sec-Fetch-Mode", "navigate")
            header("Sec-Fetch-Site", "none")
            header("Sec-Fetch-User", "?1")

            header("Upgrade-Insecure-Requests", "1")
            header(HttpHeaders.Accept,
                "text/html,application/xhtml+xml,application/xml;q=0.9," +
                        "image/avif,image/webp,image/apng,*/*;q=0.8," +
                        "application/signed-exchange;v=b3;q=0.7")

            header(HttpHeaders.AcceptLanguage, "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7")
            header("Priority", "u=0, i")
        }

        install(HttpCookies)

        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = false
                    encodeDefaults = true
                    explicitNulls = false
                }
            )
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 60_000
            connectTimeoutMillis = 15_000
            socketTimeoutMillis = 30_000
        }

        if (isEnabledLogging) {
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        val chunk = 3_900
                        when(message.length < chunk) {
                            true -> Log.d("Instagram", message)
                            false -> message.chunked(chunk).forEachIndexed {index, part ->
                                Log.d("Instagram", "${index+1} $part")
                            }
                        }
                    }
                }
                level = LogLevel.HEADERS
            }
        }
    }
}