package com.vzyn.ffpanel.sim

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import com.vzyn.ffpanel.storage.SettingsState
import com.vzyn.ffpanel.ui.theme.hueColor
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RadarCanvas(state: SettingsState, modifier: Modifier = Modifier) {
    val phase = remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        var last = 0L
        while (true) {
            withFrameNanos { now ->
                if (last != 0L) phase.floatValue += ((now - last) / 1e9f).coerceAtMost(0.05f)
                last = now
            }
        }
    }
    Canvas(modifier) {
        val t = phase.floatValue
        backdrop()
        if (!state.on("radar_enable")) {
            drawText("RADAR OFF", size.width / 2f, size.height / 2f, newTextPaint(Color(0xFFFF2E63), 13f))
            return@Canvas
        }

        val hue = if (state.on("radar_rgb")) (t * 90f) % 360f else state.hue("esp_color")
        val alpha = state.num("radar_opacity") / 100f
        val color = hueColor(hue, alpha = alpha)
        val radius = minOf(size.width, size.height) * 0.36f * (state.num("radar_size") / 100f)
        val origin = when (state.idx("radar_position")) {
            1 -> Offset(radius + 16f, radius + 16f)
            2 -> Offset(size.width - radius - 16f, radius + 16f)
            3 -> Offset(radius + 16f, size.height - radius - 16f)
            4 -> Offset(size.width - radius - 16f, size.height - radius - 16f)
            else -> Offset(size.width / 2f, size.height / 2f)
        }
        val zoom = state.num("radar_zoom") / 120f

        if (state.on("radar_grid")) {
            for (i in 1..3) {
                val r = radius * i / 4f
                drawCircle(color.copy(alpha = alpha * 0.25f), r, origin, style = Stroke(1f))
            }
            drawLine(color.copy(alpha = alpha * 0.25f), origin - Offset(radius, 0f), origin + Offset(radius, 0f), 1f)
            drawLine(color.copy(alpha = alpha * 0.25f), origin - Offset(0f, radius), origin + Offset(0f, radius), 1f)
        }

        rotate(state.num("radar_rotation") + t * 18f, origin) {
            if (state.on("radar_circular")) {
                drawCircle(color, radius, origin, style = Stroke(2f))
            }
            if (state.on("radar_square")) {
                drawRect(color, origin - Offset(radius, radius), Size(radius * 2f, radius * 2f), style = Stroke(2f))
            }
            val sweep = t * 2.4f
            drawLine(color, origin, origin + Offset(cos(sweep) * radius, sin(sweep) * radius), 2f)
            if (state.on("radar_dots")) {
                val blips = listOf(
                    Triple(0.4f, 0.9f, true),
                    Triple(2.1f, 1.2f, false),
                    Triple(3.6f, 0.7f, false),
                    Triple(5.0f, 1.0f, true)
                )
                blips.forEach { (angle, factor, ally) ->
                    val d = radius * 0.6f * (factor / zoom).coerceIn(0.25f, 1.1f)
                    val p = origin + Offset(cos(angle + t * 0.2f) * d, sin(angle + t * 0.2f) * d)
                    drawCircle(
                        if (state.on("radar_team_colors") && ally) Color(0xFF00FF9D) else Color(0xFFFF2E63),
                        4f, p
                    )
                }
            }
        }
        drawCircle(hueColor(120f, alpha = 0.9f), 3f, origin)
    }
}
