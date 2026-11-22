package com.vzyn.ffpanel.sim

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import com.vzyn.ffpanel.storage.SettingsState
import com.vzyn.ffpanel.ui.theme.hueColor
import kotlin.math.sin

@Composable
fun CrosshairCanvas(state: SettingsState, modifier: Modifier = Modifier) {
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
        if (!state.on("cross_enable")) {
            drawText("CROSSHAIR OFF", size.width / 2f, size.height / 2f, newTextPaint(Color(0xFFFF2E63), 13f))
            return@Canvas
        }

        val hue = when {
            state.on("cross_rainbow") -> (t * 120f) % 360f
            state.on("cross_rgb") -> (t * 60f) % 360f
            else -> state.hue("cross_color")
        }
        val baseAlpha = state.num("cross_opacity") / 100f
        val pulse = when (state.idx("cross_anim")) {
            1 -> 1f + 0.18f * sin(t * 6f)
            2 -> 1f
            else -> 1f
        }
        val color = hueColor(hue, alpha = baseAlpha)
        val center = Offset(
            size.width / 2f,
            when (state.idx("cross_pos")) {
                1 -> size.height * 0.25f
                2 -> size.height * 0.75f
                else -> size.height / 2f
            }
        )
        val size0 = state.num("cross_size") * pulse
        val gap = state.num("cross_gap")
        val thickness = state.num("cross_thickness")
        val glow = state.num("cross_glow") / 100f

        if (glow > 0f) {
            drawCircle(hueColor(hue, alpha = 0.12f * glow), size0 * 1.4f, center)
        }

        val rotation = if (state.idx("cross_anim") == 2) (t * 90f) % 360f else state.num("cross_rotation")
        rotate(rotation, center) {
            if (state.on("cross_circle")) {
                drawCircle(color, size0 * 0.6f, center, style = Stroke(thickness))
            }
            if (state.on("cross_outline")) {
                drawCircle(color.copy(alpha = baseAlpha * 0.35f), size0 * 0.8f, center, style = Stroke(1f))
            }
            if (state.on("cross_cross")) {
                drawLine(color, center - Offset(gap + size0, 0f), center - Offset(gap, 0f), thickness)
                drawLine(color, center + Offset(gap, 0f), center + Offset(gap + size0, 0f), thickness)
                drawLine(color, center - Offset(0f, gap + size0), center - Offset(0f, gap), thickness)
                drawLine(color, center + Offset(0f, gap), center + Offset(0f, gap + size0), thickness)
            }
            if (state.on("cross_tshape")) {
                drawLine(color, center - Offset(gap + size0, 0f), center - Offset(gap, 0f), thickness)
                drawLine(color, center + Offset(gap, 0f), center + Offset(gap + size0, 0f), thickness)
                drawLine(color, center - Offset(0f, gap + size0 * 0.6f), center - Offset(0f, gap), thickness)
            }
        }
        if (state.on("cross_dot")) {
            drawCircle(color, thickness * 1.2f + 0.8f, center)
        }

        drawText(
            "preset: ${state.optionText("cross_presets")}",
            size.width / 2f, size.height - 10f, newTextPaint(Color(0xFF8C97B5), 10f, false)
        )
    }
}
