package com.vzyn.ffpanel.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vzyn.ffpanel.ui.theme.DeepBlack
import com.vzyn.ffpanel.ui.theme.NeonBlue
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onDone: () -> Unit) {
    val transition = rememberInfiniteTransition(label = "splash")
    val spin by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(1600)),
        label = "spin"
    )
    val glow by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900), repeatMode = RepeatMode.Reverse),
        label = "glow"
    )

    LaunchedEffect(Unit) {
        delay(1700)
        onDone()
    }

    Box(Modifier.fillMaxSize().background(DeepBlack), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.Center) {
                Canvas(Modifier.size(160.dp)) {
                    rotate(spin) {
                        drawArc(
                            color = NeonBlue.copy(alpha = glow),
                            startAngle = 0f,
                            sweepAngle = 120f,
                            useCenter = false,
                            style = Stroke(3f),
                            topLeft = Offset(10f, 10f),
                            size = Size(size.width - 20f, size.height - 20f)
                        )
                        drawArc(
                            color = Color(0xFF8A2BE2).copy(alpha = glow),
                            startAngle = 180f,
                            sweepAngle = 90f,
                            useCenter = false,
                            style = Stroke(3f),
                            topLeft = Offset(28f, 28f),
                            size = Size(size.width - 56f, size.height - 56f)
                        )
                    }
                }
                Text("VZYN", fontSize = 34.sp, fontWeight = FontWeight.Black, color = Color.White)
            }
            Spacer(Modifier.height(26.dp))
            Text(
                "ULTIMATE VISUAL EDITION",
                fontSize = 11.sp,
                letterSpacing = 4.sp,
                color = NeonBlue,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text("V10", fontSize = 12.sp, color = Color(0xFF8C97B5))
        }
    }
}
