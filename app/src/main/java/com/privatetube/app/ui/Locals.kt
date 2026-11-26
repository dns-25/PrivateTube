package com.privatetube.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.privatetube.app.domain.VideoItem

/** Opens the context menu of a video (long press on its card); null where there is no menu. */
val LocalVideoMenu = staticCompositionLocalOf<((VideoItem) -> Unit)?> { null }

/** The text size chosen in Settings. */
val LocalFontScale = compositionLocalOf { 1f }

/** A dialog is its own window with its own density: this applies the chosen text size inside it too. */
@Composable
fun ScaledText(content: @Composable () -> Unit) {
    val density = LocalDensity.current.density
    CompositionLocalProvider(LocalDensity provides Density(density, LocalFontScale.current), content = content)
}