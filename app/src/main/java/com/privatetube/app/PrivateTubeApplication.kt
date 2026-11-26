package com.privatetube.app

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import com.privatetube.app.core.network.SiteClient
import com.privatetube.app.core.network.SiteConfig
import com.privatetube.app.data.ContentRepository
import com.privatetube.app.data.ImageCaches
import com.privatetube.app.data.LanguagePrefs
import com.privatetube.app.data.UserDataStore

class PrivateTubeApplication : Application(), SingletonImageLoader.Factory {

    val siteClient: SiteClient by lazy { SiteClient() }
    private val imageCaches by lazy { ImageCaches(this) }

    /** Pictures of categories, channels and models: they do not change, so they are kept between launches. */
    val catalogImages: ImageLoader get() = imageCaches.catalog
    val repository: ContentRepository by lazy { ContentRepository(siteClient) }
    val userData: UserDataStore by lazy { UserDataStore(this) }

    override fun onCreate() {
        super.onCreate()
        instance = this
        // Also covers the cases where the app was killed or the TV lost power before it could clean up.
        ImageCaches.deleteLeftovers(cacheDir)
        // The site (and the titles it returns) follows the app language.
        SiteConfig.language = LanguagePrefs.tag(this)
    }

    /** The default loader is the one for video previews: whatever is not explicitly permanent gets wiped. */
    override fun newImageLoader(context: PlatformContext): ImageLoader = imageCaches.previews

    /** Called whenever the user leaves the app: no video previews stay on the disk. */
    fun clearVideoPreviews() {
        imageCaches.previews.diskCache?.clear()
    }

    /** The "clear cache" button of Settings: everything, the permanent pictures too. */
    fun clearAllImages() {
        imageCaches.memory.clear()
        imageCaches.previews.diskCache?.clear()
        imageCaches.catalog.diskCache?.clear()
    }
    companion object {
        /** Null only in plain JVM unit tests, where no Application exists. */
        var instance: PrivateTubeApplication? = null
            private set
    }
}
