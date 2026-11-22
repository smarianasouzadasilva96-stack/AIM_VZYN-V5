package com.vzyn.ffpanel.sim

import android.graphics.Paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb

fun DrawScope.drawText(text: String, x: Float, y: Float, paint: Paint) {
    drawIntoCanvas { it.nativeCanvas.drawText(text, x, y, paint) }
}

fun newTextPaint(color: Color, sizePx: Float, bold: Boolean = true): Paint =
    Paint().apply {
        isAntiAlias = true
        this.color = color.toArgb()
        textSize = sizePx
        typeface = if (bold) android.graphics.Typeface.DEFAULT_BOLD
        else android.graphics.Typeface.DEFAULT
        textAlign = Paint.Align.CENTER
    }

fun DrawScope.backdrop() {
    drawRect(Color(0xFF070910))
    val grid = Color(0xFF00BFFF).copy(alpha = 0.06f)
    var x = 0f
    while (x < size.width) {
        drawLine(grid, Offset(x, 0f), Offset(x, size.height), 1f)
        x += size.width / 12f
    }
    var y = 0f
    while (y < size.height) {
        drawLine(grid, Offset(0f, y), Offset(size.width, y), 1f)
        y += size.height / 7f
    }
}
