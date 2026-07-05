package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.graphics.Color

private val CinematicDarkColorScheme = darkColorScheme(
  primary = CinematicAccent,
  onPrimary = CinematicAccentOn,
  primaryContainer = CinematicSurfaceVariant,
  onPrimaryContainer = CinematicAccent,
  secondary = CinematicAccent,
  onSecondary = CinematicAccentOn,
  secondaryContainer = CinematicSurfaceVariant,
  onSecondaryContainer = CinematicTextPrimary,
  background = CinematicBackground,
  onBackground = CinematicTextPrimary,
  surface = CinematicSurface,
  onSurface = CinematicTextPrimary,
  surfaceVariant = CinematicSurfaceVariant,
  onSurfaceVariant = CinematicTextSecondary,
  outline = CinematicBorder,
  outlineVariant = CinematicBorder
)

private val CinematicLightColorScheme = lightColorScheme(
  primary = CinematicAccent,
  onPrimary = Color.Black,
  primaryContainer = Color(0xFFFFF4CC),
  onPrimaryContainer = Color(0xFF8A6D00),
  secondary = CinematicAccent,
  onSecondary = Color.Black,
  secondaryContainer = Color(0xFFF1F3F5),
  onSecondaryContainer = Color(0xFF1C1E21),
  background = Color(0xFFF8F9FA),
  onBackground = Color(0xFF1C1E21),
  surface = Color(0xFFFFFFFF),
  onSurface = Color(0xFF1C1E21),
  surfaceVariant = Color(0xFFF1F3F5),
  onSurfaceVariant = Color(0xFF5F6368),
  outline = Color(0xFFDEE2E6),
  outlineVariant = Color(0xFFDEE2E6)
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Control the theme dynamically
  dynamicColor: Boolean = false, // Disable dynamic color to maintain design identity
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) CinematicDarkColorScheme else CinematicLightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
