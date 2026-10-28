package ru.gorinih.instaimagecopy.data.parsers

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import ru.gorinih.instaimagecopy.data.parsers.models.CarouselItem
import ru.gorinih.instaimagecopy.data.parsers.models.CarouselSearchResult

/**
 * Created by Igor Abdulganeev on 23.10.2026
 */

class InstagramHtmlParser {

    private val json = Json

    private val scriptRegex = Regex(
        """<script\b(?=[^>]*\btype\s*=\s*["']application/json["'])(?=[^>]*\bdata-sjs\b)[^>]*>(.*?)</script\s*>""",
        setOf(
            RegexOption.IGNORE_CASE,
            RegexOption.DOT_MATCHES_ALL
        )
    )

    fun parsePost(
        url: String,
        html: String
    ): CarouselSearchResult {
        val shortcode = extractShortcode(url) ?: return CarouselSearchResult.NotFound
        return findPostJson(
            html = html,
            shortcode = shortcode
        )
    }

    fun parseCarousel(
        post: JsonObject
    ): List<CarouselItem> {
        val author = extractAuthor(post)

        return extractCarouselMedia(post)
            .map { media -> parseCarouselItem(media, author)}
    }

    private fun findPostJson(
        html: String,
        shortcode: String
    ): CarouselSearchResult {
        for (match in scriptRegex.findAll(html)) {
            val scriptContent = match.groupValues[1]

            val root = try {
                json.parseToJsonElement(scriptContent)
            } catch (_: SerializationException) {
                continue
            }

            // залогиненый формат Instagram, т.к. пока без логина то скроем
/*
            val postInfo = findProperty(
                element = root,
                propertyName = "xdt_api__v1__media__shortcode__web_info"
            )

            val items = postInfo?.let {
                findProperty(it, "items") as? JsonArray
            }

            val loginPost = items?.firstOrNull { item ->
                item.jsonObjectOrNull()
                    ?.get("code")
                    ?.jsonPrimitive
                    ?.contentOrNull == shortcode
            }?.jsonObjectOrNull()
                ?: items?.singleOrNull()?.jsonObjectOrNull()

            if (loginPost != null) { return CarouselSearchResult.Found(loginPost) }

 */

            // без логина формат: ищем объект, содержащий carousel_media.
            val unLoginPost = findObjectWithCarousel(
                element = root,
                shortcode = shortcode
            )

            if (unLoginPost !is CarouselSearchResult.NotFound) {
                return unLoginPost
            }
        }

        return CarouselSearchResult.NotFound
    }

    private fun extractShortcode(url: String): String? {
        val regex = Regex(
            """instagram\.com/(?:p|reel|tv)/([^/?#]+)"""
        )

        return regex.find(url)?.groupValues?.get(1)
    }

    private fun findProperty(
        element: JsonElement,
        propertyName: String
    ): JsonElement? = when (element) {
        is JsonObject -> {
            element[propertyName] ?: element.values.firstNotNullOfOrNull { value ->
                findProperty(value, propertyName)
            }
        }

        is JsonArray -> {
            element.firstNotNullOfOrNull { child ->
                findProperty(child, propertyName)
            }
        }

        else -> null
    }

    private fun JsonElement.jsonObjectOrNull(): JsonObject? =
        try {
            jsonObject
        } catch (_: IllegalArgumentException) {
            null
        }

     private fun findObjectWithCarousel(
        element: JsonElement,
        shortcode: String
    ): CarouselSearchResult {
        when (element) {
            is JsonObject -> {
                val code = element["code"]
                    ?.jsonPrimitive
                    ?.contentOrNull

                val carousel = element["carousel_media"] as? JsonArray
                val hasImageVersions = element["image_versions2"] is JsonObject
                val hasVideoVersions = element["video_versions"] is JsonArray
                val hasDisplayUri = element["display_uri"]
                    ?.jsonPrimitive
                    ?.contentOrNull
                    ?.isNotBlank() == true
                val mediaType = element["media_type"]
                    ?.jsonPrimitive
                    ?.intOrNull

                if ( code == shortcode &&
                        (carousel != null || hasImageVersions || hasVideoVersions || hasDisplayUri || mediaType == 1 || mediaType == 2 || mediaType == 8)) {
                    return CarouselSearchResult.Found(element)
                }

                val gatingRuling = element["gating_ruling"]
                    ?.takeUnless { it is JsonNull }

                val mediaData = element["if_not_gated_logged_out"]

                if (
                    code == shortcode &&
                    gatingRuling != null &&
                    mediaData is JsonNull
                ) {
                    return CarouselSearchResult.AgeRestricted
                }

                for (value in element.values) {
                    val result = findObjectWithCarousel(value, shortcode)

                    if (result !is CarouselSearchResult.NotFound) {
                        return result
                    }
                }
            }

            is JsonArray -> {
                for (value in element) {
                    val result = findObjectWithCarousel(value, shortcode)

                    if (result !is CarouselSearchResult.NotFound) {
                        return result
                    }
                }
            }

            else -> Unit
        }

        return CarouselSearchResult.NotFound
    }

