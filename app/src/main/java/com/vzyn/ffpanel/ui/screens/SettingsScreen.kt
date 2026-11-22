package com.vzyn.ffpanel.ui.screens

import android.os.Build
import android.provider.Settings as ASettings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.vzyn.ffpanel.BuildConfig
import com.vzyn.ffpanel.core.Catalog
import com.vzyn.ffpanel.core.tr
import com.vzyn.ffpanel.ui.components.ControlRow
import com.vzyn.ffpanel.ui.components.ScreenHeader
import com.vzyn.ffpanel.ui.theme.hueColor
import com.vzyn.ffpanel.vm.SettingsViewModel

@Composable
fun SettingsScreen(vm: SettingsViewModel, nav: NavHostController) {
    val state by vm.state.collectAsStateWithLifecycle()
    val isEn = state.isEn
    val hue = state.hue("app_accent")
    val context = LocalContext.current
    val group = Catalog.group(Catalog.G_SET) ?: return

    val overlayOk = ASettings.canDrawOverlays(context)
    val notifOk = NotificationManagerCompat.from(context).areNotificationsEnabled()

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        ScreenHeader(
            title = tr(isEn, "Configurações", "Settings"),
            subtitle = tr(isEn, "Aparência, idioma e perfis", "Appearance, language and profiles"),
            hue = hue,
            nav = nav
        )

        Column(Modifier.padding(horizontal = 14.dp)) {
            Text(
                tr(isEn, "Diagnóstico de permissões", "Permission diagnostic"),
                style = MaterialTheme.typography.labelSmall,
                color = hueColor(hue)
            )
            Spacer(Modifier.height(6.dp))
            DiagnosticLine(tr(isEn, "Sobreposição", "Overlay"), overlayOk)
            DiagnosticLine(tr(isEn, "Notificações", "Notifications"), notifOk)
            Spacer(Modifier.height(14.dp))

            group.specs.forEach { spec ->
                ControlRow(
                    spec = spec,
                    state = state,
                    compact = state.on("app_compact"),
                    onChange = { vm.set(spec.key, it) },
                    onAction = {
                        when (spec.key) {
                            "restore_defaults" -> vm.resetAll()
                            "open_profiles", "export_json", "import_json" -> nav.navigate("profiles")
                        }
                    }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            }

            Spacer(Modifier.height(16.dp))
            Text("VZYN FF PANEL V10 · ${BuildConfig.VERSION_NAME}", style = MaterialTheme.typography.bodyMedium)
            Text(
                tr(
                    isEn,
                    "Sem root · Android ${Build.VERSION.RELEASE} · sem injeção de código",
                    "No root · Android ${Build.VERSION.RELEASE} · no code injection"
                ),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "https://venice.ai",
                Modifier.fillMaxWidth().padding(vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall,
                color = hueColor(hue)
            )
            Spacer(Modifier.height(30.dp))
        }
    }
}

@Composable
private fun DiagnosticLine(label: String, ok: Boolean) {
    Text(
        "${if (ok) "OK" else "PENDENTE"} · $label",
        style = MaterialTheme.typography.bodyMedium,
        color = if (ok) hueColor(120f) else hueColor(0f)
    )
}
