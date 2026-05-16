package com.gramakhata.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Core palette mapped from the web app's oklch tokens
val TealPrimary      = Color(0xFF1A8C7A)  // oklch(0.55 0.14 175) → deep teal
val TealLight        = Color(0xFF3DB89E)  // oklch(0.72 0.16 165) → lighter teal-emerald
val CoralDanger      = Color(0xFFD9503A)  // oklch(0.62 0.21 25)  → coral red
val AmberWarning     = Color(0xFFD4962A)  // oklch(0.78 0.16 75)  → amber
val EmeraldSuccess   = Color(0xFF3DA876)  // oklch(0.62 0.16 155) → emerald
val CreamBackground  = Color(0xFFFAF8F2)  // oklch(0.985 0.012 90) → warm cream
val CardWhite        = Color(0xFFFFFFFF)
val DarkForeground   = Color(0xFF1E2A30)  // oklch(0.22 0.03 220)
val MutedForeground  = Color(0xFF7A8A90)
val SoftBorder       = Color(0xFFEAE6DC)
val PrimaryContainer = Color(0xFFD4F0EA)  // primary-soft

private val LightColorScheme = lightColorScheme(
    primary          = TealPrimary,
    onPrimary        = Color.White,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = TealPrimary,
    secondary        = Color(0xFFF5F0E5),
    onSecondary      = DarkForeground,
    error            = CoralDanger,
    onError          = Color.White,
    background       = CreamBackground,
    onBackground     = DarkForeground,
    surface          = CardWhite,
    onSurface        = DarkForeground,
    surfaceVariant   = Color(0xFFF2EFE5),
    onSurfaceVariant = MutedForeground,
    outline          = SoftBorder,
)

@Composable
fun GramaKhataTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography  = GramaKhataTypography,
        content     = content
    )
}
