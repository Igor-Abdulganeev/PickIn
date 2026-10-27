package ru.gorinih.instaimagecopy.data.remote.models

/**
 * Created by Igor Abdulganeev on 23.10.2026
 */

data class PageResult(
    val requestedUrl: String,
    val status: Int,
    val contentType: String?,
    val html: String,
    val error: String? = null,
)