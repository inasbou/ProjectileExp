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
            //MainScreen()
            val chart = SwingChartFactory().newChart(Quality.Advanced())
            val curve = createProjectileCurve()
            chart.add(curve)
            HysChartPanal(chart)

        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HysChartPanal(chart: AWTChart) {
    val jPanel: JPanel = remember { JPanel() }
    var isLoding by remember { mutableStateOf(true) }


    SwingPanel(
        modifier = Modifier.fillMaxSize().padding(start = if (isLoding) 1.dp else 0.dp),
        factory = {
//            Platform.runLater {
//            }
            jPanel.removeAll()
            chart.view.viewPoint = Coord3d(0.0, Math.PI / 10, 1.0)
            try {
                chart.mouse.thread.start()
                chart.addKeyboardCameraController()
            } catch (e: Exception) {
            }
            var x = chart.apply {
                try {

                    mouse.thread.start()

//                    axisLayout.gridColor = colorToJzy3dColor(axisColor)
//
//                    this.axisLayout.mainColor = colorToJzy3dColor(axisColor)
//
//                    view.backgroundColor = colorToJzy3dColor(backgroundColor)
//
//                    if (this.colorbar != null) {
//                        this.colorbar.apply {
//                            background = colorToJzy3dColor(backgroundColor)
//                            foreground = colorToJzy3dColor(axisColor)
//                        }
//                    }
                } catch (e: Exception) {
                }

                // quality = Quality.Fastest()
            }.canvas as Component
            jPanel.add(x)        },

        )

    DisposableEffect(chart) { onDispose { jPanel.removeAll() } }
}
fun createProjectileCurve(): LineStrip {
    val points = mutableListOf<Coord3d>()
    val velocity = 50.0 // Initial velocity
    val angle = Math.PI / 4 // 45 degrees launch angle
    val g = 9.81 // Gravity
    val tMax = (2 * velocity * Math.sin(angle)) / g // Max flight time

    // Calculate points over time (t)
    var t = 0.0
    val dt = tMax / 100 // Step size

    //

    while (t <= tMax) {
        // Projectile Motion Equations (assuming X is horizontal, Y is vertical)
        val x = velocity * t * Math.cos(angle)
        val y = velocity * t * Math.sin(angle) - 0.5 * g * t * t

        // Since Jzy3d often uses Y and Z as the "floor", we'll plot
        // the 2D parabola (x, y) with a small offset for the Z-axis (depth).
        val zDepth = 0.5

        if (y >= 0) { // Only plot above the ground
            points.add(Coord3d(x, y, zDepth))
        }
        t += dt
    }

    // 2. Create the Drawable LineStrip (Curve)

    val curve = LineStrip(points)

// FIX: Explicitly set the color after creation
    curve.wireframeColor = Color.RED
    return curve
}