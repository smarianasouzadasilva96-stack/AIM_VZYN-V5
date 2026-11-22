package com.vzyn.ffpanel.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.Settings as ASettings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.vzyn.ffpanel.core.Catalog
import com.vzyn.ffpanel.core.tr
import com.vzyn.ffpanel.overlay.OverlayService
import com.vzyn.ffpanel.ui.components.ControlRow
import com.vzyn.ffpanel.ui.components.ScreenHeader
import com.vzyn.ffpanel.ui.theme.hueColor
import com.vzyn.ffpanel.vm.SettingsViewModel

@Composable
fun OverlayScreen(vm: SettingsViewModel, nav: NavHostController) {
    val state by vm.state.collectAsStateWithLifecycle()
    val isEn = state.isEn
    val hue = state.hue("app_accent")
    val context = LocalContext.current
    var granted by remember { mutableStateOf(ASettings.canDrawOverlays(context)) }
    var running by remember { mutableStateOf(OverlayService.isRunning) }
    val group = Catalog.group(Catalog.G_SET) ?: return
    val overlaySpecs = group.specs.filter { it.key.startsWith("bubble") || it.key.startsWith("overlay") }

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        ScreenHeader(
            title = tr(isEn, "Sobreposição", "Overlay"),
            subtitle = tr(isEn, "Bolinha flutuante e crosshair", "Floating bubble and crosshair"),
            hue = hue,
            nav = nav
        )

        Column(Modifier.padding(horizontal = 14.dp)) {
            Text(
                tr(
                    isEn,
                    "A permissão é solicitada apenas quando você ativa o recurso.",
                    "Permission is requested only when you enable the feature."
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = {
                        context.startActivity(
                            Intent(
                                ASettings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:${context.packageName}")
                            )
                        )
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(tr(isEn, "Permissão", "Permission"), style = MaterialTheme.typography.labelSmall)
                }

                Button(
                    onClick = {
                        granted = ASettings.canDrawOverlays(context)
                        if (!granted) return@Button
                        if (running) {
                            context.stopService(Intent(context, OverlayService::class.java))
                            running = false
                        } else {
                            ContextCompat.startForegroundService(
                                context, Intent(context, OverlayService::class.java)
                            )
                            running = true
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (running) hueColor(0f) else hueColor(hue)
                    )
                ) {
                    Text(
                        if (running) tr(isEn, "Encerrar", "Stop") else tr(isEn, "Iniciar", "Start"),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Spacer(Modifier.height(10.dp))
            Text(
                if (granted) tr(isEn, "Permissão concedida", "Permission granted")
                else tr(isEn, "Permissão pendente", "Permission pending"),
                style = MaterialTheme.typography.labelSmall,
                color = if (granted) hueColor(120f) else hueColor(0f)
            )

            Spacer(Modifier.height(18.dp))
            Text(
                tr(isEn, "Ajustes da bolinha", "Bubble settings"),
                style = MaterialTheme.typography.labelSmall,
                color = hueColor(hue)
            )

            overlaySpecs.forEach { spec ->
                ControlRow(
                    spec = spec,
                    state = state,
                    compact = state.on("app_compact"),
                    onChange = { vm.set(spec.key, it) },
                    onAction = { vm.resetGroup(Catalog.G_SET) }
                )
            }

            Spacer(Modifier.height(24.dp))
            Text(
                tr(
                    isEn,
                    "O serviço usa TYPE_APPLICATION_OVERLAY, é encerrado com o app e não injeta código no Free Fire.",
                    "The service uses TYPE_APPLICATION_OVERLAY, shuts down with the app and does not inject code into Free Fire."
                ),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(30.dp))
        }
    }
}
