package ru.gorinih.instaimagecopy.data.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.gorinih.instaimagecopy.ui.models.MediaDownload
import ru.gorinih.instaimagecopy.ui.models.TypeDownload

/**
 * Created by Igor Abdulganeev on 29.10.2026
 */

class DataStoreRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
): DataStoreRepository {
    override suspend fun saveTypeDownload(value: TypeDownload) {
        dataStore.edit { pref ->
            pref[intPreferencesKey(TYPE_DOWNLOAD)] = value.index
        }
    }

    override fun loadTypeDownload(): Flow<Int> =
        dataStore.data.map { pref ->
            pref[intPreferencesKey(TYPE_DOWNLOAD)] ?: 100
        }

    override suspend fun saveMediaDownload(value: MediaDownload) {
        dataStore.edit { pref ->
            pref[intPreferencesKey(MEDIA_DOWNLOAD)] = value.index
        }
    }

    override fun loadMediaDownload(): Flow<Int> =
        dataStore.data.map { pref ->
            pref[intPreferencesKey(MEDIA_DOWNLOAD)] ?: 100
        }

    companion object {
        private const val TYPE_DOWNLOAD = "instagram.type.download"
        private const val MEDIA_DOWNLOAD = "instagram.media.download"
    }
}