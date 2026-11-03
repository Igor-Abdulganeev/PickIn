package ru.gorinih.instaimagecopy.ui.models

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentMap

/**
 * Created by Igor Abdulganeev on 21.10.2026
 */

data class UiState (
    val url: String = "",
    val job: StatusJob = StatusJob.WAITING_JOB,
    val result: ResultMessage = ResultMessage(message = 0, type = TypeResult.DONE),
    val blocks: PersistentMap<ScreenBlocks, Boolean> = mapOf<ScreenBlocks, Boolean>(
        ScreenBlocks.INPUT to true,
        ScreenBlocks.SETTINGS to false,
        ScreenBlocks.PREVIEW to true
    ).toPersistentMap(),
    val selectedTypeDownload: TypeDownload = TypeDownload.NONE,
    val selectedMediaDownload: MediaDownload = MediaDownload.NONE,
    @Stable
    val listMedia: List<MediaItem> = emptyList()
)