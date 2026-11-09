package ru.gorinih.pickin.ui.models

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
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
    val selectedTypeDownload: TypeDownload = TypeDownload.MANUAL,
    val selectedMediaDownload: MediaDownload = MediaDownload.ALL,
    val selectedAutoComplete: Boolean = false,
    @Stable
    val listMedia: List<MediaItem> = emptyList(),
    val selectedList: ImmutableList<String>
)