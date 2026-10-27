package ru.gorinih.instaimagecopy.data.remote

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
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

    fun create(
        isEnabledLogging: Boolean = true
    ): HttpClient = HttpClient(OkHttp) {

        expectSuccess = false
        followRedirects = true

        install(DefaultRequest) {
            header(
                HttpHeaders.UserAgent,
                "Mozilla/5.0 (Linux; Android 14) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) " +
                        "Chrome/131.0.0.0 Mobile Safari/537.36"
            )

            header(
                HttpHeaders.Accept,
                "text/html,application/xhtml+xml," +
                        "application/xml;q=0.9,*/*;q=0.8"
            )

            header(
                HttpHeaders.AcceptLanguage,
                "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7"
            )

            header(HttpHeaders.Referrer, "https://www.instagram.com/")

            header("Sec-Fetch-Dest", "document")
            header("Sec-Fetch-Mode", "navigate")
            header("Sec-Fetch-Site", "same-origin")
            header("Sec-Fetch-User", "?1")
        }

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
                        Log.d("IIC", message)
                    }
                }
                level = LogLevel.ALL
            }
        }
    }
}