package com.privatetube.app.data

import com.privatetube.app.core.network.Layout
import com.privatetube.app.core.network.PageFetcher
import com.privatetube.app.core.network.SiteConfig
import com.privatetube.app.core.network.SiteException
import com.privatetube.app.core.parsing.CategoryParser
import com.privatetube.app.core.parsing.ChannelParser
import com.privatetube.app.core.parsing.ParsingRules
import com.privatetube.app.core.parsing.StarParser
import com.privatetube.app.core.parsing.VideoListParser
import com.privatetube.app.core.parsing.VideoPageParser
import com.privatetube.app.domain.CategoryItem
import com.privatetube.app.domain.ChannelItem
import com.privatetube.app.domain.Page
import com.privatetube.app.domain.StarItem
import com.privatetube.app.domain.VideoDetails
import com.privatetube.app.domain.VideoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import java.util.concurrent.ConcurrentHashMap

/**
 * The single entry point the UI layer uses to get content. Knows which URL, layout and parser belongs to
 * each kind of data; the UI never sees HTML.
 *
 * - results are cached in memory for [ttlMs], so going back and forth does not re-download pages;
 * - identical simultaneous requests are collapsed into one;
 * - at most [maxParallel] downloads run at once, to stay polite to the site and cheap on weak devices.
 */
class ContentRepository(
    private val fetcher: PageFetcher,
    private val ttlMs: Long = 5 * 60_000L,
    maxParallel: Int = 3,
    private val now: () -> Long = System::currentTimeMillis,
) {
    private class Entry(val at: Long, val value: Any)

    private val cache = ConcurrentHashMap<String, Entry>()
    private val locks = ConcurrentHashMap<String, Mutex>()
    private val gate = Semaphore(maxParallel)

    @Suppress("UNCHECKED_CAST")
    private suspend fun <T : Any> cached(key: String, force: Boolean, load: suspend () -> T): T {
        if (!force) (cache[key]?.takeIf { now() - it.at < ttlMs })?.let { return it.value as T }
        val lock = locks.getOrPut(key) { Mutex() }
        return lock.withLock {
            // A concurrent caller may have filled the cache while we were waiting for the lock.
            if (!force) (cache[key]?.takeIf { now() - it.at < ttlMs })?.let { return it.value as T }
            val value = gate.withPermit { load() }
            cache[key] = Entry(now(), value)
            value
        }
    }

    private suspend fun download(url: String, layout: Layout): String = fetcher.fetch(url, layout)

    /** Videos of any list path: `/video?o=mv`, `/video?c=37`, `/channels/x/videos`, `/pornstar/x/videos`, search... */
    suspend fun videos(path: String, page: Int = 1, force: Boolean = false): Page<VideoItem> {
        val url = SiteConfig.absolute(SiteConfig.withPage(SiteConfig.withDefaultSort(path), page))
        val desktop = ParsingRules.DESKTOP_LIST_PATHS.any { path.startsWith(it) }
        return cached("v:$url", force) {
            val html = download(url, if (desktop) Layout.DESKTOP else Layout.TV)
            val scopes = if (desktop) ParsingRules.DESKTOP_LIST_SCOPES else ParsingRules.TV_LIST_SCOPES
            val items = withContext(Dispatchers.Default) { VideoListParser.parse(html, scopes) }
            Page(items, page, hasMore = items.isNotEmpty())
        }
    }

    /**
     * The home feed: the site's own "Recommended" list (`/recommended`, linked from the site's TV menu).
     * The site personalises that list and may serve an empty one to a visitor with no history. The feed then starts
     * with the videos of the site's home page and goes on with the pages of its "hot" list. That list is finite
     * (55 pages when checked, then HTTP 404), so after it come the pages of "most viewed", which goes much deeper.
     */
    suspend fun recommended(page: Int = 1, force: Boolean = false): Page<VideoItem> {
        if (page <= 1) {
            val rec = videos("/recommended", 1, force)
            homeFeedFallback = rec.items.isEmpty()
            hotPages = null
            if (!homeFeedFallback) return rec
            return Page(videos("/", 1, force).items, 1, hasMore = true)
        }
        if (!homeFeedFallback) return videos("/recommended", page, force)
        // Page 2 of the feed is page 1 of the "hot" list, and so on.
        val hotIndex = page - 1
        if (hotPages.let { it == null || hotIndex <= it }) {
            val items = try {
                videos(HOME_HOT, hotIndex, force).items
            } catch (e: SiteException.Http) {
                // On the very first page a 404 is a real failure; deeper it is how the site ends the list.
                if (e.code != 404 || hotIndex == 1) throw e
                emptyList()
            }
            if (items.isNotEmpty()) return Page(items, page, hasMore = true)
            hotPages = hotIndex - 1
        }
        val popular = videos(HOME_POPULAR, hotIndex - (hotPages ?: 0), force)
        return Page(popular.items, page, popular.hasMore)
    }

    /** Which source the home feed uses; decided each time its first page is loaded. */
    @Volatile
    private var homeFeedFallback = false

    /** How many pages the "hot" list turned out to have; null until its end has been reached. */
    @Volatile
    private var hotPages: Int? = null

    private companion object {
        const val HOME_HOT = "/video?o=ht"
        const val HOME_POPULAR = "/video?o=mv"
    }
    suspend fun search(query: String, page: Int = 1): Page<VideoItem> {
        val q = URLEncoder.encode(query.trim(), "UTF-8")
        return videos("/video/search?search=$q", page)
    }

    suspend fun categories(force: Boolean = false): List<CategoryItem> =
        cached("categories", force) {
            val html = download(SiteConfig.absolute("/categories"), Layout.TV)
            val items = withContext(Dispatchers.Default) { CategoryParser.parse(html) }
            if (items.isEmpty()) throw SiteException.ParseFailed("empty category list")
            items
        }

    suspend fun channels(page: Int = 1, force: Boolean = false): Page<ChannelItem> {
        val url = SiteConfig.absolute(SiteConfig.withPage(ParsingRules.CHANNEL_LIST_PATH, page))
        return cached("ch:$url", force) {
            // This page has the covers. Should the site stop serving it, the parser reads the usual page (logos only).
            val items = withContext(Dispatchers.Default) { ChannelParser.parse(download(url, Layout.SMART_TV)) }
            Page(items, page, items.isNotEmpty())
        }
    }

    suspend fun stars(page: Int = 1, force: Boolean = false): Page<StarItem> {
        val url = SiteConfig.absolute(SiteConfig.withPage("/pornstars", page))
        return cached("st:$url", force) {
            val items = withContext(Dispatchers.Default) { StarParser.parse(download(url, Layout.TV)) }
            Page(items, page, items.isNotEmpty())
        }
    }

    /**
     * Details and stream list of one video. Stream links are signed and expire (the site signs them for about a
     * day), so they are cached only for the short [ttlMs]: enough for details -> player, never for long.
     * The heavy desktop page is downloaded at most once for that hop.
     */
    suspend fun videoDetails(viewKey: String, force: Boolean = false): VideoDetails =
        cached("d:$viewKey", force) {
            val html = download(SiteConfig.videoPage(viewKey), Layout.DESKTOP)
            withContext(Dispatchers.Default) { VideoPageParser.parse(html, viewKey) }
        }

    fun clearCache() = cache.clear()
}
