package org.example.project

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.max
import org.example.project.theme.Theme

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
//                        generateReportDoc(
//                            masse, gravite, vitesse0, alpha0,
//                            tof.toFloat(),
//                            maxHeight.toFloat(),
//                            range.toFloat()
  //                      )
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

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(420.dp)
                .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(6.dp))
                .padding(8.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                if (trajectory.size < 2) return@Canvas

                val maxX = trajectory.maxOf { it.x }.coerceAtLeast(1f)
                val maxY = trajectory.maxOf { it.y }.coerceAtLeast(1f)

                val padding = 20f
                val drawableWidth = size.width - padding * 2
                val drawableHeight = size.height - padding * 2

                val scaleX = drawableWidth / maxX
                val scaleY = drawableHeight / maxY
                val groundY = size.height - padding

                drawLine(
                    color = Color.DarkGray,
                    start = Offset(padding, groundY),
                    end = Offset(size.width - padding, groundY),
                    strokeWidth = 2f
                )

                for (i in 1 until trajectory.size) {
                    val p1 = trajectory[i - 1]
                    val p2 = trajectory[i]

                    drawLine(
                        color = if (useDrag) Color(0xFF1E88E5) else Color.Red,
                        start = Offset(padding + p1.x * scaleX, groundY - p1.y * scaleY),
                        end = Offset(padding + p2.x * scaleX, groundY - p2.y * scaleY),
                        strokeWidth = 3f
                    )
                }

                val current = trajectory[index.coerceIn(0, trajectory.lastIndex)]
                drawCircle(
                    color = Color.Black,
                    radius = 8f,
                    center = Offset(
                        padding + current.x * scaleX,
                        groundY - current.y * scaleY
                    )
                )
            }
        }
    }
}

/**  the fucntion to calculate and display the trajectory
 * */