    private fun extractAuthor(post: JsonObject): String? {
        val user = post["user"] as? JsonObject ?: return null

        return user["username"]
            ?.jsonPrimitive
            ?.contentOrNull
            ?.takeIf { it.isNotBlank() }
            ?: user["full_name"]
                ?.jsonPrimitive
                ?.contentOrNull
                ?.takeIf { it.isNotBlank() }
    }

    private fun extractCarouselMedia(
        post: JsonObject
    ): List<JsonObject> =
        (post["carousel_media"] as? JsonArray)?.filterIsInstance<JsonObject>() ?: listOf(post)

    private fun extractImageUrl(
        media: JsonObject
    ): String? {
        val candidates = media["image_versions2"]
            ?.jsonObjectOrNull()
            ?.get("candidates") as? JsonArray

        val candidateUrl = candidates
            ?.firstOrNull()
            ?.jsonObjectOrNull()
            ?.get("url")
            ?.jsonPrimitive
            ?.contentOrNull

        if (!candidateUrl.isNullOrBlank()) {
            return candidateUrl
        }

        return media["display_uri"]
            ?.jsonPrimitive
            ?.contentOrNull
    }

    private fun extractVideoUrl(media: JsonObject): String? {
        val versions = media["video_versions"] as? JsonArray
            ?: return null
        return versions
            .mapNotNull { it.jsonObjectOrNull() }
            .maxByOrNull { version ->
                val width = version["width"]
                    ?.jsonPrimitive?.intOrNull ?: 0
                val height = version["height"]
                    ?.jsonPrimitive?.intOrNull ?: 0

                width * height
            }
            ?.get("url")
            ?.jsonPrimitive
            ?.contentOrNull
    }

    private fun extractPreviewUrl(media: JsonObject): String? {
        val candidates = media["image_versions2"]
            ?.jsonObjectOrNull()
            ?.get("candidates") as? JsonArray
            ?: return extractDisplayUri(media)
        return candidates
            .mapNotNull { it.jsonObjectOrNull() }
            .maxByOrNull { candidate ->
                val width = candidate["width"]
                    ?.jsonPrimitive?.intOrNull ?: 0
                val height = candidate["height"]
                    ?.jsonPrimitive?.intOrNull ?: 0

                width * height
            }
            ?.get("url")
            ?.jsonPrimitive
            ?.contentOrNull
    }

    private fun extractDisplayUri(media: JsonObject): String? =
        media["display_uri"]
            ?.jsonPrimitive
            ?.contentOrNull

    private fun isVideo(media: JsonObject): Boolean {
        val typeName = media["__typename"]
            ?.jsonPrimitive
            ?.contentOrNull
        if (typeName?.contains("Video", ignoreCase = true) == true) return true

        val mediaType = media["media_type"]
            ?.jsonPrimitive
            ?.intOrNull
        return mediaType == 2 || media["video_versions"] is JsonArray
    }

    private fun parseCarouselItem(media: JsonObject, author: String?): CarouselItem {
        val isVideo = isVideo(media)
        return CarouselItem(
            isVideo = isVideo,
            previewUrl = extractPreviewUrl(media),
            mediaUrl = if (isVideo) {
                extractVideoUrl(media)
            } else {
                extractImageUrl(media)
            },
            width = media["original_width"]?.jsonPrimitive?.intOrNull,
            height = media["original_height"]?.jsonPrimitive?.intOrNull,
            author = author
        )
    }
}
