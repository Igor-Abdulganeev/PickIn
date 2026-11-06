package ru.gorinih.pickin.data.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow
import ru.gorinih.pickin.ui.models.MediaDownload
import ru.gorinih.pickin.ui.models.TypeDownload

/**
 * Created by Igor Abdulganeev on 29.10.2026
 */

const val SETTINGS_DATA_STORE = "instagram.settings.data.store.preferences_pb"

fun createDataStore(context: Context): DataStore<Preferences> =
    PreferenceDataStoreFactory.create {
        context.filesDir.resolve(SETTINGS_DATA_STORE)
    }

interface DataStoreRepository {
    suspend fun saveTypeDownload(value: TypeDownload)
    fun loadTypeDownload(): Flow<Int>
    suspend fun saveMediaDownload(value: MediaDownload)
    fun loadMediaDownload(): Flow<Int>
    suspend fun saveKeyAutoComplete(value: Boolean)
    fun loadKeyAutoComplete(): Flow<Boolean>
}