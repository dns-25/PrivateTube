package com.privatetube.app.data

import com.privatetube.app.Fixtures
import com.privatetube.app.core.network.Layout
import com.privatetube.app.core.network.PageFetcher
import com.privatetube.app.core.network.SiteException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

private class FakeFetcher(private val pages: Map<String, String> = emptyMap(), private val default: String = "") : PageFetcher {
    val calls = mutableListOf<Pair<String, Layout>>()
    var failWith: SiteException? = null
    override suspend fun fetch(url: String, layout: Layout): String {
        calls += url to layout
        delay(100) // simulate network time so that concurrent callers really overlap
        failWith?.let { throw it }
        return pages[url] ?: default
    }
}

class ContentRepositoryTest {
    private val tvList = Fixtures.read("tv_video_list.html")

    @Test fun secondCallIsServedFromCache() = runTest {
        val fetcher = FakeFetcher(default = tvList)
        val repo = ContentRepository(fetcher)
        val a = repo.videos("/video?o=mv")
        val b = repo.videos("/video?o=mv")
        assertEquals(a, b)
        assertEquals(1, fetcher.calls.size)
    }

    @Test fun simultaneousIdenticalRequestsDownloadOnce() = runTest {
        val fetcher = FakeFetcher(default = tvList)
        val repo = ContentRepository(fetcher)
        val results = List(5) { async { repo.videos("/video?o=mv") } }.awaitAll()
        assertEquals(1, fetcher.calls.size)
        assertTrue(results.all { it == results.first() })
    }

    @Test fun forceRefreshAndTtlExpiryRefetch() = runTest {
        var now = 0L
        val fetcher = FakeFetcher(default = tvList)
        val repo = ContentRepository(fetcher, ttlMs = 1_000, now = { now })
        repo.videos("/x")
        repo.videos("/x", force = true)
        assertEquals(2, fetcher.calls.size)
        now += 2_000
        repo.videos("/x")
        assertEquals(3, fetcher.calls.size)
    }

    @Test fun pagesAreRequestedWithPageParameter() = runTest {
        val fetcher = FakeFetcher(default = tvList)
        val repo = ContentRepository(fetcher)
        repo.videos("/video?o=mv", page = 1)
        repo.videos("/video?o=mv", page = 2)
        repo.videos("/channels/x/videos", page = 3)
        assertEquals(
            listOf(
                "https://www.pornhub.com/video?o=mv",
                "https://www.pornhub.com/video?o=mv&page=2",
                "https://www.pornhub.com/channels/x/videos?page=3",
            ),
            fetcher.calls.map { it.first },
        )
    }

    @Test fun theChannelListIsAskedForAsASmartTv_becauseOnlyThatPageHasCovers() = runTest {
        val fetcher = FakeFetcher(default = Fixtures.read("channels.html"))
        val page = ContentRepository(fetcher).channels(2)
        assertEquals("https://www.pornhub.com/channels?o=rk&page=2" to Layout.SMART_TV, fetcher.calls.single())
        assertTrue(page.items.isNotEmpty() && page.items.all { it.coverUrl != null })
    }

    @Test fun categoryListsAlwaysCarryASortOrder_becauseThePlainPage2IsRefusedBySite() = runTest {
        val fetcher = FakeFetcher(default = tvList)
        val repo = ContentRepository(fetcher)
        repo.videos("/video?c=37", page = 1)
        repo.videos("/video?c=37", page = 2)
        repo.videos("/video?c=5&o=cm", page = 2)   // an explicit sort is left alone
        assertEquals(
            listOf(
                "https://www.pornhub.com/video?c=37&o=mv",
                "https://www.pornhub.com/video?c=37&o=mv&page=2",
                "https://www.pornhub.com/video?c=5&o=cm&page=2",
            ),
            fetcher.calls.map { it.first },
        )
    }

    @Test fun modelPagesUseDesktopLayoutAndTheirOwnScope_othersUseTv() = runTest {
        val fetcher = FakeFetcher(default = Fixtures.read("desktop_star_videos.html"))
        val repo = ContentRepository(fetcher)
        val page = repo.videos("/pornstar/star-1/videos")
        assertEquals(Layout.DESKTOP, fetcher.calls.single().second)
        assertEquals(listOf("desktopkey0001", "desktopkey0002"), page.items.map { it.viewKey })
        repo.videos("/video?o=mv")
        assertEquals(Layout.TV, fetcher.calls.last().second)
    }

    @Test fun searchEncodesRussianQueryAsUtf8() = runTest {
        val fetcher = FakeFetcher(default = tvList)
        ContentRepository(fetcher).search("привет мир")
        val url = fetcher.calls.single().first
        assertEquals("https://www.pornhub.com/video/search?search=%D0%BF%D1%80%D0%B8%D0%B2%D0%B5%D1%82+%D0%BC%D0%B8%D1%80", url)
    }

