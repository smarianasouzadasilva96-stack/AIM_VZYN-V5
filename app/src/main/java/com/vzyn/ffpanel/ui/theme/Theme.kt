package com.vzyn.ffpanel.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun VzynTheme(
    theme: Int,
    accentHue: Float,
    content: @Composable () -> Unit
) {
    val accent = hueColor(accentHue)
    val secondary = hueColor(accentHue + 60f)

    val scheme = when (theme) {
        1 -> lightColorScheme(
            primary = accent,
            secondary = secondary,
            background = Color(0xFFF3F6FC),
            surface = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFFE3E9F5),
            onPrimary = Color(0xFF04121C),
            onBackground = Color(0xFF0A1220),
            onSurface = Color(0xFF0A1220),
            onSurfaceVariant = Color(0xFF41506B),
            outline = accent
        )
        2 -> darkColorScheme(
            primary = accent,
            secondary = secondary,
            background = PureBlack,
            surface = Color(0xFF050505),
            surfaceVariant = Color(0xFF0D0D0D),
            onPrimary = Color(0xFF04121C),
            onBackground = TextPrimary,
            onSurface = TextPrimary,
            onSurfaceVariant = TextMuted,
            outline = accent
        )
        else -> darkColorScheme(
            primary = accent,
            secondary = secondary,
            background = DeepBlack,
            surface = SurfaceDark,
            surfaceVariant = SurfaceDarkAlt,
            onPrimary = Color(0xFF04121C),
            onBackground = TextPrimary,
            onSurface = TextPrimary,
            onSurfaceVariant = TextMuted,
            outline = accent
        )
    }

    MaterialTheme(colorScheme = scheme, typography = VzynTypography, content = content)
}
