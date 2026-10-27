package ru.gorinih.instaimagecopy.di

import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.gorinih.instaimagecopy.ImageCopyViewModel
import ru.gorinih.instaimagecopy.data.parsers.InstagramHtmlParser
import ru.gorinih.instaimagecopy.data.storage.InstagramStorage

/**
 * Created by Igor Abdulganeev on 20.10.2026
 */

val appModule = module {

    single<InstagramHtmlParser> { InstagramHtmlParser() }

    factory<InstagramStorage> { InstagramStorage(context = androidContext(), httpClient = get()) }

    viewModel<ImageCopyViewModel> { ImageCopyViewModel(webLoader = get(), parser = get(), storage = get()) }
}