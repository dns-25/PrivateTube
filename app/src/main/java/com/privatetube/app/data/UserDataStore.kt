package com.privatetube.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.privatetube.app.domain.Author
import com.privatetube.app.domain.VideoItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_data")

/**
 * Everything the user changes locally. Note: the site's own accounts are not used, so favorites, history and
 * playback positions live only on this device and are NOT synced with anything.
 */
class UserDataStore(private val store: DataStore<Preferences>) {
    constructor(context: Context) : this(context.dataStore)

    private val json = Json { ignoreUnknownKeys = true }
    private val videoListSer = ListSerializer(VideoItem.serializer())
    private val positionsSer = MapSerializer(String.serializer(), Long.serializer())
    private val authorListSer = ListSerializer(Author.serializer())

    private object Keys {
        val AGE_CONFIRMED = booleanPreferencesKey("age_confirmed")
        val FAVORITES = stringPreferencesKey("favorites")
        val HISTORY = stringPreferencesKey("history")
        val POSITIONS = stringPreferencesKey("positions")
        val QUALITY = intPreferencesKey("preferred_quality")
        val FONT_SIZE = intPreferencesKey("font_size")
        val PIN = stringPreferencesKey("pin")
        val SUBSCRIPTIONS = stringPreferencesKey("subscriptions")
    }

    /** Stored PIN as "salt:hash" (hex), or empty when no PIN is set. The PIN itself is never stored. */
    val pinRecord: Flow<String> = store.data.map { it[Keys.PIN] ?: "" }
    val fontSize: Flow<FontSize> = store.data.map { FontSize.fromId(it[Keys.FONT_SIZE]) }

    suspend fun setFontSize(size: FontSize) = store.edit { it[Keys.FONT_SIZE] = size.id }

    suspend fun setPin(pin: String) = store.edit { it[Keys.PIN] = Pin.record(pin) }
    suspend fun clearPin() = store.edit { it.remove(Keys.PIN) }
    suspend fun checkPin(pin: String): Boolean = Pin.matches(pin, pinRecord.first())

    val ageConfirmed: Flow<Boolean> = store.data.map { it[Keys.AGE_CONFIRMED] ?: false }
    val favorites: Flow<List<VideoItem>> = store.data.map { decodeVideos(it[Keys.FAVORITES]) }
    val history: Flow<List<VideoItem>> = store.data.map { decodeVideos(it[Keys.HISTORY]) }
    /** Highest quality (in pixels of height) the player should start with; 0 = best available. */
    val preferredQuality: Flow<Int> = store.data.map { it[Keys.QUALITY] ?: 720 }

    suspend fun setAgeConfirmed(value: Boolean) = store.edit { it[Keys.AGE_CONFIRMED] = value }
    suspend fun setPreferredQuality(height: Int) = store.edit { it[Keys.QUALITY] = height }

    suspend fun toggleFavorite(video: VideoItem) = store.edit { prefs ->
        val list = decodeVideos(prefs[Keys.FAVORITES])
        val next = if (list.any { it.viewKey == video.viewKey }) list.filterNot { it.viewKey == video.viewKey }
        else listOf(video) + list
        prefs[Keys.FAVORITES] = json.encodeToString(videoListSer, next)
    }

    suspend fun isFavorite(viewKey: String): Boolean = favorites.first().any { it.viewKey == viewKey }

    /** Authors the user is subscribed to, newest first. A local list: no site account is involved. */
    val subscriptions: Flow<List<Author>> = store.data.map { decodeAuthors(it[Keys.SUBSCRIPTIONS]) }

    suspend fun subscribe(author: Author) = store.edit { prefs ->
        val list = decodeAuthors(prefs[Keys.SUBSCRIPTIONS])
        if (list.none { it.path == author.path }) {
            prefs[Keys.SUBSCRIPTIONS] = json.encodeToString(authorListSer, listOf(author) + list)
        }
    }

    suspend fun unsubscribe(path: String) = store.edit { prefs ->
        prefs[Keys.SUBSCRIPTIONS] =
            json.encodeToString(authorListSer, decodeAuthors(prefs[Keys.SUBSCRIPTIONS]).filterNot { it.path == path })
    }

    /** What a backup contains: favorites, subscriptions, history and playback positions as they are right now. */
    suspend fun backupData(): BackupData {
        val prefs = store.data.first()
        return BackupData(
            favorites = decodeVideos(prefs[Keys.FAVORITES]),
            subscriptions = decodeAuthors(prefs[Keys.SUBSCRIPTIONS]),
            history = decodeVideos(prefs[Keys.HISTORY]),
            positions = positions(prefs[Keys.POSITIONS]),
        )
    }

