package org.example.project.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = darkColors(
            primary = BackgroundCyan,
            background = BackgroundCyan,
            surface = SurfaceCyan,
            onPrimary = TextPrimary,
            onBackground = TextPrimary,
        ),
        content = content
    )
}
