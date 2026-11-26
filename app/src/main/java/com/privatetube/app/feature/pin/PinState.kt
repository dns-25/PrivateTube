package com.privatetube.app.feature.pin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Whether the PIN has been entered since the app was started or last left. It lives outside the Activity, so a
 * screen rotation or a re-created Activity does not ask again, but leaving the app ([lock]) does.
 */
object PinState {
    var unlocked by mutableStateOf(false)

    fun lock() { unlocked = false }
}
