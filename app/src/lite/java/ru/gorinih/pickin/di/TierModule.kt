package ru.gorinih.pickin.di

import io.ktor.client.HttpClient
import org.koin.dsl.module
import ru.gorinih.pickin.data.remote.HttpClientFactory
import ru.gorinih.pickin.data.storage.InstagramSessionManager

/**
 * Created by Igor Abdulganeev on 17.11.2026
 */

val tierModule = module {
    single<HttpClient>{
        HttpClientFactory.create(
            isEnabledLogging = false
            )
    }

    single<InstagramSessionManager> {
        object : InstagramSessionManager {
            override suspend fun restoreSession() {}
        }
    }
}