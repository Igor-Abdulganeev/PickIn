package ru.gorinih.instaimagecopy.data.parsers.models

import kotlinx.serialization.json.JsonObject

/**
 * Created by Igor Abdulganeev on 27.10.2026
 */

sealed interface CarouselSearchResult {
    data class Found(val media: JsonObject) : CarouselSearchResult
    data object AgeRestricted : CarouselSearchResult
    data object NotFound : CarouselSearchResult
}