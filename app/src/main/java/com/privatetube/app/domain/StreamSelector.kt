package com.privatetube.app.domain

/**
 * Picks the stream to start with. [preferredHeight] = 0 means "the best available".
 * Otherwise the best stream not taller than the preference; if every stream is taller, the lowest one.
 */
fun chooseStream(streams: List<StreamOption>, preferredHeight: Int): StreamOption? {
    if (streams.isEmpty()) return null
    val sorted = streams.sortedByDescending { it.height }
    if (preferredHeight <= 0) return sorted.first()
    return sorted.firstOrNull { it.height <= preferredHeight } ?: sorted.last()
}
