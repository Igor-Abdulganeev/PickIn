package ru.gorinih.instaimagecopy.data.parsers.models

/**
 * Created by Igor Abdulganeev on 26.10.2026
 */

data class CarouselItem(
    val isVideo: Boolean,
    val previewUrl: String?,
    val mediaUrl: String?,
    val width: Int?,
    val height: Int?,
    val author: String?
)
