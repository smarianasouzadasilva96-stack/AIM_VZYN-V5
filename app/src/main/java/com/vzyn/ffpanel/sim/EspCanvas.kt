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
import kotlin.math.cos
import kotlin.math.sin

private data class EspTarget(
    val nx: Float,
    val ny: Float,
    val dist: Int,
    val hp: Int,
    val armor: Int,
    val name: String,
    val weapon: String,
    val ally: Boolean
)

@Composable
fun EspCanvas(state: SettingsState, modifier: Modifier = Modifier) {
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
        if (!state.on("esp_master")) {
            drawText("ESP OFF", size.width / 2f, size.height / 2f, newTextPaint(Color(0xFFFF2E63), 14f))
            return@Canvas
        }

        val baseHue = if (state.on("esp_rainbow")) (t * 80f) % 360f else state.hue("esp_color")
        val alpha = 1f - state.num("esp_transparency") / 100f * 0.75f
        val scale = state.num("esp_scale") / 100f
        val thickness = state.num("esp_thickness")
        val font = state.num("esp_font")
        val maxDist = state.num("esp_max_distance")

        val targets = listOf(
            EspTarget(0.24f + 0.03f * sin(t * 1.4f), 0.62f, 38, 82, 40, "VZN_Kai", "AK47", true),
            EspTarget(0.52f, 0.58f + 0.02f * cos(t * 1.1f), 74, 55, 0, "Ghost", "MP40", false),
            EspTarget(0.79f + 0.02f * cos(t * 0.9f), 0.65f, 141, 100, 15, "Raven", "SCAR", false)
        )

        targets.forEachIndexed { index, target ->
            if (target.dist > maxDist) return@forEachIndexed
            val hue = if (state.on("esp_team_color") && target.ally) 120f else baseHue + index * 22f
            val color = hueColor(hue, alpha = alpha)
            val highlight = state.on("esp_target_highlight") && !target.ally

            val h = 88f * scale
            val w = 42f * scale
            val center = Offset(size.width * target.nx, size.height * target.ny)
            val topLeft = Offset(center.x - w / 2f, center.y - h / 2f)
            val boxSize = Size(w, h)

            if (state.on("esp_glow")) {
                drawRect(color.copy(alpha = alpha * 0.15f), topLeft - Offset(4f, 4f), Size(w + 8f, h + 8f))
            }
            if (state.on("esp_filled")) {
                drawRect(color.copy(alpha = alpha * 0.14f), topLeft, boxSize)
            }
            if (state.on("esp_box2d")) {
                drawRect(color, topLeft, boxSize, style = Stroke(if (highlight) thickness + 1f else thickness))
            }
            if (state.on("esp_box3d")) {
                drawRect(color, topLeft, boxSize, style = Stroke(thickness * 0.8f))
                val d = 12f * scale
                drawLine(color, topLeft, topLeft + Offset(d, -d), thickness)
                drawLine(color, topLeft + Offset(w, 0f), topLeft + Offset(w + d, -d), thickness)
                drawLine(color, topLeft + Offset(w, h), topLeft + Offset(w + d, h - d), thickness)
                drawLine(color, topLeft + Offset(0f, h), topLeft + Offset(d, h - d), thickness)
            }
            if (state.on("esp_corner")) {
                val c = 12f * scale
                drawLine(color, topLeft, topLeft + Offset(c, 0f), thickness)
                drawLine(color, topLeft, topLeft + Offset(0f, c), thickness)
                drawLine(color, topLeft + Offset(w, 0f), topLeft + Offset(w - c, 0f), thickness)
                drawLine(color, topLeft + Offset(w, 0f), topLeft + Offset(w, c), thickness)
                drawLine(color, topLeft + Offset(0f, h), topLeft + Offset(c, h), thickness)
                drawLine(color, topLeft + Offset(0f, h), topLeft + Offset(0f, h - c), thickness)
                drawLine(color, topLeft + Offset(w, h), topLeft + Offset(w - c, h), thickness)
                drawLine(color, topLeft + Offset(w, h), topLeft + Offset(w, h - c), thickness)
            }
            if (state.on("esp_outline")) {
                drawRect(color.copy(alpha = 0.4f), topLeft - Offset(2f, 2f), Size(w + 4f, h + 4f), style = Stroke(1f))
            }
            if (state.on("esp_chams")) {
                drawRect(Color(0x22FF2E63), topLeft, boxSize)
            }
            if (state.on("esp_skeleton")) {
                val skel = hueColor(state.hue("esp_skel_color"), alpha = alpha)
                val neck = Offset(center.x, topLeft.y + h * 0.22f)
                val hip = Offset(center.x, topLeft.y + h * 0.62f)
                drawLine(skel, neck, hip, thickness)
                drawLine(skel, neck, Offset(center.x - w * 0.45f, topLeft.y + h * 0.40f), thickness)
                drawLine(skel, neck, Offset(center.x + w * 0.45f, topLeft.y + h * 0.40f), thickness)
                drawLine(skel, hip, Offset(center.x - w * 0.35f, topLeft.y + h), thickness)
                drawLine(skel, hip, Offset(center.x + w * 0.35f, topLeft.y + h), thickness)
                drawCircle(skel, 6f * scale, Offset(center.x, topLeft.y + h * 0.09f), style = Stroke(thickness))
            }
            if (state.on("esp_head_dot")) {
                drawCircle(color, 3.5f * scale, Offset(center.x, topLeft.y + h * 0.09f))
            }
            if (state.on("esp_head_circle")) {
                drawCircle(color, 9f * scale, Offset(center.x, topLeft.y + h * 0.09f), style = Stroke(thickness))
            }
            if (state.on("esp_health")) {
                val hpColor = hueColor(state.hue("esp_health_color"), alpha = alpha)
                drawRect(Color(0x55000000), topLeft - Offset(7f, 0f), Size(3.5f, h))
                drawRect(hpColor, topLeft - Offset(7f, h * (1f - target.hp / 100f)), Size(3.5f, h * target.hp / 100f))
            }
            if (state.on("esp_armor")) {
                val arColor = hueColor(state.hue("esp_armor_color"), alpha = alpha)
                drawRect(Color(0x55000000), topLeft + Offset(w + 4f, 0f), Size(3.5f, h))
                drawRect(arColor, topLeft + Offset(w + 4f, h * (1f - target.armor / 100f)), Size(3.5f, h * target.armor / 100f))
            }
            val boxColor = hueColor(state.hue("esp_box_color"), alpha = alpha)
            val lineColor = hueColor(state.hue("esp_line_color"), alpha = alpha)
            if (state.on("esp_tracer")) {
                drawLine(lineColor, Offset(size.width / 2f, size.height), topLeft + Offset(w / 2f, h), thickness * 0.7f)
            }
            if (state.on("esp_line")) {
                drawLine(lineColor, Offset(size.width / 2f, 0f), Offset(center.x, topLeft.y), thickness * 0.5f)
            }
            if (state.on("esp_name")) {
                drawText(target.name, center.x, topLeft.y - 8f, newTextPaint(boxColor, font))
            }
            if (state.on("esp_distance")) {
                drawText("${target.dist}m", center.x, topLeft.y + h + font, newTextPaint(boxColor, font, false))
            }
            if (state.on("esp_weapon")) {
                drawText(target.weapon, center.x, topLeft.y + h + font * 2f, newTextPaint(boxColor, font * 0.9f, false))
            }
            if (state.on("esp_target_info") && !target.ally) {
                drawText(
                    "HP ${target.hp} | AR ${target.armor}",
                    center.x, topLeft.y - 8f - font,
                    newTextPaint(Color(0xFFFFD166), font * 0.9f, false)
                )
            }
            if (state.on("esp_visibility")) {
                drawCircle(
                    if (target.ally) Color(0xFF00FF9D) else Color(0xFFFF2E63),
                    3f, topLeft + Offset(-12f, -6f)
                )
            }
        }

        if (state.on("esp_rgb")) {
            drawRect(
                hueColor((t * 120f) % 360f, alpha = 0.6f),
                Offset.Zero,
                size,
                style = Stroke(3f)
            )
        }
        if (state.on("esp_radar") || state.on("esp_minimap")) {
            val r = 34f
            val origin = Offset(size.width - r - 14f, r + 14f)
            drawCircle(Color(0xCC070910), r, origin)
            drawCircle(hueColor(baseHue, alpha = 0.7f), r, origin, style = Stroke(1.5f))
            targets.forEach { tg ->
                drawCircle(
                    if (tg.ally) Color(0xFF00FF9D) else Color(0xFFFF2E63),
                    3f,
                    origin + Offset((tg.nx - 0.5f) * 44f, (tg.ny - 0.5f) * 44f)
                )
            }
        }
    }
}
