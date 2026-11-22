package com.vzyn.ffpanel.ui.theme

import androidx.compose.ui.graphics.Color

val DeepBlack = Color(0xFF080A12)
val PureBlack = Color(0xFF000000)
val NeonBlue = Color(0xFF00BFFF)
val ElectricPurple = Color(0xFF8A2BE2)
val SurfaceDark = Color(0xFF11141F)
val SurfaceDarkAlt = Color(0xFF161A28)
val TextPrimary = Color(0xFFEAF2FF)
val TextMuted = Color(0xFF8C97B5)

fun hueColor(hue: Float, sat: Float = 0.9f, value: Float = 1f, alpha: Float = 1f): Color {
    val hsv = floatArrayOf(((hue % 360f) + 360f) % 360f, sat.coerceIn(0f, 1f), value.coerceIn(0f, 1f))
    return Color(android.graphics.Color.HSVToColor((alpha.coerceIn(0f, 1f) * 255).toInt(), hsv))
}
