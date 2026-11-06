package ru.gorinih.pickin.ui.models

import ru.gorinih.pickin.data.parsers.models.CarouselItem

/**
 * Created by Igor Abdulganeev on 26.10.2026
 */

data class MediaItem(
    val isVideo: Boolean = false,
    val previewUrl: String = "",
    val mediaUrl: String = "",
    val width: Int = 0,
    val height: Int = 0,
    val author: String = "",
    val nickName: String = ""
)

fun CarouselItem.toMediaItem() =
    MediaItem(
        isVideo = this.isVideo,
        previewUrl = this.previewUrl ?: "",
        mediaUrl = this.mediaUrl ?: "",
        width = this.width ?: 0,
        height = this.height ?: 0,
        author = this.author ?: "",
        nickName = this.nickName ?: ""
    )
