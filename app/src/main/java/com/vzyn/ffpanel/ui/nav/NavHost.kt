package com.vzyn.ffpanel.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vzyn.ffpanel.ui.screens.CategoryScreen
import com.vzyn.ffpanel.ui.screens.DashboardScreen
import com.vzyn.ffpanel.ui.screens.OverlayScreen
import com.vzyn.ffpanel.ui.screens.PerformanceScreen
import com.vzyn.ffpanel.ui.screens.ProfilesScreen
import com.vzyn.ffpanel.ui.screens.SettingsScreen
import com.vzyn.ffpanel.ui.screens.SplashScreen
import com.vzyn.ffpanel.vm.SettingsViewModel

@Composable
fun VzynNavHost(vm: SettingsViewModel) {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = "splash") {
        composable("splash") {
            SplashScreen { nav.navigate("dashboard") { popUpTo("splash") { inclusive = true } } }
        }
        composable("dashboard") { DashboardScreen(vm, nav) }
        listOf("aim", "fov", "esp", "recoil", "cross", "radar").forEach { route ->
            composable(route) { CategoryScreen(route, vm, nav) }
        }
        composable("perf") { PerformanceScreen(vm, nav) }
        composable("overlay") { OverlayScreen(vm, nav) }
        composable("settings") { SettingsScreen(vm, nav) }
        composable("profiles") { ProfilesScreen(vm, nav) }
    }
}
