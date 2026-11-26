package com.privatetube.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme

object AppColors {
    val Background = Color(0xFF0E0F13)
    val Card = Color(0xFF1B1D24)
    val CardFocused = Color(0xFF272A33)
    val Accent = Color(0xFFFF9000)
    val OnAccent = Color(0xFF111111)
    val TextPrimary = Color(0xFFF2F2F2)
    val TextSecondary = Color(0xFFA0A4AE)
    val Error = Color(0xFFFF6B6B)
}

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = AppColors.Accent,
            onPrimary = AppColors.OnAccent,
            background = AppColors.Background,
            onBackground = AppColors.TextPrimary,
            surface = AppColors.Card,
            onSurface = AppColors.TextPrimary,
        ),
        content = content,
    )
}
