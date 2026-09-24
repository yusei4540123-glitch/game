package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val BloxDarkColorScheme = darkColorScheme(
    primary = BloxRedRoblox,
    onPrimary = BloxTextPrimary,
    primaryContainer = BloxSurfaceElevated,
    onPrimaryContainer = BloxTextPrimary,
    secondary = BloxGreenRobux,
    onSecondary = BloxDarkBackground,
    secondaryContainer = BloxSurface,
    onSecondaryContainer = BloxTextPrimary,
    tertiary = BloxCyan,
    onTertiary = BloxTextPrimary,
    background = BloxDarkBackground,
    onBackground = BloxTextPrimary,
    surface = BloxSurface,
    onSurface = BloxTextPrimary,
    surfaceVariant = BloxSurfaceElevated,
    onSurfaceVariant = BloxTextSecondary,
    outline = BloxBorder,
    outlineVariant = BloxBorderLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force the immersive iconic Roblox Dark Mode
    content: @Composable () -> Unit
) {
    val colorScheme = BloxDarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = BloxDarkBackground.toArgb()
                window.navigationBarColor = BloxDarkBackground.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