    @Test fun emptyListPageEndsPaging() = runTest {
        val repo = ContentRepository(FakeFetcher(default = "<html><body>no videos</body></html>"))
        val page = repo.videos("/video?c=1", page = 99)
        assertTrue(page.items.isEmpty())
        assertEquals(false, page.hasMore)
    }

    @Test fun emptyCategoriesPageIsAFormatError() = runTest {
        val repo = ContentRepository(FakeFetcher(default = "<html><body>changed markup</body></html>"))
        try { repo.categories(); fail() } catch (e: SiteException.ParseFailed) { /* expected */ }
    }

    @Test fun failuresAreNotCached() = runTest {
        val fetcher = FakeFetcher(default = tvList).also { it.failWith = SiteException.Http(500) }
        val repo = ContentRepository(fetcher)
        try { repo.videos("/x"); fail() } catch (e: SiteException.Http) { /* expected */ }
        fetcher.failWith = null
        assertTrue(repo.videos("/x").items.isNotEmpty())
        assertEquals(2, fetcher.calls.size)
    }

    @Test fun videoDetailsParsedAndCached() = runTest {
        val fetcher = FakeFetcher(default = Fixtures.read("video_page.html"))
        val repo = ContentRepository(fetcher)
        val d = repo.videoDetails("abc")
        assertEquals(listOf(1080, 720, 240), d.streams.map { it.height })
        assertEquals("https://www.pornhub.com/view_video.php?viewkey=abc", fetcher.calls.single().first)
        assertEquals(Layout.DESKTOP, fetcher.calls.single().second)
        repo.videoDetails("abc")
        assertEquals(1, fetcher.calls.size)
    }

    @Test fun recommendedFeedUsesTheRecommendedPage() = runTest {
        val fetcher = FakeFetcher(pages = mapOf("https://www.pornhub.com/recommended" to tvList))
        val repo = ContentRepository(fetcher)
        val page = repo.recommended()
        assertTrue(page.items.isNotEmpty())
        assertEquals(listOf("https://www.pornhub.com/recommended"), fetcher.calls.map { it.first })
    }

    @Test fun emptyRecommendations_theFeedStartsWithTheHomePageAndGoesOnWithTheHotList() = runTest {
        val hot = "https://www.pornhub.com/video?o=ht"
        val fetcher = FakeFetcher(
            pages = mapOf("https://www.pornhub.com/" to tvList, hot to tvList, "$hot&page=2" to tvList),
            default = "<html><body></body></html>",
        )
        val repo = ContentRepository(fetcher)
        val first = repo.recommended()
        assertTrue(first.items.isNotEmpty())
        assertTrue(first.hasMore)
        // Page 2 of the feed is page 1 of the hot list, page 3 is its page 2.
        val second = repo.recommended(page = 2)
        assertEquals(2, second.page)
        assertTrue(second.items.isNotEmpty() && second.hasMore)
        assertTrue(repo.recommended(page = 3).items.isNotEmpty())
        assertEquals(listOf(hot, "$hot&page=2"), fetcher.calls.map { it.first }.takeLast(2))
    }

    @Test fun whenTheHotListRunsOutTheFeedGoesOnWithMostViewed() = runTest {
        val hot = "https://www.pornhub.com/video?o=ht"
        val popular = "https://www.pornhub.com/video?o=mv"
        val pages = mapOf(
            "https://www.pornhub.com/" to tvList, hot to tvList, popular to tvList, "$popular&page=2" to tvList,
        )
        val base = FakeFetcher(pages = pages, default = "<html><body></body></html>")
        // As on the site: past its last page the hot list answers 404.
        val repo = ContentRepository(PageFetcher { url, layout ->
            if (url.startsWith("$hot&page=")) throw SiteException.Http(404)
            base.fetch(url, layout)
        })
        repo.recommended()
        repo.recommended(page = 2)
        val third = repo.recommended(page = 3)
        assertTrue(third.items.isNotEmpty() && third.hasMore)
        assertTrue(repo.recommended(page = 4).items.isNotEmpty())
        assertEquals(listOf(popular, "$popular&page=2"), base.calls.map { it.first }.takeLast(2))
        // An empty page of the last source ends the feed.
        assertEquals(false, repo.recommended(page = 5).hasMore)
    }
    @Test fun workingRecommendationsKeepTheirOwnPages() = runTest {
        val rec = "https://www.pornhub.com/recommended"
        val fetcher = FakeFetcher(pages = mapOf(rec to tvList, "$rec?page=2" to tvList))
        val repo = ContentRepository(fetcher)
        repo.recommended()
        repo.recommended(page = 2)
        assertEquals(listOf(rec, "$rec?page=2"), fetcher.calls.map { it.first })
    }
}
