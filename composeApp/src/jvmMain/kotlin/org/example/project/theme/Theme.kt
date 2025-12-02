package org.example.project.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Define colors
val Primary = Color(0xFF5977F7)
val Background = Color(0xFFF4F4F4)

val PrimaryDark = Color.Cyan
val BackgroundDark = Color(0xFF1E1F22)
// Define themes
val LightColors = lightColorScheme(
    primary = Primary,
    background = Background
)

val DarkColors = darkColorScheme(
    primary = PrimaryDark,
    background = BackgroundDark
)

@Composable
fun Theme(content: @Composable () -> Unit) {
    val dark = androidx.compose.foundation.isSystemInDarkTheme()

    MaterialTheme(
        colorScheme = if (dark) DarkColors else LightColors,
        content = content
    )
}
