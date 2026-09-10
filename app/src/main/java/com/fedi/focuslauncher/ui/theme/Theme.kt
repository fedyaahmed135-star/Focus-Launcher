package com.fedi.focuslauncher.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme =
  darkColorScheme(
    primary = PureWhite,
    secondary = AccentWhite,
    tertiary = LightGray,
    background = PureBlack,
    surface = DarkGray,
    onPrimary = PureBlack,
    onSecondary = PureBlack,
    onTertiary = PureWhite,
    onBackground = PureWhite,
    onSurface = PureWhite,
    surfaceVariant = LightGray,
    onSurfaceVariant = PureWhite
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = DarkColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
