package com.privatetube.app.core.network

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

/**
 * What went wrong. The messages are technical (English, for logs); the text a viewer sees is chosen by the UI layer
 * from the exception type (see `userMessage()`), so it follows the app language.
 */
sealed class SiteException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class NoConnection(cause: Throwable? = null) : SiteException("No internet connection", cause)
    class Timeout(cause: Throwable? = null) : SiteException("Server did not respond", cause)
    class Http(val code: Int) : SiteException("HTTP $code")
    class EmptyResponse : SiteException("Empty response")
    /** The page loaded but did not contain what the parser expects: the site's markup probably changed. */
    class ParseFailed(what: String) : SiteException("Could not parse: $what")
}

/** Fetches raw page text. An interface so repositories can be tested without a network. */
fun interface PageFetcher {
    suspend fun fetch(url: String, layout: Layout): String
}

/**
 * OkHttp-based fetcher. It has no disk cache on purpose: the player downloads the video through the same client, and
 * neither pages nor video may stay on the device. Stateless (no cookie jar): the TV and desktop layouts are chosen by
 * user-agent only, and a shared cookie would make the site mix them up.
 * Retries only transient failures (I/O errors, 5xx) a limited number of times with a growing pause;
 * 4xx answers (403, 404, 429) are reported straight away so the app does not hammer the site.
 */
class SiteClient(
    private val maxAttempts: Int = 3,
    private val retryDelayMs: Long = 600,
    clientOverride: OkHttpClient? = null,
) : PageFetcher {

    val http: OkHttpClient = clientOverride ?: OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .callTimeout(30, TimeUnit.SECONDS)
        .followRedirects(true)
        .build()

    override suspend fun fetch(url: String, layout: Layout): String = withContext(Dispatchers.IO) {
        var lastError: SiteException? = null
        repeat(maxAttempts) { attempt ->
            try {
                return@withContext executeOnce(url, layout)
            } catch (e: CancellationException) {
                throw e
            } catch (e: SiteException) {
                if (e is SiteException.Http && e.code < 500) throw e
                lastError = e
            }
            if (attempt < maxAttempts - 1) delay(retryDelayMs * (attempt + 1))
        }
        throw lastError ?: SiteException.Timeout()
    }

    private fun executeOnce(url: String, layout: Layout): String {
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", layout.userAgent)
            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            .header("Accept-Language", SiteConfig.ACCEPT_LANGUAGE)
            .build()
        try {
            http.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw SiteException.Http(response.code)
                val body = response.body?.string().orEmpty()
                if (body.isBlank()) throw SiteException.EmptyResponse()
                return body
            }
        } catch (e: SiteException) {
            throw e
        } catch (e: UnknownHostException) {
            throw SiteException.NoConnection(e)
        } catch (e: SocketTimeoutException) {
            throw SiteException.Timeout(e)
        } catch (e: IOException) {
            throw SiteException.NoConnection(e)
        }
    }
}
