package com.vzyn.ffpanel.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.vzyn.ffpanel.core.Catalog
import com.vzyn.ffpanel.core.Spec
import com.vzyn.ffpanel.core.tr
import com.vzyn.ffpanel.sim.AimCanvas
import com.vzyn.ffpanel.sim.CrosshairCanvas
import com.vzyn.ffpanel.sim.EspCanvas
import com.vzyn.ffpanel.sim.FovCanvas
import com.vzyn.ffpanel.sim.RadarCanvas
import com.vzyn.ffpanel.sim.RecoilCanvas
import com.vzyn.ffpanel.ui.components.ControlRow
import com.vzyn.ffpanel.ui.components.PreviewFrame
import com.vzyn.ffpanel.ui.components.ScreenHeader
import com.vzyn.ffpanel.ui.components.SearchField
import com.vzyn.ffpanel.ui.theme.hueColor
import com.vzyn.ffpanel.vm.SettingsViewModel

@Composable
fun CategoryScreen(route: String, vm: SettingsViewModel, nav: NavHostController) {
    val state by vm.state.collectAsStateWithLifecycle()
    val group = Catalog.group(route) ?: return
    val isEn = state.isEn
    val hue = state.hue("app_accent")
    var query by remember { mutableStateOf("") }
    var fps by remember { mutableStateOf(0) }

    val results: List<Spec> = remember(query, state.lang) { Catalog.search(query, state.lang) }

    LazyColumn(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        item {
            ScreenHeader(
                title = tr(isEn, group.titlePt, group.titleEn),
                subtitle = "${group.specs.size} ${tr(isEn, "controles funcionais", "functional controls")}",
                hue = hue,
                nav = nav,
                trailing = {
                    TextButton(onClick = { vm.resetGroup(route) }) {
                        Text(tr(isEn, "Resetar", "Reset"), style = MaterialTheme.typography.labelSmall)
                    }
                }
            )
        }

        item {
            Column(Modifier.padding(horizontal = 14.dp)) {
                SearchField(query, isEn) { query = it }
                Spacer(Modifier.height(12.dp))
            }
        }

        if (query.isBlank()) {
            item {
                Column(Modifier.padding(horizontal = 14.dp)) {
                    PreviewFrame(
                        hue = hue,
                        title = tr(isEn, "Pré-visualização", "Live preview"),
                        footer = "$fps fps"
                    ) {
                        when (route) {
                            "aim" -> AimCanvas(state, Modifier.fillMaxSize()) { fps = it }
                            "fov" -> FovCanvas(state, Modifier.fillMaxSize())
                            "esp" -> EspCanvas(state, Modifier.fillMaxSize())
                            "recoil" -> RecoilCanvas(state, Modifier.fillMaxSize())
                            "cross" -> CrosshairCanvas(state, Modifier.fillMaxSize())
                            "radar" -> RadarCanvas(state, Modifier.fillMaxSize())
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                }
            }
            items(group.specs, key = { it.key }) { spec ->
                Column(Modifier.padding(horizontal = 14.dp)) {
                    ControlRow(
                        spec = spec,
                        state = state,
                        compact = state.on("app_compact"),
                        onChange = { vm.set(spec.key, it) },
                        onAction = { handleAction(spec.key, route, vm, nav) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                }
            }
        } else {
            if (results.isEmpty()) {
                item {
                    Text(
                        tr(isEn, "Nenhum recurso encontrado.", "No feature found."),
                        Modifier.padding(18.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            items(results, key = { it.key }) { spec ->
                Column(Modifier.padding(horizontal = 14.dp).clip(RoundedCornerShape(12.dp))) {
                    ControlRow(
                        spec = spec,
                        state = state,
                        compact = state.on("app_compact"),
                        onChange = { vm.set(spec.key, it) },
                        onAction = { handleAction(spec.key, spec.group, vm, nav) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                }
            }
        }

        item {
            Spacer(Modifier.height(30.dp))
            Text(
                tr(
                    isEn,
                    "Simulação local — não altera o jogo.",
                    "Local simulation — does not modify the game."
                ),
                Modifier.fillMaxWidth().padding(14.dp),
                style = MaterialTheme.typography.labelSmall,
                color = hueColor(hue)
            )
        }
    }
}

private fun handleAction(key: String, group: String, vm: SettingsViewModel, nav: NavHostController) {
    when (key) {
        "cross_reset" -> vm.resetGroup("cross")
        "restore_defaults" -> vm.resetAll()
        "open_profiles", "export_json", "import_json" -> nav.navigate("profiles")
        else -> vm.resetGroup(group)
    }
}
