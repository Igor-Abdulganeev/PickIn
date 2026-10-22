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
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.Dns
import okhttp3.Protocol

/**
 * Created by Igor Abdulganeev on 20.10.2026
 */

object HttpClientFactory {

    fun create(
        isEnabledLogging: Boolean = true
    ): HttpClient = HttpClient(OkHttp) {

        expectSuccess = true
        followRedirects = false

        engine {
            config {
                // Заставляем OkHttp использовать системную прокси-конфигурацию Android
                proxy(java.net.Proxy.NO_PROXY) // Или настроить через ProxySelector
                // Альтернативно, можно явно передать ProxySelector:
                proxySelector(java.net.ProxySelector.getDefault())

                // Принудительно фильтруем только IPv4 адреса, так как VPN часто ломает IPv6
                dns { hostname ->
                    Dns.SYSTEM.lookup(hostname).filterIsInstance<java.net.Inet4Address>()
                }
                // Явно указываем поддерживать HTTP/2 и HTTP/1.1
                protocols(listOf(Protocol.HTTP_2, Protocol.HTTP_1_1))

                addNetworkInterceptor { chain ->
                    val originalRequest = chain.request()
                    if (originalRequest.method == "GET") {
                        val newRequest = originalRequest.newBuilder()
                            .removeHeader("Content-Length")
                            .build()
                        chain.proceed(newRequest)
                    } else {
                        chain.proceed(originalRequest)
                    }
                }
            }
        }
        install(DefaultRequest) {

            header(
                "User-Agent",
                "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Mobile Safari/537.36"
            )
            header(
                "Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8"
            )
            header("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7")
            header(
                "sec-ch-ua",
                "\"Chromium\";v=\"130\", \"Google Chrome\";v=\"130\", \"Not?A_Brand\";v=\"99\""
            )
            header("sec-ch-ua-mobile", "?1")
            header("sec-ch-ua-platform", "\"Android\"")
            header("Sec-Fetch-Dest", "document")
            header("Sec-Fetch-Mode", "navigate")
            header("Sec-Fetch-Site", "none")
            header("Sec-Fetch-User", "?1")
            header("Upgrade-Insecure-Requests", "1")
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