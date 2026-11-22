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
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AimCanvas(state: SettingsState, modifier: Modifier = Modifier, onFps: (Int) -> Unit = {}) {
    val time = remember { mutableFloatStateOf(0f) }
    val aimX = remember { mutableFloatStateOf(0.5f) }
    val aimY = remember { mutableFloatStateOf(0.5f) }
    val trailX = remember { ArrayDeque<Float>() }
    val trailY = remember { ArrayDeque<Float>() }

    LaunchedEffect(Unit) {
        var last = 0L
        var frames = 0
        var window = 0L
        while (true) {
            withFrameNanos { now ->
                if (last != 0L) time.floatValue += ((now - last) / 1e9f).coerceAtMost(0.05f)
                last = now
                frames++
                if (window == 0L) window = now
                if (now - window >= 500_000_000L) {
                    onFps((frames * 1_000_000_000L / (now - window)).toInt())
                    frames = 0
                    window = now
                }
            }
        }
    }

    Canvas(modifier) {
        val t = time.floatValue
        backdrop()

        val tx = 0.5f + 0.33f * sin(t * 1.15f)
        val ty = 0.45f + 0.27f * cos(t * 0.83f)
        val target = Offset(size.width * tx, size.height * ty)

        trailX.addLast(target.x)
        trailY.addLast(target.y)
        val delayFrames = (state.num("aim_delay") / 1000f * 60f).toInt().coerceIn(0, 30)
        while (trailX.size > 40) { trailX.removeFirst(); trailY.removeFirst() }
        val delayed = Offset(
            trailX.getOrNull((trailX.size - 1 - delayFrames).coerceAtLeast(0)) ?: target.x,
            trailY.getOrNull((trailY.size - 1 - delayFrames).coerceAtLeast(0)) ?: target.y
        )

        val prediction = if (state.on("aim_prediction")) state.num("aim_accuracy") / 100f * 26f else 0f
        val goal = delayed + Offset(
            state.num("aim_offset_x") * 0.35f + prediction * sin(t * 1.15f),
            state.num("aim_offset_y") * 0.35f - prediction * cos(t * 0.83f)
        )

        val fovR = state.num("fov_radius") * 0.7f
        val inFov = (goal - Offset(size.width / 2f, size.height / 2f)).getDistance() <= fovR
        val deadzone = state.num("aim_deadzone") / 100f * 60f

        if (state.on("aim_enable")) {
            val smooth = 1f + state.num("aim_smoothness") / 18f
            var k = (state.num("aim_speed") / 100f) / smooth * (state.num("aim_strength") / 100f)
            k *= when (state.idx("aim_curve")) {
                1 -> 0.7f
                2 -> 1.4f
                3 -> 1.1f
                else -> 1f
            }
            k = (k * (1f + state.num("aim_acceleration") / 250f)).coerceIn(0f, 0.9f)
            if (state.on("aim_lock") || inFov) {
                val dist = (goal - Offset(size.width * aimX.floatValue, size.height * aimY.floatValue))
                if (dist.getDistance() > deadzone) {
                    aimX.floatValue += dist.x * k / size.width
                    aimY.floatValue += dist.y * k / size.height
                }
            }
        }
        val aim = Offset(size.width * aimX.floatValue, size.height * aimY.floatValue)

        val fovHue = if (state.on("fov_rainbow")) (t * 90f) % 360f else state.hue("fov_color")
        val fovColor = hueColor(fovHue, alpha = state.num("fov_opacity") / 100f)
        if (state.on("fov_circle")) {
            drawCircle(fovColor, fovR, Offset(size.width / 2f, size.height / 2f), style = Stroke(state.num("fov_thickness")))        }
        if (state.on("fov_cross")) {
            val c = Offset(size.width / 2f, size.height / 2f)
            drawLine(fovColor, c - Offset(fovR, 0f), c - Offset(30f, 0f), 2f)
            drawLine(fovColor, c + Offset(30f, 0f), c + Offset(fovR, 0f), 2f)
            drawLine(fovColor, c - Offset(0f, fovR), c - Offset(0f, 30f), 2f)
            drawLine(fovColor, c + Offset(0f, 30f), c + Offset(0f, fovR), 2f)
        }

        val boxW = 46f
        val boxH = 96f
        val topLeft = target - Offset(boxW / 2f, boxH / 2f)
        drawRect(Color(0x2200FF9D), topLeft, Size(boxW, boxH))
        drawRect(Color(0xFF00FF9D), topLeft, Size(boxW, boxH), style = Stroke(2f))
        drawCircle(Color(0xFF00FF9D), 5f, target - Offset(0f, boxH / 2f))

        val bone = when (state.idx("aim_bone")) {
            0 -> 0.06f
            1 -> 0.12f
            2 -> 0.30f
            else -> 0.46f
        }
        val aimTarget = Offset(target.x, target.y - boxH / 2f + boxH * bone)
        drawLine(Color(0x55FFFFFF), target - Offset(0f, boxH / 2f), target + Offset(0f, boxH / 2f), 1f)

        val color = if (state.on("aim_auto_headshot")) Color(0xFFFFD166) else hueColor(state.hue("fov_color"))
        val spread = 12f + (100f - state.num("aim_accuracy")) * 0.25f
        drawLine(color, aim - Offset(spread, 0f), aim - Offset(4f, 0f), 2f)
        drawLine(color, aim + Offset(4f, 0f), aim + Offset(spread, 0f), 2f)
        drawLine(color, aim - Offset(0f, spread), aim - Offset(0f, 4f), 2f)
        drawLine(color, aim + Offset(0f, 4f), aim + Offset(0f, spread), 2f)
        drawCircle(color, 2.5f, aim)
        drawLine(Color(0x3300BFFF), aim, aimTarget, 1.5f)

        if (state.on("aim_tracking")) {
            drawCircle(Color(0x66FF2E63), 14f, target, style = Stroke(1.5f))
        }
        if (state.on("aim_stabilizer")) {
            drawCircle(Color(0x4400BFFF), 22f, aim, style = Stroke(1f))
        }

        val paint = newTextPaint(Color.White, 11f, false)
        drawText(
            "TARGET ${(abs(target.x - aim.x) + abs(target.y - aim.y)).toInt()}px",
            size.width / 2f, size.height - 10f, paint
        )
        if (!state.on("aim_enable")) {
            drawText("AIMBOT OFF", size.width / 2f, 22f, newTextPaint(Color(0xFFFF2E63), 13f))
        }
    }
}
