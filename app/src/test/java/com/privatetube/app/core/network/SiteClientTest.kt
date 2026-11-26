package com.privatetube.app.core.network

import com.privatetube.app.data.AppLanguage
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class SiteClientTest {
    private lateinit var server: MockWebServer
    private lateinit var client: SiteClient

    @Before fun setUp() {
        server = MockWebServer().also { it.start() }
        client = SiteClient(maxAttempts = 3, retryDelayMs = 1)
    }

    @After fun tearDown() = server.shutdown()

    private fun url() = server.url("/page").toString()

    private suspend fun errorOf(): SiteException = try {
        client.fetch(url(), Layout.TV); fail("expected an exception"); error("unreachable")
    } catch (e: SiteException) { e }

    @Test fun ok_returnsBodyAndSendsLayoutUserAgent() = runTest {
        server.enqueue(MockResponse().setBody("<html>ok</html>"))
        assertEquals("<html>ok</html>", client.fetch(url(), Layout.TV))
        assertEquals(SiteConfig.UA_TV, server.takeRequest().getHeader("User-Agent"))
        server.enqueue(MockResponse().setBody("x"))
        client.fetch(url(), Layout.DESKTOP)
        assertEquals(SiteConfig.UA_DESKTOP, server.takeRequest().getHeader("User-Agent"))
    }

    @Test fun http403_404_429_failFastWithoutRetry() = runTest {
        for (code in listOf(403, 404, 429)) {
            server.enqueue(MockResponse().setResponseCode(code))
            val e = errorOf()
            assertTrue("code $code", e is SiteException.Http && e.code == code)
        }
        assertEquals("4xx answers must not be retried", 3, server.requestCount)
    }

    @Test fun http500_isRetriedThenSucceeds() = runTest {
        server.enqueue(MockResponse().setResponseCode(500))
        server.enqueue(MockResponse().setResponseCode(503))
        server.enqueue(MockResponse().setBody("recovered"))
        assertEquals("recovered", client.fetch(url(), Layout.TV))
        assertEquals(3, server.requestCount)
    }

    @Test fun http500_everyTime_givesUpAfterLimitedAttempts() = runTest {
        repeat(5) { server.enqueue(MockResponse().setResponseCode(500)) }
        val e = errorOf()
        assertTrue(e is SiteException.Http && e.code == 500)
        assertEquals(3, server.requestCount)
    }

    @Test fun emptyBody_isReportedAsEmptyResponse() = runTest {
        repeat(3) { server.enqueue(MockResponse().setBody("   ")) }
        assertTrue(errorOf() is SiteException.EmptyResponse)
    }

    @Test fun droppedConnection_isReportedAsNoConnection() = runTest {
        repeat(3) { server.enqueue(MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START)) }
        assertTrue(errorOf() is SiteException.NoConnection)
    }

    @Test fun exceptionsCarryTechnicalMessages_viewerTextComesFromTheUiLayer() {
        assertTrue(SiteException.Http(429).message!!.contains("429"))
        assertTrue(SiteException.Http(403).message!!.contains("403"))
        assertTrue(SiteException.NoConnection().message!!.isNotBlank())
    }

    @Test fun acceptLanguageAndHostFollowTheSiteLanguage() {
        try {
            SiteConfig.language = "ru"
            assertEquals("https://rt.pornhub.com", SiteConfig.BASE_URL)
            assertTrue(SiteConfig.ACCEPT_LANGUAGE.startsWith("ru-RU"))
            assertEquals("https://rt.pornhub.com/video?o=mv", SiteConfig.absolute("/video?o=mv"))
        } finally {
            SiteConfig.language = "en"
        }
        assertEquals("https://www.pornhub.com", SiteConfig.BASE_URL)
        assertEquals("en-US,en;q=0.9", SiteConfig.ACCEPT_LANGUAGE)
    }

    @Test fun everyAppLanguageHasItsSiteEdition() {
        val hosts = mapOf(
            "en" to "www", "ru" to "rt", "uk" to "rt", "es" to "es", "pt" to "pt", "fr" to "fr", "de" to "de",
            "it" to "it", "pl" to "pl", "nl" to "nl", "cs" to "cz", "ja" to "jp", "zh" to "cn",
        )
        assertEquals(AppLanguage.entries.mapNotNull { it.code }.toSet(), hosts.keys)
        try {
            for ((code, host) in hosts) {
                SiteConfig.language = code
                assertEquals(code, "https://$host.pornhub.com", SiteConfig.BASE_URL)
            }
        } finally {
            SiteConfig.language = "en"
        }
    }
}
