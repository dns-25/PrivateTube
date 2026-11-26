package com.privatetube.app.core.parsing

import com.privatetube.app.core.network.SiteConfig
import com.privatetube.app.core.network.SiteException
import com.privatetube.app.domain.StreamOption
import com.privatetube.app.domain.Unavailable
import com.privatetube.app.domain.VideoDetails
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.jsoup.Jsoup

/**
 * Reads the player configuration the desktop video page embeds (`flashvars_<id>`), without building a DOM for
 * the whole ~1.4 MB page: only the `mediaDefinitions` array and a few scalar fields are extracted.
 */
object VideoPageParser {
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    fun parse(html: String, viewKey: String): VideoDetails {
        val start = ParsingRules.FLASHVARS_START.find(html)
            ?: throw SiteException.ParseFailed("no player data on the video page")
        // The flashvars object is large; scalar fields we need sit before/around mediaDefinitions.
        val window = html.substring(start.range.last, minOf(html.length, start.range.last + 200_000))

        val unavailable = scalar(window, "video_unavailable") == "true" ||
            scalar(window, "video_unavailable_country") == "true"
        val streams = parseStreams(window)
        val title = unescape(scalar(window, "video_title"))
            ?: Jsoup.parse(html.substring(0, minOf(html.length, 20_000)))
                .selectFirst("meta[property=og:title]")?.attr("content")
            ?: "Video"

        val head = Jsoup.parse(html.substring(0, minOf(html.length, 30_000)))
        val thumb = unescape(scalar(window, "image_url"))
            ?: head.selectFirst("meta[property=og:image]")?.attr("content")?.takeIf { it.isNotBlank() }

        // Optional metadata that only exists in the page body. Parsing the whole 1.4 MB page into a DOM is
        // too heavy for budget TV boxes, so only small slices around known markers are parsed.
        val views = slice(html, ParsingRules.VIEWS_MARKER, 600)
            ?.let { Jsoup.parseBodyFragment(it).selectFirst(".views .count")?.text()?.trim() }
            ?.takeIf { it.isNotEmpty() }
        val author = ParsingRules.CHANNEL_MARKERS.firstNotNullOfOrNull { marker ->
            slice(html, marker, ParsingRules.AUTHOR_SLICE_LENGTH)?.let { part ->
                val fragment = Jsoup.parseBodyFragment(part)
                fragment.select(ParsingRules.AUTHOR_LINK).firstOrNull { it.text().isNotBlank() }?.let { link ->
                    link to fragment.selectFirst(ParsingRules.AUTHOR_AVATAR)?.attr("src")?.takeIf { it.startsWith("http") }
                }
            }
        }
        val channel = author?.first

        return VideoDetails(
            viewKey = viewKey,
            title = title,
            thumbUrl = thumb,
            durationSeconds = scalar(window, "video_duration")?.toIntOrNull(),
            views = views,
            channelName = channel?.text()?.trim()?.takeIf { it.isNotEmpty() },
            channelPath = channel?.attr("href")?.substringBefore('?')?.takeIf { it.isNotBlank() }?.let { href ->
                if (href.endsWith("/videos")) href else href.trimEnd('/') + "/videos"
            },
            channelAvatarUrl = author?.second,
            streams = streams,
            unavailableReason = when {
                unavailable -> Unavailable.Removed
                streams.isEmpty() -> Unavailable.NoStreams
                else -> null
            },
        )
    }

    internal fun parseStreams(window: String): List<StreamOption> {
        val keyIdx = window.indexOf(ParsingRules.MEDIA_DEFINITIONS_KEY)
        if (keyIdx < 0) return emptyList()
        val arrStart = window.indexOf('[', keyIdx)
        if (arrStart < 0) return emptyList()
        val arrEnd = findBalancedEnd(window, arrStart, '[', ']')
        if (arrEnd < 0) return emptyList()
        val array = try {
            json.parseToJsonElement(window.substring(arrStart, arrEnd + 1)) as? JsonArray ?: return emptyList()
        } catch (e: Exception) {
            throw SiteException.ParseFailed("quality list")
        }
        return array.mapNotNull { el ->
            val obj = el as? JsonObject ?: return@mapNotNull null
            if (obj["format"]?.jsonPrimitive?.contentOrNull != "hls") return@mapNotNull null
            val url = obj["videoUrl"]?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() }
                ?: return@mapNotNull null
            val height = obj["height"]?.jsonPrimitive?.intOrNull
                ?: (obj["quality"] as? JsonPrimitive)?.contentOrNull?.toIntOrNull()
                ?: return@mapNotNull null
            StreamOption(height, SiteConfig.absolute(url))
        }.distinctBy { it.height }.sortedByDescending { it.height }
    }

    /** Index of the bracket that closes the one at [start], skipping brackets inside JSON strings; -1 if unbalanced. */
    internal fun findBalancedEnd(text: String, start: Int, open: Char, close: Char): Int {
        var depth = 0
        var inString = false
        var i = start
        while (i < text.length) {
            val c = text[i]
            if (inString) {
                if (c == '\\') i++ else if (c == '"') inString = false
            } else when (c) {
                '"' -> inString = true
                open -> depth++
                close -> if (--depth == 0) return i
            }
            i++
        }
        return -1
    }

    private fun slice(html: String, marker: String, length: Int): String? {
        val i = html.indexOf(marker)
        if (i < 0) return null
        return html.substring(i, minOf(html.length, i + length))
    }

    private fun scalar(window: String, key: String): String? {
        val m = Regex("\"" + Regex.escape(key) + "\"\\s*:\\s*(\"((?:[^\"\\\\]|\\\\.)*)\"|[^,}\\s]+)").find(window)
            ?: return null
        return m.groupValues[2].ifEmpty { m.groupValues[1] }.takeIf { it.isNotEmpty() }
    }

    private fun unescape(raw: String?): String? {
        if (raw == null) return null
        return try {
            json.parseToJsonElement("\"$raw\"").jsonPrimitive.content
        } catch (e: Exception) {
            raw
        }
    }
}
