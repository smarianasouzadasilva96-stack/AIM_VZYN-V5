package com.vzyn.ffpanel.perf

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos

@Composable
fun rememberFpsMeter(): State<Int> {
    val fps = remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        var frames = 0
        var windowStart = 0L
        while (true) {
            withFrameNanos { now ->
                if (windowStart == 0L) windowStart = now
                frames++
                if (now - windowStart >= 500_000_000L) {
                    fps.intValue = (frames * 1_000_000_000L / (now - windowStart)).toInt()
                    frames = 0
                    windowStart = now
                }
            }
        }
    }
    return fps
}
