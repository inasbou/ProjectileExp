package org.example.project

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.max

@Composable
fun ResultWindow(
    masse: String,
    gravite: String,
    vitesse0: String,
    alpha0: String
) {
    // parse inputs with safe fallbacks
    val m = masse.toFloatOrNull() ?: 1f
    val g = gravite.toFloatOrNull() ?: 9.81f
    val v0 = vitesse0.toFloatOrNull() ?: 0f
    val angle = alpha0.toFloatOrNull() ?: 0f

    // UI state
    var useDrag by remember { mutableStateOf(false) }
    var dragCoeff by remember { mutableStateOf(0.1f) } // example default (0.5 * rho * Cd * A)
    var mass by remember { mutableStateOf(m) }
    var timeStep by remember { mutableStateOf(0.005f) } // used for RK4
    var playing by remember { mutableStateOf(true) }

    // compute trajectories (recomputed when inputs change)
    val analytic = remember(v0, angle, g) { computeTrajectory(v0, angle, g) }
    val analyticMetrics = remember(v0, angle, g) { computeMetrics(v0, angle, g) }

    val rkTrajectory = remember(v0, angle, g, mass, dragCoeff, useDrag, timeStep) {
        if (useDrag) {
            computeTrajectoryWithDrag(v0, angle, g, mass, dragCoeff, maxTime = 30f, dt = timeStep)
        } else {
            analytic
        }
    }

    // determine which trajectory to show
    val trajectory = rkTrajectory

    // animation index
    var index by remember { mutableStateOf(0) }

    // compute a dt estimate for time-of-flight when using RK solver:
    val estimatedTimeOfFlight = if (useDrag) {
        // rough estimate: dt * numberOfSteps (we used dt inside computeTrajectoryWithDrag)
        // in computeTrajectoryWithDrag we used dt param; here we can approximate:
        trajectory.size * timeStep
    } else {
        analyticMetrics["TimeOfFlight"] ?: 0f
    }

    LaunchedEffect(trajectory, playing) {
        index = 0
        if (!playing) return@LaunchedEffect
        // animate by stepping through points approx at 60 fps
        while (playing && index < trajectory.size - 1) {
            index++
            delay(16L) // ~60fps
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Metrics", style = MaterialTheme.typography.h6)
                val tof = if (!useDrag) analyticMetrics["TimeOfFlight"]?.toDouble() ?: 0.0 else estimatedTimeOfFlight.toDouble()
                val maxHeight = if (!useDrag) analyticMetrics["MaxHeight"]?.toDouble() ?: 0.0 else (trajectory.maxOfOrNull { it.y }?.toDouble() ?: 0.0)
                val range = if (!useDrag) analyticMetrics["Range"]?.toDouble() ?: 0.0 else (trajectory.maxOfOrNull { it.x }?.toDouble() ?: 0.0)

                Text("Time of flight: ${"%.3f".format(tof)} s")
                Text("Max height: ${"%.3f".format(maxHeight)} m")
                Text("Range: ${"%.3f".format(range)} m")
                Button(
                    onClick = {
                        generateReportDoc(
                            masse = masse,
                            gravite = gravite,
                            vitesse0 = vitesse0,
                            alpha0 = alpha0,
                            timeOfFlight = tof.toFloat(),
                            maxHeight = maxHeight.toFloat(),
                            range = range.toFloat()
                        )
                    },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Generate Report")
                }




            }

            Column(modifier = Modifier.width(240.dp)) {
                Text("Simulation Controls", style = MaterialTheme.typography.h6)
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Text("Drag")
                    Switch(checked = useDrag, onCheckedChange = { useDrag = it })
                }
                OutlinedTextField(
                    value = mass.toString(),
                    onValueChange = { mass = it.toFloatOrNull() ?: mass },
                    label = { Text("Mass (kg)") },
                    modifier = Modifier.fillMaxWidth()
                )
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
                    Button(onClick = { playing = true }) { Text("Play") }
                    Button(onClick = {
                        playing = false
                        index = 0
                    }) { Text("Reset") }
                   
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Canvas area for trajectory
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(420.dp)
                .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(6.dp))
                .padding(8.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                if (trajectory.size < 2) return@Canvas

                // compute scale
                val maxX = trajectory.maxOf { it.x }.coerceAtLeast(1f)
                val maxY = trajectory.maxOf { it.y }.coerceAtLeast(1f)

                val padding = 20f
                val drawableWidth = size.width - padding * 2
                val drawableHeight = size.height - padding * 2

                val scaleX = drawableWidth / maxX
                val scaleY = drawableHeight / maxY

                // draw ground line
                val groundY = size.height - padding
                drawLine(
                    color = Color.DarkGray,
                    start = Offset(padding, groundY),
                    end = Offset(size.width - padding, groundY),
                    strokeWidth = 2f
                )

                // draw path
                for (i in 1 until trajectory.size) {
                    val p1 = trajectory[i - 1]
                    val p2 = trajectory[i]
                    val x1 = padding + p1.x * scaleX
                    val y1 = groundY - (p1.y * scaleY)
                    val x2 = padding + p2.x * scaleX
                    val y2 = groundY - (p2.y * scaleY)
                    drawLine(
                        color = if (useDrag) Color(0xFF1E88E5) else Color.Red,
                        start = Offset(x1, y1),
                        end = Offset(x2, y2),
                        strokeWidth = 3f
                    )
                }

                // draw projectile (current index)
                val current = trajectory.getOrNull(index.coerceIn(0, trajectory.lastIndex)) ?: trajectory.last()
                val cx = padding + current.x * scaleX
                val cy = groundY - (current.y * scaleY)
                drawCircle(color = Color.Black, radius = 8f, center = Offset(cx, cy))
            }
        }
    }
}
