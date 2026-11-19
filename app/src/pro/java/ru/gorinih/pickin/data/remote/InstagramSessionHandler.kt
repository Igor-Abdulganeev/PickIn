package ru.gorinih.pickin.data.remote

import kotlinx.coroutines.flow.StateFlow
import ru.gorinih.pickin.data.remote.models.InstagramSession
import ru.gorinih.pickin.data.storage.InstagramSessionManager

/**
 * Created by Igor Abdulganeev on 18.11.2026
 */

interface InstagramSessionHandler: InstagramSessionManager {
    val session: StateFlow<InstagramSession?>
    val isSession: Boolean
    suspend fun update(newSession: InstagramSession?, isSave: Boolean = true)
}