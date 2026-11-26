package com.privatetube.app.feature.pin

import android.view.KeyEvent as AndroidKeyEvent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Text
import com.privatetube.app.R
import com.privatetube.app.data.Pin
import com.privatetube.app.ui.AppColors
import com.privatetube.app.ui.ExitHint
import com.privatetube.app.ui.onBackKey
import com.privatetube.app.ui.rememberDoubleBack
import com.privatetube.app.ui.FocusCard
import com.privatetube.app.ui.requestFocusWhenReady

/**
 * A PIN entry screen for the remote: an on-screen keypad (arrow keys + OK) that also accepts the digit keys of
 * a remote or keyboard. After [Pin.LENGTH] digits [onComplete] is called and the entry starts over, so the caller
 * can show a [message] (e.g. "wrong PIN") and ask again.
 */
@Composable
fun PinPad(
    title: String,
    message: String?,
    onComplete: (String) -> Unit,
    onCancel: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    var digits by remember { mutableStateOf("") }
    val start = remember { FocusRequester() }
    LaunchedEffect(Unit) { start.requestFocusWhenReady() }

    fun press(d: Char) {
        if (digits.length >= Pin.LENGTH) return
        val next = digits + d
        if (next.length == Pin.LENGTH) {
            digits = ""
            onComplete(next)
        } else {
            digits = next
        }
    }
    fun backspace() { digits = digits.dropLast(1) }

    if (onCancel != null) BackHandler(onBack = onCancel)

    Box(
        modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .onPreviewKeyEvent { event ->
                val code = event.nativeKeyEvent.keyCode
                val digit = when (code) {
                    in AndroidKeyEvent.KEYCODE_0..AndroidKeyEvent.KEYCODE_9 -> '0' + (code - AndroidKeyEvent.KEYCODE_0)
                    in AndroidKeyEvent.KEYCODE_NUMPAD_0..AndroidKeyEvent.KEYCODE_NUMPAD_9 -> '0' + (code - AndroidKeyEvent.KEYCODE_NUMPAD_0)
                    else -> null
                }
                when {
                    digit != null -> { if (event.type == KeyEventType.KeyDown) press(digit); true }
                    code == AndroidKeyEvent.KEYCODE_DEL -> { if (event.type == KeyEventType.KeyDown) backspace(); true }
                    else -> false
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(title, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = AppColors.TextPrimary, textAlign = TextAlign.Center)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.height(28.dp)) {
                repeat(Pin.LENGTH) { i ->
                    Box(
                        Modifier
                            .size(22.dp)
                            .background(if (i < digits.length) AppColors.Accent else AppColors.CardFocused, CircleShape),
                    )
                }
            }
            Text(
                message ?: " ",
                fontSize = 20.sp,
                color = AppColors.Error,
                textAlign = TextAlign.Center,
                modifier = Modifier.height(28.dp),
            )
            val rows = listOf("123", "456", "789")
            rows.forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEach { d ->
                        Key(d.toString(), { press(d) }, if (d == '5') Modifier.focusRequester(start) else Modifier)
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Key("⌫", ::backspace)
                Key("0", { press('0') })
                if (onCancel != null) Key("✕", onCancel) else Box(Modifier.width(KeyWidth))
            }
        }
    }
}

private val KeyWidth = 92.dp

@Composable
private fun Key(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    FocusCard(onClick = onClick, modifier = modifier.width(KeyWidth).height(64.dp)) { focused ->
        Text(
            label,
            fontSize = 28.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (focused) AppColors.Accent else AppColors.TextPrimary,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

/** Shown at start (and after the app was left) when a PIN is set. BACK leaves the app. */
@Composable
fun PinLockScreen(verify: suspend (String) -> Boolean, onUnlocked: () -> Unit, onExit: () -> Unit) {
    var wrong by remember { mutableStateOf(false) }
    var attempt by remember { mutableStateOf<String?>(null) }
    // The ✕ button leaves at once; the BACK key asks to be pressed twice, like everywhere in the app.
    val back = rememberDoubleBack()
    fun handleBack() { if (back.press()) onExit() }
    LaunchedEffect(attempt) {
        val pin = attempt ?: return@LaunchedEffect
        if (verify(pin)) onUnlocked() else wrong = true
        attempt = null
    }
    Box(Modifier.fillMaxSize().onBackKey(::handleBack), contentAlignment = Alignment.BottomCenter) {
        PinPad(
            title = stringResource(R.string.pin_enter),
            message = if (wrong) stringResource(R.string.pin_wrong) else null,
            onComplete = { attempt = it },
            onCancel = onExit,
        )
        // After the pad, so that this handler wins over the one of the pad.
        BackHandler { handleBack() }
        if (back.hintAt != 0L) ExitHint()
    }
}
