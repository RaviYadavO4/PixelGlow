package com.portfolio.pixelglow.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val PremiumDarkColorScheme = darkColorScheme(
    primary = NeonCyanGlow,
    background = DarkMatteBg,
    surface = SurfaceBlack,
    onBackground = TextWhite,
    onSurface = TextWhite
)

@Composable
fun PixelGlowTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PremiumDarkColorScheme,
        content = content
    )
}
