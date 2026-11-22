package com.vzyn.ffpanel.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.vzyn.ffpanel.core.tr
import com.vzyn.ffpanel.ui.components.ScreenHeader
import com.vzyn.ffpanel.ui.theme.hueColor
import com.vzyn.ffpanel.vm.SettingsViewModel

@Composable
fun ProfilesScreen(vm: SettingsViewModel, nav: NavHostController) {
    val state by vm.state.collectAsStateWithLifecycle()
    val isEn = state.isEn
    val hue = state.hue("app_accent")
    val context = LocalContext.current

    var creating by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }
    var renaming by remember { mutableStateOf<String?>(null) }
    var renameValue by remember { mutableStateOf("") }
    var importResult by remember { mutableStateOf<String?>(null) }

    val createLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        runCatching {
            context.contentResolver.openOutputStream(uri)?.use {
                it.write(vm.export().toByteArray())
            }
        }
    }

    val openLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        val raw = runCatching {
            context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
        }.getOrNull()
        if (raw != null) {
            vm.import(raw).fold(
                onSuccess = { importResult = tr(isEn, "Importado com sucesso.", "Imported successfully.") },
                onFailure = { importResult = tr(isEn, "JSON inválido.", "Invalid JSON.") }
            )
        }
    }

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        ScreenHeader(
            title = tr(isEn, "Perfis", "Profiles"),
            subtitle = tr(isEn, "Configurações persistentes", "Persistent configurations"),
            hue = hue,
            nav = nav
        )

        Row(
            Modifier.fillMaxWidth().padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { creating = true },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(tr(isEn, "Novo", "New"), style = MaterialTheme.typography.labelSmall)
            }
            OutlinedButton(
                onClick = { createLauncher.launch("vzyn-perfis.json") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(tr(isEn, "Exportar", "Export"), style = MaterialTheme.typography.labelSmall)
            }
            OutlinedButton(
                onClick = { openLauncher.launch(arrayOf("application/json", "text/plain")) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(tr(isEn, "Importar", "Import"), style = MaterialTheme.typography.labelSmall)
            }
        }

        importResult?.let {
            Text(it, Modifier.padding(14.dp), style = MaterialTheme.typography.labelSmall, color = hueColor(120f))
        }

        LazyColumn(Modifier.padding(14.dp)) {
            items(state.profiles, key = { it.id }) { profile ->
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (profile.id == state.activeId) hueColor(hue, alpha = 0.16f)
                            else MaterialTheme.colorScheme.surface
                        )
                        .clickable { vm.activate(profile.id) }
                        .padding(13.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            profile.name,
                            Modifier.weight(1f),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (profile.id == state.activeId) {
                            Text(
                                tr(isEn, "ATIVO", "ACTIVE"),
                                style = MaterialTheme.typography.labelSmall,
                                color = hueColor(hue)
                            )
                        }
                    }
                    Text(
                        "${profile.values.size} ${tr(isEn, "valores salvos", "saved values")}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        TextButton(onClick = { renaming = profile.id; renameValue = profile.name }) {
                            Text(tr(isEn, "Renomear", "Rename"), style = MaterialTheme.typography.labelSmall)
                        }
                        TextButton(onClick = { vm.duplicate(profile.id) }) {
                            Text(tr(isEn, "Duplicar", "Duplicate"), style = MaterialTheme.typography.labelSmall)
                        }
                        TextButton(onClick = { vm.delete(profile.id) }) {
                            Text(tr(isEn, "Excluir", "Delete"), style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(16.dp))
                Text(
                    tr(isEn, "Histórico de alterações", "Change history"),
                    style = MaterialTheme.typography.labelSmall,
                    color = hueColor(hue)
                )
                Spacer(Modifier.height(6.dp))
                state.history.take(30).forEach { entry ->
                    Text(
                        "${entry.label}: ${entry.value}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.height(30.dp))
            }
        }
    }

    if (creating) {
        AlertDialog(
            onDismissRequest = { creating = false },
            title = { Text(tr(isEn, "Novo perfil", "New profile")) },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    singleLine = true,
                    label = { Text(tr(isEn, "Nome", "Name")) }
                )
            },
            confirmButton = {
                Button(onClick = { vm.create(newName); newName = ""; creating = false }) {
                    Text(tr(isEn, "Criar", "Create"))
                }
            },
            dismissButton = {
                TextButton(onClick = { creating = false }) { Text(tr(isEn, "Cancelar", "Cancel")) }
            }
        )
    }

    renaming?.let { id ->
        AlertDialog(
            onDismissRequest = { renaming = null },
            title = { Text(tr(isEn, "Renomear perfil", "Rename profile")) },
            text = {
                OutlinedTextField(
                    value = renameValue,
                    onValueChange = { renameValue = it },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(onClick = { vm.rename(id, renameValue); renaming = null }) {
                    Text(tr(isEn, "Salvar", "Save"))
                }
            },
            dismissButton = {
                TextButton(onClick = { renaming = null }) { Text(tr(isEn, "Cancelar", "Cancel")) }
            }
        )
    }
}
