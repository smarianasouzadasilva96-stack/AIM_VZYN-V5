package com.vzyn.ffpanel.overlay

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View

data class CrossConfig(
    val enabled: Boolean,
    val dot: Boolean,
    val circle: Boolean,
    val cross: Boolean,
    val tShape: Boolean,
    val size: Float,
    val thickness: Float,
    val gap: Float,
    val opacity: Float,
    val hue: Float,
    val glow: Float,
    val rotation: Float,
    val position: Int
)

class CrosshairView(context: Context) : View(context) {

    var config = CrossConfig(false, false, false, false, false, 22f, 2f, 6f, 1f, 120f, 0.2f, 0f, 0)
        set(value) {
            field = value
            invalidate()
        }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }
    private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }

    override fun onDraw(canvas: Canvas) {
        val cfg = config
        if (!cfg.enabled) return

        val color = Color.HSVToColor(
            (cfg.opacity * 255).toInt().coerceIn(0, 255),
            floatArrayOf(((cfg.hue % 360f) + 360f) % 360f, 0.9f, 1f)
        )
        val cx = width / 2f
        val cy = when (cfg.position) {
            1 -> height * 0.25f
            2 -> height * 0.75f
            else -> height / 2f
        }

        paint.color = color
        paint.strokeWidth = cfg.thickness
        dotPaint.color = color
        glowPaint.color = color

        if (cfg.glow > 0f) {
            glowPaint.alpha = (cfg.opacity * cfg.glow * 30).toInt().coerceIn(0, 80)
            canvas.drawCircle(cx, cy, cfg.size * 1.4f, glowPaint)
        }

        val s = cfg.size
        val g = cfg.gap

        if (cfg.circle) canvas.drawCircle(cx, cy, s * 0.6f, paint)
        if (cfg.cross) {
            canvas.drawLine(cx - g - s, cy, cx - g, cy, paint)
            canvas.drawLine(cx + g, cy, cx + g + s, cy, paint)
            canvas.drawLine(cx, cy - g - s, cx, cy - g, paint)
            canvas.drawLine(cx, cy + g, cx, cy + g + s, paint)
        }
        if (cfg.tShape) {
            canvas.drawLine(cx - g - s, cy, cx - g, cy, paint)
            canvas.drawLine(cx + g, cy, cx + g + s, cy, paint)
            canvas.drawLine(cx, cy - g - s * 0.6f, cx, cy - g, paint)
        }
        if (cfg.dot) canvas.drawCircle(cx, cy, cfg.thickness * 1.2f + 0.8f, dotPaint)
    }
}
