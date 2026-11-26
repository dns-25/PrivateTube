package com.privatetube.app.domain

import kotlinx.serialization.Serializable

/** A single video card as shown in lists. Persisted as-is for favorites/history. */
@Serializable
data class VideoItem(
    val viewKey: String,
    val title: String,
    val thumbUrl: String? = null,
    val duration: String? = null,
)

data class CategoryItem(
    val id: String,
    val name: String,
    /** Site-relative path of the category's video list, e.g. `/video?c=37`. */
    val path: String,
    val thumbUrl: String? = null,
    val videoCount: String? = null,
)

data class ChannelItem(
    val slug: String,
    val name: String,
    /** Site-relative path of the channel's video list. */
    val path: String,
    val coverUrl: String? = null,
    val avatarUrl: String? = null,
    val subscribers: String? = null,
    val videos: String? = null,
)

data class StarItem(
    val slug: String,
    val name: String,
    /** Site-relative path of the model's video list. */
    val path: String,
    val thumbUrl: String? = null,
    val rank: String? = null,
)

data class StreamOption(
    val height: Int,
    val url: String,
) {
    val label: String get() = "${height}p"
}

data class VideoDetails(
    val viewKey: String,
    val title: String,
    val thumbUrl: String?,
    val durationSeconds: Int?,
    val views: String?,
    val channelName: String?,
    val channelPath: String?,
    val streams: List<StreamOption>,
    /** Non-null when the video cannot be played (removed, geo-blocked, no streams). The UI words it. */
    val unavailableReason: Unavailable?,
    val channelAvatarUrl: String? = null,
) {
    /** The uploader of the video (channel, model or pornstar), when the page names one. */
    val author: Author?
        get() {
            val name = channelName ?: return null
            val path = channelPath ?: return null
            return Author(name, path, channelAvatarUrl)
        }
}

/** An author the user can subscribe to. [path] (site-relative list of the author's videos) identifies them. */
@Serializable
data class Author(
    val name: String,
    val path: String,
    val avatarUrl: String? = null,
)

enum class Unavailable { Removed, NoStreams }

/** One page of a paginated list. [hasMore] is a hint; an empty next page ends the list for real. */
data class Page<T>(
    val items: List<T>,
    val page: Int,
    val hasMore: Boolean,
)
