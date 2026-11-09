package ru.gorinih.pickin.ui.models

/**
 * Created by Igor Abdulganeev on 06.11.2026
 */

sealed interface EventImageCopy {

    data object GetPostData: EventImageCopy
    data object GetSelectedData: EventImageCopy
    data object RotateVisibilityHelp: EventImageCopy
    data object RotateVisibilityPreview: EventImageCopy
    data object RotateVisibilitySettings: EventImageCopy
    data class OnChangeUrl(val newUrl: String, val startIfPossibly: Boolean = false): EventImageCopy
    data class OnChangeTypeDownload(val index: Int): EventImageCopy
    data class OnChangeMediaDownload(val index: Int): EventImageCopy
    data class OnChangeAutoComplete(val value: Boolean): EventImageCopy
    data class SelectData(val previewUrl: String): EventImageCopy
    data class ClearData(val url: String = ""): EventImageCopy
}