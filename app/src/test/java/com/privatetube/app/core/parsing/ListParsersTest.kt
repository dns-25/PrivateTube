package com.privatetube.app.core.parsing

import com.privatetube.app.Fixtures
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ListParsersTest {

    @Test
    fun tvVideoList_extractsCardsAndIgnoresPromoLinksOutsideTheList() {
        val items = VideoListParser.parse(Fixtures.read("tv_video_list.html"))
        // 3 real cards; the promo link is outside ul.sectionContent, the duplicate card is merged, "dup" has no thumb/duration.
        assertTrue(items.none { it.viewKey == "promo000000001" })
        assertEquals(items.map { it.viewKey }.distinct(), items.map { it.viewKey })
        val first = items.first { it.viewKey == "samplekey00001" }
        assertEquals("Sample title 1", first.title)
        assertEquals("15:02", first.duration)
        assertNotNull(first.thumbUrl)
        assertTrue(first.thumbUrl!!.startsWith("https://"))
    }

    @Test
    fun tvVideoList_keepsOrderOfTheSite() {
        val keys = VideoListParser.parse(Fixtures.read("tv_video_list.html")).map { it.viewKey }
        assertEquals("samplekey00001", keys.first())
        assertEquals(3, keys.take(3).size)
    }

    @Test
    fun desktopList_isRestrictedToTheMainSectionAndReadsDesktopMarkup() {
        val items = VideoListParser.parse(Fixtures.read("desktop_star_videos.html"), ParsingRules.DESKTOP_LIST_SCOPES)
        assertEquals(listOf("desktopkey0001", "desktopkey0002"), items.map { it.viewKey })
        assertEquals("Desktop sample 1", items[0].title)
        assertEquals("7:56", items[0].duration)
        assertEquals("1:02:03", items[1].duration)
        // HTML entities are decoded
        assertEquals("Desktop sample 2 & more", items[1].title)
        // thumbnail comes from data-mediumthumb when present
        assertEquals("https://example.test/thumb1-medium.jpg", items[0].thumbUrl)
    }

    @Test
    fun videoList_emptyOrUnrelatedPagesGiveEmptyList() {
        assertTrue(VideoListParser.parse("").isEmpty())
        assertTrue(VideoListParser.parse("<html><body><p>nothing here</p></body></html>").isEmpty())
    }

    @Test
    fun videoList_cardWithoutImageStillParses() {
        val html = """<ul class="sectionContent"><li><a href="/view_video.php?viewkey=noimg1"><span class="videoTitle">No image</span></a></li></ul>"""
        val item = VideoListParser.parse(html).single()
        assertEquals("No image", item.title)
        assertNull(item.thumbUrl)
        assertNull(item.duration)
    }

    @Test
    fun categories_parsedWithIdNameAndCount_navigationEntriesSkipped() {
        val cats = CategoryParser.parse(Fixtures.read("categories.html"))
        assertEquals(3, cats.size)
        assertTrue(cats.all { it.path.startsWith("/video?c=") })
        assertTrue(cats.all { it.name.isNotBlank() })
        assertEquals("37", cats.first { it.path == "/video?c=37" }.id)
        assertNotNull(cats.first().videoCount)
    }

    @Test
    fun channels_parsedWithStatsAndSlug() {
        val channels = ChannelParser.parse(Fixtures.read("channels.html"))
        assertEquals(2, channels.size)
        assertEquals("channel-1", channels[0].slug)
        assertEquals("/channels/channel-1/videos", channels[0].path)
        assertEquals("Channel 1", channels[0].name)
        assertTrue(channels[0].subscribers!!.endsWith("Subscribers"))
        assertTrue(channels[0].videos!!.endsWith(" Videos"))
        assertFalse(channels.any { it.slug.isEmpty() })
    }

    @Test
    fun siteLayout_videoCardsTakeTheTitleItselfAndSkipTheSideSlider() {
        val items = VideoListParser.parse(Fixtures.read("site_video_list.html"))
        assertEquals(listOf("sitekey0000001", "sitekey0000002"), items.map { it.viewKey })
        // Neither the uploader nor the views, which share a wrapper with the title.
        assertEquals("Site sample 1 & more", items[0].title)
        assertEquals("17:03", items[0].duration)
        assertEquals("1:02:03", items[1].duration)
        assertEquals("https://example.test/site1.jpg", items[0].thumbUrl)
    }

    @Test
    fun siteLayout_categoriesComeFromTheFullList() {
        val cats = CategoryParser.parse(Fixtures.read("site_categories.html"))
        assertEquals(listOf("1", "2", "3"), cats.map { it.id })
        assertEquals(listOf("/video?c=1", "/video?c=2", "/categories/sample"), cats.map { it.path })
        assertEquals("Category 1", cats[0].name)
        assertEquals("127,923", cats[0].videoCount)
        assertEquals("https://example.test/cat1.jpg", cats[0].thumbUrl)
    }

    @Test
    fun siteLayout_channels() {
        val channels = ChannelParser.parse(Fixtures.read("site_channels.html"))
        assertEquals(listOf("first-sample", "second-sample"), channels.map { it.slug })
        assertEquals("/channels/first-sample/videos", channels[0].path)
        assertEquals("First Sample", channels[0].name)
        assertEquals("Subscribers 8.5M", channels[0].subscribers)
        assertEquals("Videos 4.9K", channels[0].videos)
        assertEquals("https://example.test/ch1.jpg", channels[0].avatarUrl)
        assertNull(channels[1].avatarUrl)
        assertNull(channels[1].subscribers)
    }

    @Test
    fun siteLayout_pornstarsAndModelsKeepTheirOwnAddresses() {
        val stars = StarParser.parse(Fixtures.read("site_stars.html"))
        assertEquals(listOf("/pornstar/first-sample/videos", "/model/second-sample/videos"), stars.map { it.path })
        assertEquals("First Sample", stars[0].name)
        assertEquals("22", stars[0].rank)
        assertNull(stars[1].rank)
        assertEquals("https://example.test/s2.jpg", stars[1].thumbUrl)
    }

    @Test
    fun stars_parsed_andPointToVideoListPath() {
        val stars = StarParser.parse(Fixtures.read("stars.html"))
        assertEquals(2, stars.size)
        assertEquals("star-1", stars[0].slug)
        assertEquals("/pornstar/star-1/videos", stars[0].path)
        assertEquals("Star 1", stars[0].name)
        assertNotNull(stars[0].thumbUrl)
    }
}
