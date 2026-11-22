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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import com.vzyn.ffpanel.storage.SettingsState
import com.vzyn.ffpanel.ui.theme.hueColor
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun FovCanvas(state: SettingsState, modifier: Modifier = Modifier) {
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

        val hue = if (state.on("fov_rainbow")) (t * 90f) % 360f else state.hue("fov_color")
        val baseAlpha = state.num("fov_opacity") / 100f
        val pulse = 1f + (state.num("fov_pulse") / 100f) * 0.18f * sin(t * 5f)
        val radius = state.num("fov_radius") * pulse
        val center = Offset(
            size.width * (state.num("fov_pos_x") / 100f),
            size.height * (state.num("fov_pos_y") / 100f)
        )
        val stroke = state.num("fov_thickness")
        val main = hueColor(hue, alpha = baseAlpha)

        val glow = state.num("fov_glow") / 100f
        if (glow > 0f) {
            drawCircle(hueColor(hue, alpha = 0.10f * glow), radius * 1.25f, center)
            drawCircle(hueColor(hue, alpha = 0.16f * glow), radius * 1.08f, center)
        }

        rotate(state.num("fov_rotation"), center) {
            if (state.on("fov_filled")) {
                val brush = if (state.on("fov_gradient")) {
                    Brush.radialGradient(
                        listOf(hueColor(hue, alpha = baseAlpha * 0.22f), Color.Transparent),
                        center = center,
                        radius = radius
                    )
                } else {
                    Brush.linearGradient(listOf(main.copy(alpha = 0.16f), Color.Transparent))
                }
                if (state.on("fov_circle")) {
                    drawCircle(brush, radius, center)
                }
                if (state.on("fov_square")) {
                    drawRect(brush, center - Offset(radius, radius), Size(radius * 2, radius * 2))
                }
            }
            if (state.on("fov_circle")) {
                drawCircle(main, radius, center, style = Stroke(stroke))
            }
            if (state.on("fov_square")) {
                drawRect(
                    main,
                    center - Offset(radius, radius),
                    Size(radius * 2, radius * 2),
                    style = Stroke(stroke)
                )
            }
            if (state.on("fov_cross")) {
                val gap = radius * 0.35f
                drawLine(main, center - Offset(radius, 0f), center - Offset(gap, 0f), stroke)
                drawLine(main, center + Offset(gap, 0f), center + Offset(radius, 0f), stroke)
                drawLine(main, center - Offset(0f, radius), center - Offset(0f, gap), stroke)
                drawLine(main, center + Offset(0f, gap), center + Offset(0f, radius), stroke)
            }
            if (state.on("fov_animated")) {
                for (i in 0 until 3) {
                    val a = t * 2f + i * 2.09f
                    drawCircle(
                        main,
                        radius,
                        center + Offset(cos(a) * radius * 0.6f, sin(a) * radius * 0.6f),
                        style = Stroke(stroke / 2f)
                    )
                }
            }
        }

        drawCircle(hueColor(hue), 3f, center)
        if (state.on("fov_dynamic")) {
            drawCircle(main, radius * 1.12f, center, style = Stroke(stroke / 2f))
        }
        if (state.on("fov_static")) {
            drawCircle(main.copy(alpha = baseAlpha * 0.35f), radius * 0.92f, center, style = Stroke(1f))
        }
    }
}
