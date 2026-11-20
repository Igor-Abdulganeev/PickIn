package ru.gorinih.pickin.di

import android.net.Uri
import io.ktor.client.HttpClient
import org.koin.dsl.module
import ru.gorinih.pickin.data.remote.HttpClientFactory
import ru.gorinih.pickin.data.storage.InstagramSessionManager
import ru.gorinih.pickin.data.storage.ShareManager
import ru.gorinih.pickin.data.storage.models.SavedFile

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

    single<ShareManager> {
        object : ShareManager {
            override val accessAllowed: Boolean = false
            override suspend fun shareMedia(
                url: String,
                contentUri: Uri,
                typeMime: String?
            ): SavedFile? = null

            override suspend fun getAndShareMedia(url: String): SavedFile? = null
        }
    }
}