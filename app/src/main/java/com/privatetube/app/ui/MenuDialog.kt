package com.privatetube.app.ui

import android.view.KeyEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

private val OkKeys = setOf(KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_NUMPAD_ENTER)

/**
 * A small column of actions over the current screen; BACK closes it. A menu usually opens while OK is still held
 * down, so the rest of that press is swallowed: it must not click the first item.
 */
@Composable
fun MenuDialog(onDismiss: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    var okReleased by remember { mutableStateOf(false) }
    Dialog(onDismissRequest = onDismiss) {
        ScaledText {
            Column(
                Modifier
                    .width(460.dp)
                    .background(AppColors.Card, RoundedCornerShape(16.dp))
                    .onPreviewKeyEvent { event ->
                        if (okReleased || event.nativeKeyEvent.keyCode !in OkKeys) return@onPreviewKeyEvent false
                        if (event.type == KeyEventType.KeyDown && event.nativeKeyEvent.repeatCount == 0) {
                            okReleased = true
                            false
                        } else {
                            if (event.type == KeyEventType.KeyUp) okReleased = true
                            true
                        }
                    }
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                content = content,
            )
        }
    }
}