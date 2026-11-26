package com.privatetube.app.core.parsing

import com.privatetube.app.Fixtures
import com.privatetube.app.core.network.SiteException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class VideoPageParserTest {

    @Test
    fun parsesStreamsSortedBestFirstWithoutDuplicatesOrNonHls() {
        val d = VideoPageParser.parse(Fixtures.read("video_page.html"), "abc")
        assertEquals(listOf(1080, 720, 240), d.streams.map { it.height })
        assertTrue(d.streams.all { it.url.startsWith("https://cdn.example.test/") })
        // JSON escapes (\/) are removed and the query string survives
        assertEquals("https://cdn.example.test/hls/1080P_4000K_1.mp4/master.m3u8?h=a%2Fb&e=1795403228&f=1", d.streams[0].url)
        assertNull(d.unavailableReason)
    }

    @Test
    fun parsesMetadataEvenWithBracketsAndQuotesInsideStrings() {
        val d = VideoPageParser.parse(Fixtures.read("video_page.html"), "abc")
        assertEquals("Sample \"quoted\" title – test", d.title)
        assertEquals(902, d.durationSeconds)
        assertEquals("https://example.test/poster.jpg", d.thumbUrl)
        assertEquals("2.3M", d.views)
        assertEquals("Sample Channel", d.channelName)
        assertEquals("/channels/sample-channel/videos", d.channelPath)
    }

    @Test
    fun uploaderMayBeAModelAndTheTextlessAvatarLinkIsNotTakenForTheName() {
        val d = VideoPageParser.parse(Fixtures.read("video_page_model.html"), "abc")
        assertEquals("Sample Model", d.author?.name)
        assertEquals("/model/sample-model/videos", d.author?.path)
        assertEquals("https://example.test/avatar200x200.jpg", d.author?.avatarUrl)
    }

    @Test
    fun unavailableVideoIsReportedNotCrashed() {
        assertNull(VideoPageParser.parse(Fixtures.read("video_unavailable.html"), "x").author)
        val d = VideoPageParser.parse(Fixtures.read("video_unavailable.html"), "x")
        assertNotNull(d.unavailableReason)
        assertTrue(d.streams.isEmpty())
        assertEquals("Gone video", d.title)
    }

    @Test
    fun pageWithoutPlayerDataFailsWithParseError() {
        try {
            VideoPageParser.parse("<html><body>Totally different page</body></html>", "x")
            fail("expected ParseFailed")
        } catch (e: SiteException.ParseFailed) {
            // expected: lets the UI show "site format changed" instead of an empty screen
        }
    }

    @Test
    fun balancedEnd_ignoresBracketsInsideStrings() {
        val text = """[{"a":"]]"},{"b":["x"]}] tail"""
        val end = VideoPageParser.findBalancedEnd(text, 0, '[', ']')
        assertEquals("""[{"a":"]]"},{"b":["x"]}]""", text.substring(0, end + 1))
        assertEquals(-1, VideoPageParser.findBalancedEnd("[1,2", 0, '[', ']'))
    }
}
