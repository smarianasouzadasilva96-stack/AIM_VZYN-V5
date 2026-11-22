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
import com.vzyn.ffpanel.storage.SettingsState
import com.vzyn.ffpanel.ui.theme.hueColor
import kotlin.math.sin

@Composable
fun RecoilCanvas(state: SettingsState, modifier: Modifier = Modifier) {
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
        val origin = Offset(size.width / 2f, size.height * 0.78f)

        drawLine(Color(0x33FFFFFF), Offset(0f, origin.y), Offset(size.width, origin.y), 1f)
        drawCircle(hueColor(120f, alpha = 0.6f), 14f, origin, style = Stroke(1.5f))

        val rpm = state.num("fire_rate")
        val interval = 60f / rpm
        val shots = 28
        val vert = if (state.on("recoil_none")) 0.35f else state.num("recoil_vertical") / 100f
        val horiz = if (state.on("recoil_none")) 0.2f else state.num("recoil_horizontal") / 100f
        val smooth = state.num("recoil_smooth") / 100f
        val spread = state.num("recoil_spread") / 100f * 26f
        val acc = state.num("accuracy_sim") / 100f

        var y = origin.y
        val color = hueColor(state.hue("esp_color"), alpha = 0.9f)
        for (i in 0 until shots) {
            val progress = i.toFloat() / shots
            val rise = 8f * vert * (1f + progress * 1.8f) * (1f - smooth * 0.45f)
            y -= rise
            val drift = sin(i * 0.9f) * 16f * horiz + sin(t * 2f + i * 0.4f) * spread * 0.4f
            val jitter = (1f - acc) * 10f * sin(i * 2.3f)
            val p = Offset(origin.x + drift + jitter, y)
            drawCircle(color, 2.2f, p)
            if (i % 6 == 0) {
                drawLine(color.copy(alpha = 0.35f), p, origin, 1f)
            }
            if (state.on("recoil_stabilizer") && i == shots - 1) {
                drawCircle(Color(0xFF00FF9D), 9f, p, style = Stroke(2f))
            }
            if (state.on("rapid_fire")) {
                drawCircle(Color(0xFFFFD166), 1.4f, p + Offset(6f, 2f))
            }
        }

        val weapon = state.optionText("weapon_selector")
        drawText(
            "$weapon · ${rpm.toInt()} rpm · ${state.optionText("recoil_pattern")}",
            size.width / 2f, 18f, newTextPaint(Color(0xFF8C97B5), 11f, false)
        )
        drawText(
            "Δt ${"%.1f".format(interval * 1000f)}ms",
            size.width / 2f, size.height - 10f, newTextPaint(Color(0xFF8C97B5), 10f, false)
        )
        if (state.on("shooting_range")) {
            drawRect(
                Color(0x22FF2E63),
                Offset(size.width * 0.1f, size.height * 0.12f),
                Size(size.width * 0.8f, size.height * 0.55f),
                style = Stroke(1f)
            )
        }
    }
}
