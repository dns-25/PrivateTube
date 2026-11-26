package com.privatetube.app.data

import android.content.Context
import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.crossfade
import com.privatetube.app.core.network.SiteConfig
import okhttp3.OkHttpClient
import okio.Path.Companion.toOkioPath
import java.io.File

/**
 * What the app keeps on disk besides settings, favorites and history:
 *
 * - [previews]: pictures of videos. They live only while the app is in use: wiped when the user leaves it and once
 *   more at the next start. Their links change all the time, so keeping them would be useless as well as revealing.
 * - [catalog]: pictures of categories, channels and models. They do not change and are kept; the size is capped, and
 *   only then the least recently used ones are dropped.
 *
 * Pages and video are never written to disk (see `SiteClient`).
 */
class ImageCaches(private val context: Context) {
    /** Shared by both loaders; thumbnails: bounded, for weak TV boxes. */
    val memory: MemoryCache by lazy { MemoryCache.Builder().maxSizePercent(context, 0.15).build() }

    val previews: ImageLoader by lazy { loader(PREVIEWS_DIR, 100L * MB) }
    val catalog: ImageLoader by lazy { loader(CATALOG_DIR, 60L * MB) }

    private fun loader(directory: String, maxBytes: Long): ImageLoader {
        // The image CDN answers 403 unless the request carries the site as Referer, exactly like a browser showing
        // the site's own pages would send.
        val http = OkHttpClient.Builder()
            .addInterceptor { chain ->
                chain.proceed(
                    chain.request().newBuilder()
                        .header("User-Agent", SiteConfig.UA_TV)
                        .header("Referer", SiteConfig.BASE_URL + "/")
                        .build(),
                )
            }
            .build()
        return ImageLoader.Builder(context)
            .components { add(OkHttpNetworkFetcherFactory(callFactory = { http })) }
            .memoryCache { memory }
            .diskCache {
                DiskCache.Builder().directory(context.cacheDir.resolve(directory).toOkioPath()).maxSizeBytes(maxBytes).build()
            }
            .crossfade(false)
            .build()
    }

    companion object {
        private const val MB = 1024L * 1024
        const val PREVIEWS_DIR = "previews"
        const val CATALOG_DIR = "catalog"

        /** Folders of earlier versions: "http" could hold pages and pieces of video, "images" mixed all pictures. */
        private val OBSOLETE_DIRS = listOf("http", "images")

        /** Run at start, before any loader exists: removes previews of the last session and old versions' caches. */
        fun deleteLeftovers(cacheDir: File) {
            (OBSOLETE_DIRS + PREVIEWS_DIR).forEach { File(cacheDir, it).deleteRecursively() }
        }
    }
}