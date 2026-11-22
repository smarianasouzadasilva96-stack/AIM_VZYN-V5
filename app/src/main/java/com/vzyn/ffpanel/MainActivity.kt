package com.vzyn.ffpanel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val ComponentActivity.store by preferencesDataStore(name = "vzyn_visual")
private val neon = Color(0xFF00BFFF)
private val purple = Color(0xFF8A2BE2)
private val bg = Color(0xFF080A12)

private data class Control(val label: String, val slider: Boolean = false)
private val sections = linkedMapOf(
    "AIMBOT" to "Aimbot Enable|Aimlock|Silent Aim|Legit Aim|Rage Aim|Smooth Aim|Aim Assist|Aim Prediction|Aim Tracking|Aim Stabilizer|Auto Headshot|Head Priority|Body Priority|Target Selector|Target Switch|Closest Target|Aim Bone Selector|Aim Strength|Aim Speed|Aim Smoothness|Aim Distance|Aim Accuracy|Aim Delay|Aim Deadzone|Aim Curve|Aim Acceleration|Aim Sensitivity X|Aim Sensitivity Y|Aim Offset X|Aim Offset Y",
    "AIM FOV" to "FOV Circle|FOV Square|FOV Cross|FOV Dynamic|FOV Static|FOV Rainbow|FOV Animated|FOV Filled|FOV Outline|FOV Gradient|FOV Radius|FOV Thickness|FOV Opacity|FOV Color|FOV Position X|FOV Position Y|FOV Pulse|FOV Rotation|FOV Glow|FOV Preview",
    "ESP" to "ESP Master|ESP Box 2D|ESP Box 3D|ESP Corner Box|ESP Skeleton|ESP Head Dot|ESP Head Circle|ESP Name|ESP Distance|ESP Health Bar|ESP Armor Bar|ESP Weapon|ESP Tracer|ESP Line|ESP Glow|ESP Chams|ESP Outline|ESP Filled Box|ESP Radar|ESP Mini Map|ESP Team Color|ESP Visibility|ESP Target Highlight|ESP Target Information|ESP RGB|ESP Rainbow|ESP Gradient|ESP Transparency|ESP Thickness|ESP Font Size|ESP Scale|ESP Max Distance|ESP Color Picker|ESP Skeleton Color|ESP Box Color|ESP Line Color|ESP Health Color|ESP Armor Color|ESP Preview|ESP Profiles",
    "RECOIL" to "No Recoil|Recoil Control|Recoil Vertical|Recoil Horizontal|Recoil Smooth|Recoil Stabilizer|Recoil Pattern|Recoil Preview|Bullet Spread|Spread Control|Rapid Fire|Auto Fire|Fire Rate|Auto Scope|Quick Scope|Weapon Stabilizer|Weapon Selector|Weapon Profiles|Accuracy Simulator|Shooting Range",
    "CROSSHAIR" to "Crosshair Enable|Crosshair Dot|Crosshair Circle|Crosshair Cross|Crosshair T-Shape|Crosshair Dynamic|Crosshair RGB|Crosshair Rainbow|Crosshair Glow|Crosshair Outline|Crosshair Size|Crosshair Thickness|Crosshair Gap|Crosshair Opacity|Crosshair Color|Crosshair Position|Crosshair Animation|Crosshair Presets|Crosshair Preview|Crosshair Reset",
    "RADAR" to "Radar 2D|Radar Circular|Radar Square|Radar RGB|Radar Zoom|Radar Rotation|Radar Grid|Radar Opacity|Radar Size|Radar Position|Radar Target Dots|Radar Team Colors|Radar Preview"
)
private val numericWords = listOf("Strength","Speed","Smoothness","Distance","Accuracy","Delay","Deadzone","Acceleration","Sensitivity","Offset","Radius","Thickness","Opacity","Color","Position","Pulse","Rotation","Glow","Transparency","Font Size","Scale","Zoom","Size","Gap","Vertical","Horizontal","Spread","Rate")
private fun controls(group: String) = sections[group].orEmpty().split("|").map {
    Control(it, numericWords.any { word -> it.contains(word, true) })
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var section by remember { mutableStateOf("AIMBOT") }
            var query by remember { mutableStateOf("") }
            val scope = rememberCoroutineScope()
            val prefs by store.data.collectAsState(initial = null)
            val values = prefs
            MaterialTheme(colorScheme = darkColorScheme(primary = neon, secondary = purple, background = bg, surface = Color(0xFF151827))) {
                Column(Modifier.fillMaxSize().background(bg).padding(12.dp)) {
                    Text("VZYN FF PANEL V10", style = MaterialTheme.typography.headlineSmall, color = neon)
                    Text("ULTIMATE VISUAL EDITION • DEMONSTRAÇÃO", color = Color.LightGray)
                    Spacer(Modifier.height(8.dp))
                    androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(sections.keys.toList()) { tab ->
                            FilterChip(selected = section == tab, onClick = { section = tab }, label = { Text(tab) })
                        }
                    }
                    OutlinedTextField(value = query, onValueChange = { query = it }, label = { Text("Buscar controle") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    Spacer(Modifier.height(8.dp))
                    DemoCanvas(section, values?.get(floatPreferencesKey("fov_radius")) ?: 50f)
                    Spacer(Modifier.height(8.dp))
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(controls(section).filter { it.label.contains(query, true) }) { control ->
                            val key = floatPreferencesKey(section + "_" + control.label)
                            val current = values?.get(key) ?: 0f
                            Surface(color = Color(0xFF151827), shape = RoundedCornerShape(12.dp)) {
                                Column(Modifier.fillMaxWidth().padding(12.dp)) {
                                    if (control.slider) {
                                        Text(control.label + "  " + current.toInt(), color = Color.White)
                                        Slider(value = current, onValueChange = { v -> scope.launch { store.edit { it[key] = v } } }, valueRange = 0f..100f)
                                    } else {
                                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                            Text(control.label, color = Color.White, modifier = Modifier.weight(1f))
                                            Switch(checked = current > 0.5f, onCheckedChange = { on -> scope.launch { store.edit { it[key] = if (on) 1f else 0f } } })
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DemoCanvas(section: String, radius: Float) {
    Surface(color = Color(0xFF111C2A), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().height(155.dp)) {
        Canvas(Modifier.fillMaxSize()) {
            val c = Offset(size.width / 2f, size.height / 2f)
            drawLine(neon, Offset(c.x - 18f, c.y), Offset(c.x + 18f, c.y), strokeWidth = 2f)
            drawLine(neon, Offset(c.x, c.y - 18f), Offset(c.x, c.y + 18f), strokeWidth = 2f)
            drawCircle(purple, radius = 25f + radius, center = c, style = Stroke(2f))
            if (section == "ESP" || section == "RADAR") {
                for (i in 0..2) {
                    val x = size.width * (i + 1) / 4f
                    drawRect(neon, Offset(x - 18f, c.y - 35f), androidx.compose.ui.geometry.Size(36f, 70f), style = Stroke(2f))
                }
            }
        }
    }
}
