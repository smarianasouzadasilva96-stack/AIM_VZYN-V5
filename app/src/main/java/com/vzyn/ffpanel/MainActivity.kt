package com.vzyn.ffpanel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vzyn.ffpanel.ui.nav.VzynNavHost
import com.vzyn.ffpanel.ui.theme.VzynTheme
import com.vzyn.ffpanel.vm.SettingsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repo = (application as VzynApp).repo
        setContent {
            var authorized by remember { mutableStateOf(false) }
            var enteredKey by remember { mutableStateOf("") }
            if (!authorized) {
                androidx.compose.material3.MaterialTheme {
                    androidx.compose.foundation.layout.Column(
                        modifier = androidx.compose.ui.Modifier.fillMaxSize().padding(24.dp),
                        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        androidx.compose.material3.Text("VZYN FF PANEL V10 — ACESSO")
                        androidx.compose.foundation.layout.Spacer(androidx.compose.ui.Modifier.height(16.dp))
                        androidx.compose.material3.OutlinedTextField(
                            value = enteredKey,
                            onValueChange = { enteredKey = it },
                            label = { androidx.compose.material3.Text("Key de acesso") },
                            singleLine = true
                        )
                        androidx.compose.foundation.layout.Spacer(androidx.compose.ui.Modifier.height(12.dp))
                        androidx.compose.material3.Button(onClick = { authorized = enteredKey == "vx09" }) {
                            androidx.compose.material3.Text("Entrar")
                        }
                    }
                }
            } else {
            val vm: SettingsViewModel = viewModel(factory = SettingsViewModel.factory(repo))
            val state by vm.state.collectAsStateWithLifecycle()
            VzynTheme(
                theme = state.idx("app_theme"),
                accentHue = state.hue("app_accent")
            ) {
                VzynNavHost(vm)
            }
            }
        }
    }
}
