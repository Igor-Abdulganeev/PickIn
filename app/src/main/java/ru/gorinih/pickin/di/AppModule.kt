package ru.gorinih.pickin.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.gorinih.pickin.ImageCopyViewModel
import ru.gorinih.pickin.data.parsers.InstagramHtmlParser
import ru.gorinih.pickin.domain.storage.DataStoreRepository
import ru.gorinih.pickin.data.storage.DataStoreRepositoryImpl
import ru.gorinih.pickin.data.storage.InstagramStorage
import ru.gorinih.pickin.domain.storage.createDataStore

/**
 * Created by Igor Abdulganeev on 20.10.2026
 */

val appModule = module {

    single<Json> {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
            encodeDefaults = true
        }
    }

    single<InstagramHtmlParser> { InstagramHtmlParser() }

    single<DataStore<Preferences>> { createDataStore(context = androidContext()) }

    factory<InstagramStorage> { InstagramStorage(context = androidContext(), httpClient = get()) }

    factory<DataStoreRepository> { DataStoreRepositoryImpl(dataStore = get()) }

    viewModel<ImageCopyViewModel> {
        ImageCopyViewModel(
            webLoader = get(),
            parser = get(),
            storage = get(),
            dataStore = get(),
            session = get(),
            shareManager = get(),
            downloader = get(),
        )
    }

}