package ru.gorinih.pickin.data.remote

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import ru.gorinih.pickin.data.remote.models.InstagramSession
import ru.gorinih.pickin.data.storage.CryptoManager
import ru.gorinih.pickin.data.storage.DataStoreRepository

/**
 * Created by Igor Abdulganeev on 17.11.2026
 */

class InstagramSessionManagerImpl(
    private val dataStore: DataStoreRepository,
    private val json: Json,
): InstagramSessionHandler {

    private val _session = MutableStateFlow<InstagramSession?>(null)
    override val session: StateFlow<InstagramSession?> = _session.asStateFlow()

    override var isSession by mutableStateOf(_session.value != null)
        private set

    override suspend fun update(newSession: InstagramSession?, isSave: Boolean) {
        if (isSave) {
            try {
                when(newSession == null) {
                    true -> {
                        dataStore.saveSession(null)
                    }
                    false -> {
                        val dataRaw = json.encodeToString(newSession)
                        val securityRaw = CryptoManager.encrypt(dataRaw)
                        dataStore.saveSession(securityRaw)
                    }
                }
            } catch (_: Throwable) {}
        }
        _session.value = newSession
        isSession = _session.value != null
    }

    override suspend fun restoreSession() {
        val securityRaw: String = dataStore.restoreSession() ?: run {
            update(null)
            return
        }
        val dataRaw = CryptoManager.decrypt(securityRaw) ?: run {
            update(null)
            return
        }
        try {
            val session = json.decodeFromString<InstagramSession>(dataRaw)
            update(session, false)
        } catch (_: Throwable) {
            update(null)
        }
    }
}