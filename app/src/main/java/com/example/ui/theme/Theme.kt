package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = HomeLinkPurplePrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF38006B),
    onPrimaryContainer = Color.White,
    secondary = HomeLinkCyanAccent,
    onSecondary = Color.Black,
    tertiary = HomeLinkEmeraldOnline,
    onTertiary = Color.Black,
    background = Color(0xFF07000E),
    onBackground = Color.White,
    surface = Color(0xFF16062B),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF280C42),
    onSurfaceVariant = Color(0xFFE1BEE7),
    error = HomeLinkErrorRed
)

private val LightColorScheme = DarkColorScheme // Default both to Black & Purple Dark Theme

@Composable
fun HomeLinkTheme(
    darkTheme: Boolean = true, // Default to Black and Purple theme
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color(0xFF0B192C).toArgb() // Tech Navy Blue status bar
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun HomeLinkNetworkTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    HomeLinkTheme(darkTheme = darkTheme, content = content)
}
