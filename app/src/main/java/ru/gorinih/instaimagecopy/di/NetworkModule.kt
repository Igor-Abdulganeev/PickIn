package ru.gorinih.instaimagecopy.di

import coil3.ImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.crossfade
import io.ktor.client.HttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ru.gorinih.instaimagecopy.data.remote.HttpClientFactory
import ru.gorinih.instaimagecopy.data.remote.WebLoader

/**
 * Created by Igor Abdulganeev on 20.10.2026
 */

val networkModule = module {
    single<HttpClient>{
        HttpClientFactory.create(false)
    }

    single<WebLoader> {
        WebLoader(httpClient = get())
    }

    single<ImageLoader> {
        ImageLoader.Builder(context = androidContext())
            .components {
                add(OkHttpNetworkFetcherFactory())
            }
            /*
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(androidContext(), 0.25)// Выделяем стабильные 25% от доступной приложению памяти под картинки
                    .strongReferencesEnabled(true) // Запрещаем GC агрессивно удалять картинки
                    .build()
            }
             */
            .crossfade(true)
            .build()
    }
}