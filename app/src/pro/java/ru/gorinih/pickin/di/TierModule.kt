package ru.gorinih.pickin.di

import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.binds
import org.koin.dsl.module
import ru.gorinih.pickin.data.remote.HttpClientFactory
import ru.gorinih.pickin.data.remote.InstagramSessionHandler
import ru.gorinih.pickin.data.remote.InstagramSessionManagerImpl
import ru.gorinih.pickin.data.storage.CryptoManager
import ru.gorinih.pickin.data.storage.InstagramSessionManager
import ru.gorinih.pickin.data.storage.ShareManagerImpl
import ru.gorinih.pickin.data.storage.ShareManager

/**
 * Created by Igor Abdulganeev on 17.11.2026
 */

val tierModule = module {
    single<Json> {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
            encodeDefaults = true
        }
    }

    single<CryptoManager> { CryptoManager }

    single{
        InstagramSessionManagerImpl(
            dataStore = get(),
            json = get()
        )
    } binds arrayOf(
        InstagramSessionManager::class,
        InstagramSessionHandler::class
    )

    single<HttpClient> {
        HttpClientFactory.create(
            isEnabledLogging = false,
            sessionManager = get()
        )
    }

    single<ShareManager> { ShareManagerImpl(context = androidContext(), storage = get()) }

}