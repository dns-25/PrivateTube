package com.privatetube.app.ui

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.ImageLoader
import coil3.SingletonImageLoader
import com.privatetube.app.PrivateTubeApplication
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.size.Scale
import coil3.size.Size
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull
import java.util.PriorityQueue
import java.util.concurrent.ConcurrentHashMap

/** Position of the item in its list; smaller numbers (higher up, further left) are loaded first. */
val LocalThumbPriority = compositionLocalOf { 0 }

/**
 * Loads thumbnails in reading order (left to right, row after row) instead of all at once, so the picture at the top
 * never waits behind one further down. A few downloads run at the same time; whenever one finishes the waiting
 * thumbnail with the smallest list position starts next. A download that has not finished within [TIMEOUT_MS] is
 * abandoned and tried again, so a stalled request never leaves a tile black for good.
 */
object Thumbs {
    private const val SLOTS = 4
    private const val TIMEOUT_MS = 3_000L
    private const val ATTEMPTS = 6
    /** Cached pictures are decoded at this size; every tile reuses them, whatever its own size. */
    private val DECODE_SIZE = Size(640, 360)

    private class Waiter(val priority: Int, val seq: Long, val cont: CancellableContinuation<Unit>)

    private val waiters = PriorityQueue<Waiter>(compareBy<Waiter>({ it.priority }, { it.seq }))
    private var free = SLOTS
    private var seq = 0L
    private val lock = Any()

    private val loaded = ConcurrentHashMap.newKeySet<String>()
    private val urlLocks = ConcurrentHashMap<String, Mutex>()

    fun isLoaded(url: String) = url in loaded

    /** Forget what was loaded (after the image cache was cleared). */
    fun reset() {
        loaded.clear()
        urlLocks.clear()
    }

    private suspend fun acquire(priority: Int) {
        synchronized(lock) {
            if (free > 0 && waiters.isEmpty()) { free--; return }
        }
        suspendCancellableCoroutine { cont ->
            val waiter = synchronized(lock) { Waiter(priority, seq++, cont).also { waiters.add(it) } }
            cont.invokeOnCancellation { synchronized(lock) { waiters.remove(waiter) } }
            // A slot may have been freed between the check above and joining the queue.
            dispatch()
        }
    }

    private fun release() {
        synchronized(lock) { free++ }
        dispatch()
    }

    private fun dispatch() {
        while (true) {
            val next = synchronized(lock) {
                if (free > 0) waiters.poll()?.also { free-- } else null
            } ?: return
            // If the waiter was cancelled just before being resumed, the slot is handed back.
            next.cont.resume(Unit) { _ -> release() }
        }
    }

    /**
     * Makes sure [url] is in the image cache; returns false only if every attempt failed.
     * [priority] is the position in the list (smaller loads first). [permanent] pictures stay between launches.
     */
    suspend fun load(context: Context, url: String, priority: Int, permanent: Boolean = false): Boolean {
        if (url in loaded) return true
        val urlLock = urlLocks.getOrPut(url) { Mutex() }
        return urlLock.withLock {
            if (url in loaded) return@withLock true
            val loader = loader(context, permanent)
            val request = ImageRequest.Builder(context).data(url).size(DECODE_SIZE).scale(Scale.FILL).build()
            for (attempt in 1..ATTEMPTS) {
                acquire(priority)
                val ok = try {
                    withTimeoutOrNull(TIMEOUT_MS) { loader.execute(request) is SuccessResult } == true
                } finally {
                    release()
                }
                if (ok) { loaded.add(url); return@withLock true }
                delay(300L * attempt)
            }
            false
        }
    }

    fun loader(context: Context, permanent: Boolean): ImageLoader =
        if (permanent) (context.applicationContext as PrivateTubeApplication).catalogImages else SingletonImageLoader.get(context)

    fun request(context: Context, url: String): ImageRequest =
        ImageRequest.Builder(context).data(url).size(DECODE_SIZE).scale(Scale.FILL).build()
}

/**
 * A thumbnail: a black box until the picture has been fetched (through [Thumbs], in list order, with retries), then
 * the picture itself, which is taken from the cache so it appears at once.
 */
@Composable
fun Thumb(
    url: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    /** A picture that does not change (category, channel, model): kept on disk between launches. */
    permanent: Boolean = false,
) {
    if (url.isNullOrEmpty()) return
    val context = LocalContext.current
    val priority = LocalThumbPriority.current
    var ready by remember(url) { mutableStateOf(Thumbs.isLoaded(url)) }
    if (!ready) {
        LaunchedEffect(url) {
            // Everything failed (offline, CDN hiccup): keep trying while the tile is on screen.
            while (!ready) {
                ready = Thumbs.load(context, url, priority, permanent)
                if (!ready) delay(5_000)
            }
        }
    }
    if (ready) {
        AsyncImage(
            model = Thumbs.request(context, url),
            imageLoader = Thumbs.loader(context, permanent),
            contentDescription = null,
            contentScale = contentScale,
            modifier = modifier,
        )
    } else {
        Box(modifier)
    }
}

@Composable
fun WithThumbPriority(index: Int, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalThumbPriority provides index, content = content)
}
