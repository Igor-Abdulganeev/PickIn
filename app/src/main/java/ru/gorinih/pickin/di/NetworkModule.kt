package ru.gorinih.pickin.di

import coil3.ImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.crossfade
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module
import ru.gorinih.pickin.data.remote.DownloadWorker
import ru.gorinih.pickin.domain.remote.StarterDownloadWorker
import ru.gorinih.pickin.data.remote.StarterDownloadWorkerImpl
import ru.gorinih.pickin.data.remote.WebLoader

/**
 * Created by Igor Abdulganeev on 20.10.2026
 */

val networkModule = module {
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
                    .maxSizePercent(androidContext(), 0.25)//  25% от доступной приложению памяти под картинки
                    .strongReferencesEnabled(true) // Запрещаем GC агрессивно удалять картинки
                    .build()
            }
             */
            .crossfade(true)
            .build()
    }

    worker {
        DownloadWorker(
            context = androidContext(),
            params = get(),
            storage = get(),
            json = get()
        )
    }

    factory<StarterDownloadWorker> { StarterDownloadWorkerImpl(context = androidContext(), json = get()) }

}