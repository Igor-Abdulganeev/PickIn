package ru.gorinih.instaimagecopy.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Created by Igor Abdulganeev on 20.10.2026
 */

object HttpClientFactory {

    fun create(
        isEnabledLogging: Boolean = true
    ): HttpClient = HttpClient {

      //  expectSuccess = true

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
                logger = Logger.DEFAULT
                level = LogLevel.INFO

/*
                sanitizeHeader { header ->
                    header == HttpHeaders.Authorization ||
                            header == HttpHeaders.Cookie ||
                            header == HttpHeaders.SetCookie

                }
*/
                sanitizeHeader { header ->
                    header.equals(
                        "Authorization",
                        ignoreCase = true
                    ) || header.equals(
                        "Cookie",
                        ignoreCase = true
                    ) || header.equals(
                        "Set-Cookie",
                        ignoreCase = true
                    )
                }
            }
        }
    }
}