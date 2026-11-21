package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val KromdeckColorScheme =
  lightColorScheme(
    primary = KromdeckIndigo,
    onPrimary = Color.White,
    primaryContainer = KromdeckIndigoTint,
    onPrimaryContainer = KromdeckSlateInk,
    secondary = KromdeckCoral,
    onSecondary = Color.White,
    secondaryContainer = KromdeckCoralTint,
    onSecondaryContainer = KromdeckSlateInk,
    tertiary = KromdeckAmber,
    onTertiary = KromdeckSlateInk,
    tertiaryContainer = KromdeckAmberTint,
    onTertiaryContainer = KromdeckSlateInk,
    background = KromdeckCreamCanvas,
    onBackground = KromdeckSlateInk,
    surface = KromdeckPureSurface,
    onSurface = KromdeckSlateInk,
    surfaceVariant = KromdeckCreamCanvas,
    onSurfaceVariant = KromdeckMutedInk,
    outline = KromdeckSlateInk,
    outlineVariant = KromdeckSoftBorder,
    error = KromdeckCoral,
    onError = Color.White,
  )

@Composable
fun KromdeckTheme(content: @Composable () -> Unit) {
  MaterialTheme(
    colorScheme = KromdeckColorScheme,
    typography = Typography,
    content = content,
  )
}
