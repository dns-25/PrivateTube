package com.privatetube.app

import com.privatetube.app.core.network.SiteClient
import com.privatetube.app.core.network.SiteConfig
import com.privatetube.app.data.ContentRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Request
import org.junit.Assume.assumeTrue
import org.junit.Test
import java.io.File

/**
 * Manual check against the real site; skipped unless TV_LIVE=1 (it needs internet and is deliberately gentle:
 * a handful of requests). Results are written to the file named by TV_LIVE_OUT so they can be reviewed.
 * Run:  $env:TV_LIVE="1"; $env:TV_LIVE_OUT="live.txt"; ./gradlew :app:testDebugUnitTest --tests "*LiveSiteCheck*" --rerun-tasks
 */
class LiveSiteCheck {
    @Test
    fun liveSite() { runBlocking {
        assumeTrue(System.getenv("TV_LIVE") == "1")
        val out = StringBuilder()
        fun log(s: String) { out.appendLine(s); println(s) }

        val client = SiteClient()
        // TV_LIVE_LANG: the app languages to check, e.g. "en,de,ja"; each one uses its own site edition.
        val english = HashMap<String, String>()
        for (language in (System.getenv("TV_LIVE_LANG") ?: "en").split(',')) {
        SiteConfig.language = language
        log("== $language ${SiteConfig.BASE_URL}")
        val repo = ContentRepository(client)
        try {
            val home = repo.videos("/")
            log("HOME featured: ${home.items.size} videos; first duration=${home.items.firstOrNull()?.duration}; thumbs=${home.items.count { it.thumbUrl != null }}")
            val mv = repo.videos("/video?o=mv")
            log("MOST VIEWED p1: ${mv.items.size}")
            if (language == "en") mv.items.forEach { english[it.viewKey] = it.title }
            else mv.items.filter { it.viewKey in english }.let { common ->
                log("TITLES: ${common.count { it.title != english[it.viewKey] }} of ${common.size} differ from English")
            }
            val rec = repo.recommended()
            log("RECOMMENDED p1: ${rec.items.size}; hasMore=${rec.hasMore}")
            // The home feed, page after page, the way the grid asks for it. TV_LIVE_FEED sets the depth.
            val feed = rec.items.mapTo(HashSet()) { it.viewKey }
            var feedPage = 1
            var more = rec.hasMore
            while (more && feedPage < (System.getenv("TV_LIVE_FEED")?.toInt() ?: 3)) {
                Thread.sleep(1_000)
                val next = repo.recommended(++feedPage)
                val fresh = next.items.count { feed.add(it.viewKey) }
                if (fresh == 0 || feedPage % 10 == 0) log("FEED p$feedPage: ${next.items.size} videos, $fresh new")
                more = next.hasMore
            }
            log("FEED: $feedPage pages, ${feed.size} different videos, hasMore=$more")
            log("CHANNELS p2: ${repo.channels(2).items.size}; STARS p2: ${repo.stars(2).items.size}")
            val mv2 = repo.videos("/video?o=mv", 2)
            log("MOST VIEWED p2: ${mv2.items.size}; overlap with p1: ${mv2.items.map { it.viewKey }.intersect(mv.items.map { it.viewKey }.toSet()).size}")
            val cats = repo.categories()
            log("CATEGORIES: ${cats.size}; sample: ${cats.take(3).joinToString { it.name + "(" + it.videoCount + ")" }}")
            val catVideos = repo.videos(cats.first().path)
            log("CATEGORY '${cats.first().name}' videos: ${catVideos.items.size}")
            val channels = repo.channels()
            log("CHANNELS p1: ${channels.items.size}; covers=${channels.items.count { it.coverUrl != null }}; p2 new=${repo.channels(2).items.count { c -> channels.items.none { it.slug == c.slug } }}; first has stats: ${channels.items.firstOrNull()?.subscribers} / ${channels.items.firstOrNull()?.videos}")
            val chVideos = repo.videos(channels.items.first().path)
            log("CHANNEL '${channels.items.first().name}' videos: ${chVideos.items.size}")
            val stars = repo.stars()
            log("STARS p1: ${stars.items.size}")
            val starVideos = repo.videos(stars.items.first().path)
            log("STAR '${stars.items.first().name}' videos (desktop layout): ${starVideos.items.size}")
            val search = repo.search("привет")
            log("SEARCH 'привет' (Russian): ${search.items.size}")
            val search2 = repo.search("test")
            log("SEARCH 'test': ${search2.items.size}")

            val target = mv.items.first()
            val d = repo.videoDetails(target.viewKey)
            log("DETAILS: title.len=${d.title.length} duration=${d.durationSeconds}s views=${d.views} channel=${d.channelName} streams=${d.streams.map { it.label }} unavailable=${d.unavailableReason}")
            val list = mv.items.first()
            log("COMPARE list vs page: list.duration=${list.duration} page.duration=${d.durationSeconds?.let { com.privatetube.app.feature.formatDuration(it) }}; titles equal=${list.title.trim() == d.title.trim()}")

            // Does the CDN serve the HLS stream to a plain client? (what the player will do)
            val stream = d.streams.first { it.height <= 720 }
            fun get(url: String, range: String? = null): Pair<Int, String> {
                val req = Request.Builder().url(url).header("User-Agent", SiteConfig.UA_DESKTOP)
                    .header("Referer", SiteConfig.BASE_URL + "/").apply { if (range != null) header("Range", range) }.build()
                client.http.newCall(req).execute().use { r -> return r.code to (if (range == null) r.body?.string().orEmpty() else "") }
            }
            val (masterCode, master) = get(stream.url)
            log("HLS master (${stream.label}): HTTP $masterCode; EXTM3U=${master.startsWith("#EXTM3U")}; variants=${master.lines().count { it.startsWith("#EXT-X-STREAM-INF") }}")
            val variantUrl = master.lines().firstOrNull { it.isNotBlank() && !it.startsWith("#") }
            if (variantUrl != null) {
                val abs = java.net.URI(stream.url).resolve(variantUrl).toString()
                val (vCode, variant) = get(abs)
                val segs = variant.lines().filter { it.isNotBlank() && !it.startsWith("#") }
                log("HLS media playlist: HTTP $vCode; segments=${segs.size}; endlist=${variant.contains("#EXT-X-ENDLIST")}")
                val segUrl = java.net.URI(abs).resolve(segs.first()).toString()
                val (sCode, _) = get(segUrl, "bytes=0-1023")
                log("HLS first segment (range 1KB): HTTP $sCode")
            }
            // Authors (needed for subscriptions): every kind of uploader must be recognised and have a video list.
            val kinds = HashMap<String, com.privatetube.app.domain.Author>()
            var asked = 0
            for (item in mv.items) {
                if (asked == 12 || kinds.size == 3) break
                asked++
                val a = repo.videoDetails(item.viewKey).author ?: continue
                kinds.putIfAbsent(a.path.split('/')[1], a)
            }
            log("AUTHOR kinds found in $asked videos: ${kinds.keys}")
            for ((kind, a) in kinds) {
                val p1 = repo.videos(a.path)
                val p2 = runCatching { repo.videos(a.path, 2).items.map { it.viewKey } }
                log("AUTHOR [$kind] avatar=${a.avatarUrl != null}; list p1=${p1.items.size} thumbs=${p1.items.count { it.thumbUrl != null }}; p2=${p2.map { l -> "${l.size}, new=${(l - p1.items.map { it.viewKey }.toSet()).size}" }.getOrElse { it.javaClass.simpleName }}")
            }
            log("RESULT: OK")
        } catch (e: Throwable) {
            log("RESULT: FAILED ${e.javaClass.simpleName}: ${e.message}")
        }
        System.getenv("TV_LIVE_OUT")?.let { File(it).writeText(out.toString()) }
        Thread.sleep(3_000)
        }
        SiteConfig.language = "en"
    } }
}
