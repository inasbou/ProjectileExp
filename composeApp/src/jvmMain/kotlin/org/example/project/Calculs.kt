package org.example.project

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.pow

import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Point(val x: Float, val y: Float)

/**
 * Analytic trajectory ignoring drag.
 */
fun computeTrajectory(v0: Float, angleDeg: Float, g: Float, steps: Int = 200): List<Point> {
    val angleRad = Math.toRadians(angleDeg.toDouble())
    val vx = v0 * cos(angleRad)
    val vy = v0 * sin(angleRad)
    val totalTime = (2.0 * vy) / g
    val dt = (totalTime / steps).coerceAtLeast(1e-4)
    val points = mutableListOf<Point>()
    for (i in 0..steps) {
        val t = i * dt
        val x = vx * t
        val y = vy * t - 0.5 * g * t * t
        if (y < 0.0) break
        points.add(Point(x.toFloat(), y.toFloat()))
    }
    return points
}

/**
 * Basic metrics ignoring drag.
 */
fun computeMetrics(v0: Float, angleDeg: Float, g: Float): Map<String, Float> {
    val a = Math.toRadians(angleDeg.toDouble())
    val vy = v0 * sin(a)
    val vx = v0 * cos(a)
    val T = (2.0 * vy) / g
    val H = (vy * vy) / (2.0 * g)
    val R = (v0 * v0 * sin(2.0 * a)) / g
    return mapOf(
        "TimeOfFlight" to T.toFloat(),
        "MaxHeight" to H.toFloat(),
        "Range" to R.toFloat()
    )
}

/**
 * Quadratic drag model using RK4 integration.
 *
 * dragCoeff is the combined coefficient such that drag force magnitude = dragCoeff * v^2.
 * For real systems: dragCoeff = 0.5 * rho * Cd * A
 *
 * state vector: [x, y, vx, vy]
 */
fun computeTrajectoryWithDrag(
    v0: Float,
    angleDeg: Float,
    g: Float,
    mass: Float,
    dragCoeff: Float,
    maxTime: Float = 20f,
    dt: Float = 0.005f
): List<Point> {
    val angle = Math.toRadians(angleDeg.toDouble())
    var x = 0.0
    var y = 0.0
    var vx = (v0 * cos(angle)).toDouble()
    var vy = (v0 * sin(angle)).toDouble()

    val points = mutableListOf<Point>()
    points.add(Point(x.toFloat(), y.toFloat()))

    var t = 0.0
    while (t < maxTime && y >= -1e-6) {
        // RK4 integration for vx, vy, x, y
        val state = doubleArrayOf(x, y, vx, vy)

        fun deriv(s: DoubleArray): DoubleArray {
            val sx = s[0]
            val sy = s[1]
            val svx = s[2]
            val svy = s[3]
            val speed = sqrt(svx * svx + svy * svy)
            val ax = if (speed > 0) - (dragCoeff / mass) * speed * svx else 0.0
            val ay = -g.toDouble() - (if (speed > 0) (dragCoeff / mass) * speed * svy else 0.0)
            return doubleArrayOf(svx, svy, ax, ay)
        }

        val k1 = deriv(state)
        val s2 = DoubleArray(4) { state[it] + k1[it] * (dt / 2.0) }
        val k2 = deriv(s2)
        val s3 = DoubleArray(4) { state[it] + k2[it] * (dt / 2.0) }
        val k3 = deriv(s3)
        val s4 = DoubleArray(4) { state[it] + k3[it] * dt }
        val k4 = deriv(s4)

        val next = DoubleArray(4) { i -> state[i] + (dt / 6.0) * (k1[i] + 2.0 * k2[i] + 2.0 * k3[i] + k4[i]) }

        x = next[0]
        y = next[1]
        vx = next[2]
        vy = next[3]

        if (y >= -1e-6) points.add(Point(x.toFloat(), y.toFloat()))
        t += dt
        if (points.size > 20000) break // safety
    }

    return points
}

/**
 * Estimates TimeOfFlight / Range / MaxHeight from the trajectory produced by computeTrajectoryWithDrag.
 */
fun computeMetricsFromTrajectory(points: List<Point>): Map<String, Float> {
    if (points.isEmpty()) return mapOf("TimeOfFlight" to 0f, "MaxHeight" to 0f, "Range" to 0f)
    val maxY = points.maxOf { it.y }
    val range = points.maxOf { it.x }
    // We don't track time per point here; assume uniform dt externally if needed.
    return mapOf(
        "TimeOfFlight" to 0f, // unknown without dt; ResultWindow will compute time using dt used
        "MaxHeight" to maxY,
        "Range" to range
    )
}

fun generateReportDoc(
    masse: String,
    gravite: String,
    vitesse0: String,
    alpha0: String,
    timeOfFlight: Float,
    maxHeight: Float,
    range: Float
) {
    val doc = XWPFDocument()

    val title = doc.createParagraph()
    title.createRun().apply {
        setText("Projectile Motion Report")
        fontSize = 20
        isBold = true
    }

    doc.createParagraph().createRun().apply {
        setText("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
        fontSize = 12
    }

    fun addEntry(label: String, value: String) {
        doc.createParagraph().createRun().apply {
            setText("$label: $value")
            fontSize = 14
        }
    }

    addEntry("Mass", "$masse kg")
    addEntry("Gravity", "$gravite m/s²")
    addEntry("Initial Velocity", "$vitesse0 m/s")
    addEntry("Initial Angle", "$alpha0 degrees")

    doc.createParagraph().createRun().apply {
        setText("---- Computed Results ----")
        isBold = true
        fontSize = 16
    }

    addEntry("Time of Flight", "%.3f s".format(timeOfFlight))
    addEntry("Maximum Height", "%.3f m".format(maxHeight))
    addEntry("Range", "%.3f m".format(range))

    val fileName = "projectile_report_${System.currentTimeMillis()}.docx"

    FileOutputStream(fileName).use { out ->
        doc.write(out)
    }

    println("Report saved: $fileName")
}