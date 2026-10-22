package ru.gorinih.instaimagecopy.ui.models

import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.collections.immutable.toPersistentMap

/**
 * Created by Igor Abdulganeev on 21.10.2026
 */

data class UiState (
    val url: String = "",
    val job: StatusJob = StatusJob.WAITING,
    val blocks: PersistentMap<ScreenBlocks, Boolean> = mapOf<ScreenBlocks, Boolean>(
        ScreenBlocks.INPUT to true,
        ScreenBlocks.SETTINGS to true
    ).toPersistentMap()
)

enum class StatusJob{
    WAITING,
    DOWNLOAD,
    SEPARATE,
}

enum class ScreenBlocks {
    INPUT,
    SETTINGS,
}


