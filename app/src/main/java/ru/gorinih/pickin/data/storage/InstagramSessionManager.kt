package ru.gorinih.pickin.data.storage

/**
 * Created by Igor Abdulganeev on 18.11.2026
 */

interface InstagramSessionManager {
    suspend fun restoreSession()
}