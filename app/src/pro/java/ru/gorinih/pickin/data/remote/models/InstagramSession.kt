package ru.gorinih.pickin.data.remote.models

import kotlinx.serialization.Serializable

/**
 * Created by Igor Abdulganeev on 17.11.2026
 */

@Serializable
data class InstagramSession(
    val csrfToken: String,
    val rawCookies: String
)
