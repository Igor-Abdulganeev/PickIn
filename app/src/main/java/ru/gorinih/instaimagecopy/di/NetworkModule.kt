package ru.gorinih.instaimagecopy.di

import io.ktor.client.HttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import ru.gorinih.instaimagecopy.data.remote.HttpClientFactory
import ru.gorinih.instaimagecopy.data.remote.InstaParser
import ru.gorinih.instaimagecopy.data.remote.WebLoader

/**
 * Created by Igor Abdulganeev on 20.10.2026
 */

val networkModule = module {
    single<HttpClient>{
        HttpClientFactory.create(false)
    }

//    single<InstaParser> { InstaParser(context = androidContext()) }
    single<WebLoader> {
        WebLoader(httpClient = get())
    }
}