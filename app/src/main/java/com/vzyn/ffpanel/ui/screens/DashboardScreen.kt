package com.vzyn.ffpanel.ui.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings as ASettings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.vzyn.ffpanel.BuildConfig
import com.vzyn.ffpanel.core.Catalog
import com.vzyn.ffpanel.core.tr
import com.vzyn.ffpanel.overlay.OverlayService
import com.vzyn.ffpanel.perf.PerfSampler
import com.vzyn.ffpanel.ui.components.VzynBadge
import com.vzyn.ffpanel.ui.theme.hueColor
import com.vzyn.ffpanel.vm.PerfViewModel
import com.vzyn.ffpanel.vm.SettingsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(vm: SettingsViewModel, nav: NavHostController) {
    val state by vm.state.collectAsStateWithLifecycle()
    val isEn = state.isEn
    val hue = state.hue("app_accent")
    val context = LocalContext.current
    val perfVm: PerfViewModel = viewModel(
        factory = PerfViewModel.factory(context.applicationContext as android.app.Application)
    )
    val perf by perfVm.snapshot.collectAsStateWithLifecycle()
    val drawer = rememberDrawerState(DrawerValue.Closed)
    val scope = androidx.compose.runtime.rememberCoroutineScope()

    val lifecycleOwner = LocalLifecycleOwner.current
    val overlayGranted by produceState(
        initialValue = ASettings.canDrawOverlays(context),
        lifecycleOwner
    ) {
        while (true) {
            value = ASettings.canDrawOverlays(context)
            delay(1200)
        }
    }

    val notifLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    val pulse by rememberInfiniteTransition(label = "logo").animateFloat(
        0.6f, 1f,
        infiniteRepeatable(tween(1400), repeatMode = RepeatMode.Reverse),
        label = "pulse"
    )

    ModalNavigationDrawer(
        drawerState = drawer,
        drawerContent = {
            ModalDrawerSheet {
                Column(Modifier.padding(20.dp)) {
                    Text("VZYN", fontSize = 26.sp, fontWeight = FontWeight.Black, color = hueColor(hue))
                    Text(
                        "V10 · ULTIMATE VISUAL",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(20.dp))
                    Catalog.groups.forEach { group ->
                        Text(
                            tr(isEn, group.titlePt, group.titleEn),
                            Modifier
                                .fillMaxWidth()
                                .clickable {
                                    scope.launch { drawer.close() }
                                    nav.navigate(group.id)
                                }
                                .padding(vertical = 11.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        tr(isEn, "Desempenho", "Performance"),
                        Modifier.fillMaxWidth().clickable {
                            scope.launch { drawer.close() }
                            nav.navigate("perf")
                        }.padding(vertical = 11.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        tr(isEn, "Sobreposição", "Overlay"),
                        Modifier.fillMaxWidth().clickable {
                            scope.launch { drawer.close() }
                            nav.navigate("overlay")
                        }.padding(vertical = 11.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        tr(isEn, "Perfis", "Profiles"),
                        Modifier.fillMaxWidth().clickable {
                            scope.launch { drawer.close() }
                            nav.navigate("profiles")
                        }.padding(vertical = 11.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    ) {
        LazyColumn(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            item {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { scope.launch { drawer.open() } }) {
                        Icon(Icons.Default.Menu, contentDescription = null)
                    }
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = { nav.navigate("settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = null)
                    }
                }
            }

            item {
                Column(
                    Modifier.fillMaxWidth().padding(horizontal = 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "VZYN",
                        fontSize = 46.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 8.sp,
                        color = hueColor(hue, value = pulse)
                    )
                    Text(
                        "FF PANEL · ULTIMATE VISUAL EDITION",
                        fontSize = 10.sp,
                        letterSpacing = 2.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        VzynBadge("v${BuildConfig.VERSION_NAME}", hue)
                        VzynBadge(if (isEn) "NO ROOT" else "SEM ROOT", 120f)
                        VzynBadge("ANDROID 10–15", 280f)
                    }
                }
            }

            item {
                Column(Modifier.padding(18.dp)) {
                    SectionCard(tr(isEn, "Status do sistema", "System status"), hue) {
                        StatusLine(tr(isEn, "Permissão de sobreposição", "Overlay permission"), overlayGranted)
                        StatusLine(tr(isEn, "Serviço flutuante", "Floating service"), OverlayService.isRunning)
                        StatusLine(tr(isEn, "Perfil ativo", "Active profile"), true, state.active.name)
                    }

                    Spacer(Modifier.height(12.dp))

                    SectionCard(tr(isEn, "Desempenho real", "Live performance"), hue) {
                        StatusLine(tr(isEn, "RAM usada", "RAM used"), true, "${perf?.ramUsedMb ?: 0} MB")
                        StatusLine(
                            tr(isEn, "Bateria", "Battery"), true,
                            "${perf?.batteryPercent ?: -1}% · ${perf?.batteryTempC ?: 0f}°C"
                        )
                        StatusLine(
                            tr(isEn, "CPU do app", "App CPU"), true,
                            "${"%.1f".format(perf?.cpuPercent ?: 0f)}%"
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    SectionCard(tr(isEn, "Dispositivo", "Device"), hue) {
                        Text(
                            PerfSampler(context).deviceSummary(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ActionButton(
                            tr(isEn, "Abrir Free Fire", "Open Free Fire"),
                            hue,
                            Modifier.weight(1f)
                        ) {
                            val intent = listOf("com.dts.freefireth", "com.dts.freefiremax")
                                .asSequence()
                                .mapNotNull { context.packageManager.getLaunchIntentForPackage(it) }
                                .firstOrNull()
                                ?: Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("market://details?id=com.dts.freefireth")
                                )
                            runCatching {
                                context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                            }
                        }
                        ActionButton(tr(isEn, "Sobreposição", "Overlay"), hue, Modifier.weight(1f)) {
                            nav.navigate("overlay")
                        }
                    }

                    Spacer(Modifier.height(18.dp))
                    Text(
                        tr(isEn, "CATEGORIAS", "CATEGORIES"),
                        style = MaterialTheme.typography.labelSmall,
                        color = hueColor(hue)
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }

            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(430.dp)
                        .padding(horizontal = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(Catalog.groups) { group ->
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .clickable { nav.navigate(group.id) }
                                .padding(14.dp)
                        ) {
                            Column {
                                Icon(
                                    Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = hueColor(hue),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    tr(isEn, group.titlePt, group.titleEn),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    "${group.specs.size} ${tr(isEn, "opções", "options")}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            item {
                Column(Modifier.padding(18.dp)) {
                    SectionCard(tr(isEn, "Novidades", "What's new"), hue) {
                        NewsLine("10.0", tr(isEn, "Editor de FOV com 20 parâmetros e prévia ao vivo.", "FOV editor with 20 live parameters."))
                        NewsLine("10.0", tr(isEn, "Perfis ilimitados com exportação JSON.", "Unlimited profiles with JSON export."))
                        NewsLine("10.0", tr(isEn, "Métricas reais de RAM, bateria, CPU e rede.", "Real RAM, battery, CPU and network metrics."))
                    }
                    Spacer(Modifier.height(40.dp))
                }
            }
        }
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && state.on("app_notifications")) {
        LaunchedEffect(Unit) { notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) }
    }
}

@Composable
private fun SectionCard(title: String, hue: Float, content: @Composable () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(14.dp)
    ) {
        Text(
            title.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = hueColor(hue),
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        content()
    }
}

@Composable
private fun StatusLine(label: String, ok: Boolean, value: String? = null) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(50))
                .background(if (ok) Color(0xFF00FF9D) else Color(0xFFFF2E63))
        )
        Spacer(Modifier.size(8.dp))
        Text(label, Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
        if (value != null) {
            Text(
                value,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun NewsLine(tag: String, text: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        VzynBadge(tag, 280f)
        Spacer(Modifier.size(8.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ActionButton(label: String, hue: Float, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(hueColor(hue, alpha = 0.9f), hueColor(hue + 45f, alpha = 0.75f))
                )
            )
            .clickable { onClick() }
            .padding(vertical = 13.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF04121C))
    }
}
