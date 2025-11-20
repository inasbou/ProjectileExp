package org.example.project

import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {

    var showResultWindow by remember { mutableStateOf(false) }

    // Data passed to second window
    var masse by remember { mutableStateOf("") }
    var gravite by remember { mutableStateOf("") }
    var vitesse0 by remember { mutableStateOf("") }
    var alpha0 by remember { mutableStateOf("") }

    // First window (input)
    Window(
        onCloseRequest = ::exitApplication,
        title = "Projectile - Input"
    ) {
        App(
            onSubmit = { m, g, v, a ->
                masse = m
                gravite = g
                vitesse0 = v
                alpha0 = a
                showResultWindow = true
            }
        )
    }

    // Second window (results + animation)
    if (showResultWindow) {
        Window(
            onCloseRequest = { showResultWindow = false },
            title = "Projectile - Results"
        ) {
            ResultWindow(
                masse = masse,
                gravite = gravite,
                vitesse0 = vitesse0,
                alpha0 = alpha0
            )
        }
    }
}
