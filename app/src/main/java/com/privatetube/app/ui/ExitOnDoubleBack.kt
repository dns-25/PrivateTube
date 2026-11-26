package com.privatetube.app.ui

import android.os.SystemClock
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Text
import com.privatetube.app.R
import kotlinx.coroutines.delay

/** How long the second BACK press is waited for. */
const val BACK_EXIT_WINDOW_MS = 2_500L

/** "Press BACK twice to leave": the first press only shows a hint, a second one within the window confirms. */
class DoubleBack {
    private var lastAt = 0L

    /** Not zero while the hint is on screen. */
    var hintAt by mutableLongStateOf(0L)
        private set

    /** True when this press is the second one. */
    fun press(): Boolean {
        val now = SystemClock.uptimeMillis()
        if (lastAt != 0L && now - lastAt <= BACK_EXIT_WINDOW_MS) return true
        lastAt = now
        hintAt = now
        return false
    }

    /** Something else happened: the next press counts as the first one again. */
    fun reset() {
        lastAt = 0L
        hintAt = 0L
    }

    fun hide() { hintAt = 0L }
}

@Composable
fun rememberDoubleBack(): DoubleBack {
    val state = remember { DoubleBack() }
    LaunchedEffect(state.hintAt) {
        if (state.hintAt != 0L) {
            delay(BACK_EXIT_WINDOW_MS)
            state.hide()
        }
    }
    return state
}

/**
 * Takes the BACK key before the focus system does: with a focused element the first press would otherwise only
 * clear the focus.
 */
fun Modifier.onBackKey(handle: () -> Unit): Modifier = onPreviewKeyEvent { event ->
    if (event.nativeKeyEvent.keyCode != android.view.KeyEvent.KEYCODE_BACK) return@onPreviewKeyEvent false
    if (event.type == KeyEventType.KeyDown && event.nativeKeyEvent.repeatCount == 0) handle()
    true
}

/** The hint is drawn by the app itself: a system Toast carries the app icon on some TV versions. */
@Composable
fun ExitHint(modifier: Modifier = Modifier) {
    Text(
        stringResource(R.string.player_back_again),
        fontSize = 22.sp, color = Color.White, textAlign = TextAlign.Center,
        modifier = modifier
            .padding(start = 24.dp, end = 24.dp, bottom = 36.dp)
            .background(Color(0xE6505256), RoundedCornerShape(24.dp))
            .padding(horizontal = 24.dp, vertical = 12.dp),
    )
}