    /** Adds what the backup has and the device lacks. Nothing is removed or replaced. */
    suspend fun merge(backup: BackupData): MergeResult {
        var result = MergeResult(0, 0, 0)
        store.edit { prefs ->
            val favorites = decodeVideos(prefs[Keys.FAVORITES])
            val subscriptions = decodeAuthors(prefs[Keys.SUBSCRIPTIONS])
            val knownVideos = favorites.mapTo(HashSet()) { it.viewKey }
            val knownAuthors = subscriptions.mapTo(HashSet()) { it.path }
            val newVideos = backup.favorites.filter { knownVideos.add(it.viewKey) }
            val newAuthors = backup.subscriptions.filter { knownAuthors.add(it.path) }
            prefs[Keys.FAVORITES] = json.encodeToString(videoListSer, favorites + newVideos)
            prefs[Keys.SUBSCRIPTIONS] = json.encodeToString(authorListSer, subscriptions + newAuthors)
            // What was watched on this device stays on top; the history keeps its usual size limit.
            val history = decodeVideos(prefs[Keys.HISTORY])
            val knownHistory = history.mapTo(HashSet()) { it.viewKey }
            val newHistory = backup.history.filter { knownHistory.add(it.viewKey) }
                .take((HISTORY_LIMIT - history.size).coerceAtLeast(0))
            prefs[Keys.HISTORY] = json.encodeToString(videoListSer, history + newHistory)
            // A position saved on this device is newer than the one in the file.
            val local = positions(prefs[Keys.POSITIONS])
            val imported = backup.positions.filter { (key, at) -> key !in local && at >= MIN_RESUME_MS }.entries
                .take((HISTORY_LIMIT * 2 - local.size).coerceAtLeast(0))
                .associate { it.toPair() }
            prefs[Keys.POSITIONS] = json.encodeToString(positionsSer, imported + local)
            result = MergeResult(newVideos.size, newAuthors.size, newHistory.size)
        }
        return result
    }

    /** Replaces the stored copy of a video (e.g. to renew an expired thumbnail link) in favorites and history. */
    suspend fun refreshStored(video: VideoItem) = store.edit { prefs ->
        for (key in listOf(Keys.FAVORITES, Keys.HISTORY)) {
            val list = decodeVideos(prefs[key])
            if (list.any { it.viewKey == video.viewKey }) {
                prefs[key] = json.encodeToString(videoListSer, list.map { if (it.viewKey == video.viewKey) video else it })
            }
        }
    }

    suspend fun addToHistory(video: VideoItem) = store.edit { prefs ->
        val next = (listOf(video) + decodeVideos(prefs[Keys.HISTORY]).filterNot { it.viewKey == video.viewKey })
            .take(HISTORY_LIMIT)
        prefs[Keys.HISTORY] = json.encodeToString(videoListSer, next)
    }

    suspend fun removeFromHistory(viewKey: String) = store.edit { prefs ->
        prefs[Keys.HISTORY] =
            json.encodeToString(videoListSer, decodeVideos(prefs[Keys.HISTORY]).filterNot { it.viewKey == viewKey })
        val map = positions(prefs[Keys.POSITIONS]) - viewKey
        prefs[Keys.POSITIONS] = json.encodeToString(positionsSer, map)
    }

    /** Forget where playback stopped, so the video starts from the beginning. */
    suspend fun forgetPosition(viewKey: String) = savePosition(viewKey, 0, 0)

    suspend fun clearHistory() = store.edit {
        it.remove(Keys.HISTORY)
        it.remove(Keys.POSITIONS)
    }

    suspend fun position(viewKey: String): Long = positions(store.data.first()[Keys.POSITIONS])[viewKey] ?: 0L

    /** Remembers where playback stopped; positions near the very start or end are dropped (nothing to resume). */
    suspend fun savePosition(viewKey: String, positionMs: Long, durationMs: Long) = store.edit { prefs ->
        val map = positions(prefs[Keys.POSITIONS]).toMutableMap()
        val finished = durationMs > 0 && positionMs > durationMs - FINISHED_TAIL_MS
        if (positionMs < MIN_RESUME_MS || finished) map.remove(viewKey) else map[viewKey] = positionMs
        // Keep the map bounded.
        val trimmed = if (map.size > HISTORY_LIMIT * 2) map.entries.drop(map.size - HISTORY_LIMIT * 2).associate { it.toPair() } else map
        prefs[Keys.POSITIONS] = json.encodeToString(positionsSer, trimmed)
    }

    private fun decodeVideos(raw: String?): List<VideoItem> =
        if (raw.isNullOrBlank()) emptyList() else runCatching { json.decodeFromString(videoListSer, raw) }.getOrDefault(emptyList())

    private fun decodeAuthors(raw: String?): List<Author> =
        if (raw.isNullOrBlank()) emptyList() else runCatching { json.decodeFromString(authorListSer, raw) }.getOrDefault(emptyList())

    private fun positions(raw: String?): Map<String, Long> =
        if (raw.isNullOrBlank()) emptyMap() else runCatching { json.decodeFromString(positionsSer, raw) }.getOrDefault(emptyMap())

    companion object {
        const val HISTORY_LIMIT = 100
        const val MIN_RESUME_MS = 10_000L
        const val FINISHED_TAIL_MS = 15_000L
    }
}
