package com.privatetube.app.feature

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.privatetube.app.R
import com.privatetube.app.PrivateTubeApplication
import com.privatetube.app.core.network.SiteException
import com.privatetube.app.data.LanguagePrefs
import com.privatetube.app.domain.Unavailable

/** Gives composables access to the app-level singletons (repository, user data). */
@Composable
fun rememberApp(): PrivateTubeApplication = LocalContext.current.applicationContext as PrivateTubeApplication

fun formatDuration(totalSeconds: Int): String {
    val h = totalSeconds / 3600
    val m = (totalSeconds % 3600) / 60
    val s = totalSeconds % 60
    return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%d:%02d".format(m, s)
}

fun formatMillis(ms: Long): String = formatDuration((ms / 1000).toInt().coerceAtLeast(0))

/** The application's own context follows the system language; this one follows the language of the app. */
private fun localized(): Context? = PrivateTubeApplication.instance?.let(LanguagePrefs::wrap)

/** The text a viewer sees for a failure, in the app language (the exceptions themselves carry technical text). */
fun Throwable.userMessage(): String {
    val app = localized() ?: return message ?: javaClass.simpleName
    return when (this) {
        is SiteException.NoConnection -> app.getString(R.string.err_no_connection)
        is SiteException.Timeout -> app.getString(R.string.err_timeout)
        is SiteException.Http -> when (code) {
            403 -> app.getString(R.string.err_http_403)
            404 -> app.getString(R.string.err_http_404)
            429 -> app.getString(R.string.err_http_429)
            in 500..599 -> app.getString(R.string.err_http_5xx, code)
            else -> app.getString(R.string.err_http_other, code)
        }
        is SiteException.EmptyResponse -> app.getString(R.string.err_empty)
        is SiteException.ParseFailed -> app.getString(R.string.err_parse)
        else -> app.getString(R.string.err_unexpected, javaClass.simpleName)
    }
}

fun Unavailable.message(): String = localized()?.getString(
    when (this) {
        Unavailable.Removed -> R.string.unavailable_removed
        Unavailable.NoStreams -> R.string.unavailable_nostreams
    },
) ?: name
