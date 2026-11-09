package ru.gorinih.pickin.data.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.gorinih.pickin.ui.models.MediaDownload
import ru.gorinih.pickin.ui.models.TypeDownload

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
            pref[intPreferencesKey(TYPE_DOWNLOAD)] ?: 1
        }

    override suspend fun saveMediaDownload(value: MediaDownload) {
        dataStore.edit { pref ->
            pref[intPreferencesKey(MEDIA_DOWNLOAD)] = value.index
        }
    }

    override fun loadMediaDownload(): Flow<Int> =
        dataStore.data.map { pref ->
            pref[intPreferencesKey(MEDIA_DOWNLOAD)] ?: 0
        }

    override suspend fun saveKeyAutoComplete(value: Boolean) {
        dataStore.edit { pref ->
            pref[booleanPreferencesKey(KEY_AUTO_COMPLETE)] = value
        }
    }

    override fun loadKeyAutoComplete(): Flow<Boolean> =
        dataStore.data.map { pref ->
            pref[booleanPreferencesKey(KEY_AUTO_COMPLETE)] ?: false
        }

    companion object {
        private const val TYPE_DOWNLOAD = "instagram.type.download"
        private const val MEDIA_DOWNLOAD = "instagram.media.download"
        private const val KEY_AUTO_COMPLETE = "instagram.auto.complete"
    }
}