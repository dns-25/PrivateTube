package com.privatetube.app.data

import android.content.res.Configuration
import android.util.DisplayMetrics
import kotlin.math.roundToInt

/**
 * Makes the app look the same on every screen: the layout is always [DESIGN_WIDTH_DP] dp wide, whatever density the
 * TV box reports. So 1280x720, 1920x1080 and 3840x2160 show the same picture, only with more or fewer pixels.
 */
object TvScale {
    const val DESIGN_WIDTH_DP = 960

    fun densityDpi(widthPx: Int): Int =
        (widthPx * DisplayMetrics.DENSITY_DEFAULT / DESIGN_WIDTH_DP.toFloat()).roundToInt()

    fun apply(config: Configuration, metrics: DisplayMetrics) {
        val width = maxOf(metrics.widthPixels, metrics.heightPixels)
        val height = minOf(metrics.widthPixels, metrics.heightPixels)
        if (width <= 0 || height <= 0) return
        val dpi = densityDpi(width)
        val heightDp = (height * DisplayMetrics.DENSITY_DEFAULT / dpi.toFloat()).roundToInt()
        config.densityDpi = dpi
        config.screenWidthDp = DESIGN_WIDTH_DP
        config.screenHeightDp = heightDp
        config.smallestScreenWidthDp = heightDp
        // The app has its own text size setting; the system one would break the equal look.
        config.fontScale = 1f
    }
}