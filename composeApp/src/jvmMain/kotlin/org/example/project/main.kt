package org.example.project

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.example.project.theme.Theme
//import javafx.application.Platform
import org.jzy3d.chart.AWTChart
import org.jzy3d.chart.factories.SwingChartFactory
import org.jzy3d.maths.Coord3d
import org.jzy3d.plot3d.rendering.canvas.Quality
import java.awt.Component
import javax.swing.JPanel
import org.jzy3d.plot3d.primitives.LineStrip
import org.jzy3d.colors.Color


@Composable
fun MainScreen() {
    val viewModel = AppContainer.mainViewModel
    val state = viewModel.uiState



    Column {
        TabRow(selectedTabIndex = state.currentTab.ordinal) {
            Tab(
                selected = state.currentTab == TabType.Input,
                onClick = { viewModel.onEvent(AppEvent.SelectTab(TabType.Input)) },
                text = { Text("Input") }
            )
            Tab(
                selected = state.currentTab == TabType.Results,
                onClick = { viewModel.onEvent(AppEvent.SelectTab(TabType.Results)) },
                text = { Text("Results") }
            )
        }

        Box(Modifier.fillMaxSize()) {
            when (state.currentTab) {
                TabType.Input ->
                    InputScreen()

                TabType.Results ->
                    ResultWindow(
                        state.masse,
                        state.gravite,
                        state.vitesse0,
                        state.alpha0
                    )
            }
        }
    }
}

fun main() = application {


    try {
        // ⚡ Enable GPU acceleration for Compose
        System.setProperty("compose.swing.render.on.graphics", "false")
        System.setProperty("compose.interop.blending", "true")

        // ⚡ GPU Acceleration for Java2D (Swing, Lets-Plot)
        System.setProperty("sun.java2d.opengl", "true")
        System.setProperty("sun.java2d.d3d", "false") // Avoids using Direct3D on Windows

        // ⚡ Jzy3D OpenGL Optimization
        System.setProperty("jzy3d.native.opengl", "true")
        System.setProperty("jzy3d.opengl.profile", "GL4")

        // ⚡ JavaFX Performance Boost (FXyz)
        System.setProperty("prism.order", "es2")
        System.setProperty("prism.vsync", "true")
        System.setProperty("prism.text", "t2k")
        System.setProperty("prism.forceGPU", "true")

    } catch (e: Exception) {
    }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Projectile Simulation"
    ) {
        Theme {
            MainScreen()


        }
    }
}

