package org.example.project

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.max
import org.example.project.theme.Theme
import org.jzy3d.chart.AWTChart
import org.jzy3d.chart.factories.SwingChartFactory
import org.jzy3d.maths.BoundingBox3d
import org.jzy3d.maths.Coord3d
import org.jzy3d.plot3d.primitives.LineStrip
import org.jzy3d.plot3d.rendering.canvas.Quality
import org.jzy3d.plot3d.rendering.view.AWTView
import java.awt.Component
import javax.swing.JPanel


@Composable
fun ResultWindow(
    masse: String,
    gravite: String,
    vitesse0: String,
    alpha0: String
) {
    val m = masse.toFloatOrNull() ?: 1f
    val g = gravite.toFloatOrNull() ?: 9.81f
    val v0 = vitesse0.toFloatOrNull() ?: 0f
    val angle = alpha0.toFloatOrNull() ?: 0f

    var useDrag by remember { mutableStateOf(false) }
    var dragCoeff by remember { mutableStateOf(0.1f) }
    var mass by remember { mutableStateOf(m) }
    var timeStep by remember { mutableStateOf(0.005f) }
    var playing by remember { mutableStateOf(true) }

    val analytic = remember(v0, angle, g) { computeTrajectory(v0, angle, g) }
    val analyticMetrics = remember(v0, angle, g) { computeMetrics(v0, angle, g) }

    val trajectory = remember(v0, angle, g, mass, dragCoeff, useDrag, timeStep) {
        if (useDrag) {
            computeTrajectoryWithDrag(v0, angle, g, mass, dragCoeff, maxTime = 30f, dt = timeStep)
        } else {
            analytic
        }
    }

    var index by remember { mutableStateOf(0) }
    val estimatedTime = if (useDrag) trajectory.size * timeStep else analyticMetrics["TimeOfFlight"] ?: 0f

    LaunchedEffect(trajectory, playing) {
        index = 0
        if (!playing) return@LaunchedEffect
        while (playing && index < trajectory.size - 1) {
            index++
            delay(16L)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Metrics", style = MaterialTheme.typography.titleLarge,
                        color = Color.Gray
                )

                val tof = estimatedTime.toDouble()
                val maxHeight = trajectory.maxOfOrNull { it.y }?.toDouble() ?: 0.0
                val range = trajectory.maxOfOrNull { it.x }?.toDouble() ?: 0.0

                Text("Time of flight: ${"%.3f".format(tof)} s", color = Color.Gray)
                Text("Max height: ${"%.3f".format(maxHeight)} m", color = Color.Gray)
                Text("Range: ${"%.3f".format(range)} m", color = Color.Gray)

                Button(
                    onClick = {
                        generateReportDoc(
                            masse, gravite, vitesse0, alpha0,
                            tof.toFloat(),
                            maxHeight.toFloat(),
                            range.toFloat()
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Generate Report")
                }
            }

            Column(modifier = Modifier.width(240.dp)) {
                Text("Simulation Controls", style = MaterialTheme.typography.titleLarge,  color = Color.Gray
                )

                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Text("With Drag", color = Color.Gray)
                    Switch(checked = useDrag, onCheckedChange = { useDrag = it })
                }

                OutlinedTextField(
                    value = dragCoeff.toString(),
                    onValueChange = { dragCoeff = it.toFloatOrNull() ?: dragCoeff },
                    label = { Text("Drag coeff (c)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = timeStep.toString(),
                    onValueChange = { timeStep = it.toFloatOrNull() ?: timeStep },
                    label = { Text("RK4 dt (s)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { playing = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) { Text("Play") }

                    Button(
                        onClick = { playing = false; index = 0 },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) { Text("Reset") }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        val chart = SwingChartFactory().newChart(Quality.Advanced())
        val curve = createProjectileCurve(v0,angle,g)
        chart.add(curve)

        val X_MAX = 520f
        val Y_MAX = 2f
        val Z_MAX = 130f


        val newBounds = org.jzy3d.maths.BoundingBox3d(
            -10f, X_MAX, // X bounds
            -1f, Y_MAX, // Y bounds
            -10f,  Z_MAX  // Z bounds
        )

        chart.view.setBoundManual(newBounds)

        chart.view.updateBoundsForceRefresh(true)

         // calling swing panel
        HysChartPanal(chart)
        
        


    }
}

private fun AWTView.updateBoundsForceRefresh(bool: Boolean) {}

/**  the function to calculate and display the trajectory
 */


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
            chart.view.viewPoint = Coord3d(Math.PI / 4, Math.PI / 4, 1.0)
            try {
                chart.mouse.thread.start()
                chart.addKeyboardCameraController()
            } catch (e: Exception) {
            }
            var x = chart.apply {
                try {

                    mouse.thread.start()

                } catch (e: Exception) {
                }

                // quality = Quality.Fastest()
            }.canvas as Component
            jPanel.add(x)        },

        )

    DisposableEffect(chart) { onDispose { jPanel.removeAll() } }
}
fun createProjectileCurve(v0: Float, angleDeg: Float, g: Float): LineStrip {
    val points = mutableListOf<Coord3d>()
    val velocity = v0.toDouble()
    val gravity = g.toDouble()
    val angle = Math.toRadians(angleDeg.toDouble())
    val tMax = (2 * velocity * Math.sin(angle)) / gravity

    var t = 0.0
    val dt = tMax / 100 // Step size

    while (t <= tMax) {
        val x = velocity * t * Math.cos(angle)
        val y = velocity * t * Math.sin(angle) - 0.5 * gravity * t * t

        val zDepth = 1.0

        if (y >= 0) {
           // I want to display it in x and z I  set y to 1
            points.add(Coord3d(x, 1.0, y))
        }
        t += dt
    }

    //  Create the Drawable LineStrip
    val curve = LineStrip(points)

    curve.wireframeColor = org.jzy3d.colors.Color.RED
    return curve
}