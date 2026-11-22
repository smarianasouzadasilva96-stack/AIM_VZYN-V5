package com.vzyn.ffpanel.ui.screens

import android.os.Build
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.vzyn.ffpanel.core.tr
import com.vzyn.ffpanel.perf.rememberFpsMeter
import com.vzyn.ffpanel.ui.components.ScreenHeader
import com.vzyn.ffpanel.ui.theme.hueColor
import com.vzyn.ffpanel.vm.PerfViewModel
import com.vzyn.ffpanel.vm.SettingsViewModel

@Composable
fun PerformanceScreen(vm: SettingsViewModel, nav: NavHostController) {
    val state by vm.state.collectAsStateWithLifecycle()
    val isEn = state.isEn
    val hue = state.hue("app_accent")
    val context = LocalContext.current
    val perfVm: PerfViewModel = viewModel(
        factory = PerfViewModel.factory(context.applicationContext as android.app.Application)
    )
    val snap by perfVm.snapshot.collectAsStateWithLifecycle()
    val history by perfVm.history.collectAsStateWithLifecycle()
    val fps by rememberFpsMeter()

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        ScreenHeader(
            title = tr(isEn, "Desempenho", "Performance"),
            subtitle = tr(isEn, "Medições reais do dispositivo", "Real device measurements"),
            hue = hue,
            nav = nav
        )

        Column(Modifier.padding(14.dp)) {
            Metric(
                tr(isEn, "RAM em uso", "RAM in use"),
                "${snap?.ramUsedMb ?: 0} / ${snap?.ramTotalMb ?: 0} MB",
                ((snap?.ramUsedMb ?: 0).toFloat() / (snap?.ramTotalMb ?: 1).coerceAtLeast(1)).coerceIn(0f, 1f),
                hue
            )
            Metric(
                tr(isEn, "Bateria", "Battery"),
                "${snap?.batteryPercent ?: -1}% · ${snap?.batteryTempC ?: 0f}°C",
                ((snap?.batteryPercent ?: 0).toFloat() / 100f).coerceIn(0f, 1f),
                120f
            )
            Metric(
                tr(isEn, "Armazenamento", "Storage"),
                "${snap?.storageUsedMb ?: 0} / ${snap?.storageTotalMb ?: 0} MB",
                ((snap?.storageUsedMb ?: 0).toFloat() / (snap?.storageTotalMb ?: 1).coerceAtLeast(1)).coerceIn(0f, 1f),
                45f
            )
            Metric(
                tr(isEn, "CPU do aplicativo", "Application CPU"),
                "${"%.1f".format(snap?.cpuPercent ?: 0f)}%",
                ((snap?.cpuPercent ?: 0f) / 100f).coerceIn(0f, 1f),
                280f
            )
            Metric(tr(isEn, "Heap do app", "App heap"), "${snap?.appHeapMb ?: 0} MB", 0.4f, 195f)
            Metric(tr(isEn, "FPS da pré-visualização", "Preview FPS"), "$fps", (fps / 60f).coerceIn(0f, 1f), 150f)
            Metric(
                tr(isEn, "Rede (leitura)", "Network (read)"),
                "${"%.2f".format(snap?.networkMbps ?: 0f)} Mbps",
                0.3f, 320f
            )

            Spacer(Modifier.height(14.dp))
            Text(
                tr(isEn, "Histórico de CPU do app", "App CPU history"),
                style = MaterialTheme.typography.labelSmall,
                color = hueColor(hue)
            )
            Spacer(Modifier.height(6.dp))
            Canvas(
                Modifier
                    .fillMaxWidth()
                    .height(80.dp)                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                if (history.isEmpty()) return@Canvas
                val step = size.width / (history.size.coerceAtLeast(2) - 1)
                var prev = Offset(0f, size.height)
                history.forEachIndexed { i, value ->
                    val point = Offset(i * step, size.height - (value / 100f) * size.height)
                    drawLine(hueColor(hue), prev, point, 2f)
                    prev = point
                }
            }

            Spacer(Modifier.height(14.dp))
            Text(
                tr(
                    isEn,
                    "Diagnóstico de conectividade: ${if ((snap?.networkMbps ?: 0f) > 0f) "tráfego ativo" else "sem tráfego"}",
                    "Connectivity diagnostic: ${if ((snap?.networkMbps ?: 0f) > 0f) "traffic active" else "no traffic"}"
                ),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                "Android ${Build.VERSION.RELEASE} · API ${Build.VERSION.SDK_INT}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(30.dp))
        }
    }
}

@Composable
private fun Metric(label: String, value: String, progress: Float, hue: Float) {
    Column(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(value, style = MaterialTheme.typography.labelSmall, color = hueColor(hue))
        }
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(4.dp)),
            color = hueColor(hue),
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}
