package com.vzyn.ffpanel.core

object Catalog {
    const val G_AIM = "aim"
    const val G_FOV = "fov"
    const val G_ESP = "esp"
    const val G_RECOIL = "recoil"
    const val G_CROSS = "cross"
    const val G_RADAR = "radar"
    const val G_SET = "settings"

    private val aim = listOf(
        toggle("aim_enable", "Aimbot Enable", "Aimbot Enable", G_AIM),
        toggle("aim_lock", "Aimlock", "Aimlock", G_AIM),
        toggle("aim_silent", "Silent Aim", "Silent Aim", G_AIM),
        toggle("aim_legit", "Legit Aim", "Legit Aim", G_AIM),
        toggle("aim_rage", "Rage Aim", "Rage Aim", G_AIM),
        toggle("aim_smooth", "Smooth Aim", "Smooth Aim", G_AIM, true),
        toggle("aim_assist", "Aim Assist", "Aim Assist", G_AIM, true),
        toggle("aim_prediction", "Aim Prediction", "Aim Prediction", G_AIM),
        toggle("aim_tracking", "Aim Tracking", "Aim Tracking", G_AIM),
        toggle("aim_stabilizer", "Aim Stabilizer", "Aim Stabilizer", G_AIM),
        toggle("aim_auto_headshot", "Auto Headshot", "Auto Headshot", G_AIM),
        toggle("aim_head_priority", "Head Priority", "Head Priority", G_AIM),
        toggle("aim_body_priority", "Body Priority", "Body Priority", G_AIM),
        option("aim_target_selector", "Target Selector", "Target Selector", G_AIM,
            listOf("Mais próximo", "Menor vida", "Maior ameaça", "Ordem de tela")),
        option("aim_target_switch", "Target Switch", "Target Switch", G_AIM,
            listOf("Instantâneo", "Suave", "Manual")),
        toggle("aim_closest", "Closest Target", "Closest Target", G_AIM, true),
        option("aim_bone", "Aim Bone Selector", "Aim Bone Selector", G_AIM,
            listOf("Cabeça", "Pescoço", "Peito", "Estômago")),
        slider("aim_strength", "Aim Strength", "Aim Strength", G_AIM, 0f, 100f, 70f, "%"),
        slider("aim_speed", "Aim Speed", "Aim Speed", G_AIM, 1f, 100f, 45f, "%"),
        slider("aim_smoothness", "Aim Smoothness", "Aim Smoothness", G_AIM, 0f, 100f, 40f, "%"),
        slider("aim_distance", "Aim Distance", "Aim Distance", G_AIM, 10f, 300f, 120f, "m"),
        slider("aim_accuracy", "Aim Accuracy", "Aim Accuracy", G_AIM, 0f, 100f, 92f, "%"),
        slider("aim_delay", "Aim Delay", "Aim Delay", G_AIM, 0f, 500f, 40f, "ms"),
        slider("aim_deadzone", "Aim Deadzone", "Aim Deadzone", G_AIM, 0f, 100f, 6f, "%"),
        option("aim_curve", "Aim Curve", "Aim Curve", G_AIM,
            listOf("Linear", "Ease-In", "Ease-Out", "Sigmoide")),
        slider("aim_acceleration", "Aim Acceleration", "Aim Acceleration", G_AIM, 0f, 100f, 25f, "%"),
        slider("aim_sens_x", "Aim Sensitivity X", "Aim Sensitivity X", G_AIM, 0f, 200f, 100f, "%"),
        slider("aim_sens_y", "Aim Sensitivity Y", "Aim Sensitivity Y", G_AIM, 0f, 200f, 100f, "%"),
        slider("aim_offset_x", "Aim Offset X", "Aim Offset X", G_AIM, -100f, 100f, 0f, "px"),
        slider("aim_offset_y", "Aim Offset Y", "Aim Offset Y", G_AIM, -100f, 100f, 0f, "px")
    )

    private val fov = listOf(
        toggle("fov_circle", "FOV Circle", "FOV Circle", G_FOV, true),
        toggle("fov_square", "FOV Square", "FOV Square", G_FOV),
        toggle("fov_cross", "FOV Cross", "FOV Cross", G_FOV),
        toggle("fov_dynamic", "FOV Dynamic", "FOV Dynamic", G_FOV),
        toggle("fov_static", "FOV Static", "FOV Static", G_FOV, true),
        toggle("fov_rainbow", "FOV Rainbow", "FOV Rainbow", G_FOV),
        toggle("fov_animated", "FOV Animated", "FOV Animated", G_FOV),
        toggle("fov_filled", "FOV Filled", "FOV Filled", G_FOV),
        toggle("fov_outline", "FOV Outline", "FOV Outline", G_FOV, true),
        toggle("fov_gradient", "FOV Gradient", "FOV Gradient", G_FOV),
        slider("fov_radius", "FOV Radius", "FOV Radius", G_FOV, 40f, 400f, 150f, "px"),
        slider("fov_thickness", "FOV Thickness", "FOV Thickness", G_FOV, 1f, 12f, 3f, "px"),
        slider("fov_opacity", "FOV Opacity", "FOV Opacity", G_FOV, 10f, 100f, 85f, "%"),
        color("fov_color", "FOV Color", "FOV Color", G_FOV, 195f),
        slider("fov_pos_x", "FOV Position X", "FOV Position X", G_FOV, 0f, 100f, 50f, "%"),
        slider("fov_pos_y", "FOV Position Y", "FOV Position Y", G_FOV, 0f, 100f, 50f, "%"),
        slider("fov_pulse", "FOV Pulse", "FOV Pulse", G_FOV, 0f, 100f, 0f, "%"),
        slider("fov_rotation", "FOV Rotation", "FOV Rotation", G_FOV, 0f, 360f, 0f, "°"),
        slider("fov_glow", "FOV Glow", "FOV Glow", G_FOV, 0f, 100f, 30f, "%"),
        toggle("fov_preview", "FOV Preview", "FOV Preview", G_FOV, true)
    )

    private val esp = listOf(
        toggle("esp_master", "ESP Master", "ESP Master", G_ESP, true),
        toggle("esp_box2d", "ESP Box 2D", "ESP Box 2D", G_ESP, true),
        toggle("esp_box3d", "ESP Box 3D", "ESP Box 3D", G_ESP),
        toggle("esp_corner", "ESP Corner Box", "ESP Corner Box", G_ESP),
        toggle("esp_skeleton", "ESP Skeleton", "ESP Skeleton", G_ESP),
        toggle("esp_head_dot", "ESP Head Dot", "ESP Head Dot", G_ESP),
        toggle("esp_head_circle", "ESP Head Circle", "ESP Head Circle", G_ESP),
        toggle("esp_name", "ESP Name", "ESP Name", G_ESP),
        toggle("esp_distance", "ESP Distance", "ESP Distance", G_ESP, true),
        toggle("esp_health", "ESP Health Bar", "ESP Health Bar", G_ESP, true),
        toggle("esp_armor", "ESP Armor Bar", "ESP Armor Bar", G_ESP),
        toggle("esp_weapon", "ESP Weapon", "ESP Weapon", G_ESP),
        toggle("esp_tracer", "ESP Tracer", "ESP Tracer", G_ESP),
        toggle("esp_line", "ESP Line", "ESP Line", G_ESP),
        toggle("esp_glow", "ESP Glow", "ESP Glow", G_ESP),
        toggle("esp_chams", "ESP Chams", "ESP Chams", G_ESP),
        toggle("esp_outline", "ESP Outline", "ESP Outline", G_ESP),
        toggle("esp_filled", "ESP Filled Box", "ESP Filled Box", G_ESP),
        toggle("esp_radar", "ESP Radar", "ESP Radar", G_ESP),
        toggle("esp_minimap", "ESP Mini Map", "ESP Mini Map", G_ESP),
        toggle("esp_team_color", "ESP Team Color", "ESP Team Color", G_ESP),
        toggle("esp_visibility", "ESP Visibility", "ESP Visibility", G_ESP, true),
        toggle("esp_target_highlight", "ESP Target Highlight", "ESP Target Highlight", G_ESP),
        toggle("esp_target_info", "ESP Target Information", "ESP Target Information", G_ESP),
        toggle("esp_rgb", "ESP RGB", "ESP RGB", G_ESP),
        toggle("esp_rainbow", "ESP Rainbow", "ESP Rainbow", G_ESP),
        toggle("esp_gradient", "ESP Gradient", "ESP Gradient", G_ESP),
        slider("esp_transparency", "ESP Transparency", "ESP Transparency", G_ESP, 0f, 100f, 20f, "%"),
        slider("esp_thickness", "ESP Thickness", "ESP Thickness", G_ESP, 1f, 8f, 2f, "px"),
        slider("esp_font", "ESP Font Size", "ESP Font Size", G_ESP, 8f, 24f, 12f, "sp"),
        slider("esp_scale", "ESP Scale", "ESP Scale", G_ESP, 50f, 200f, 100f, "%"),
        slider("esp_max_distance", "ESP Max Distance", "ESP Max Distance", G_ESP, 10f, 400f, 200f, "m"),
        color("esp_color", "ESP Color Picker", "ESP Color Picker", G_ESP, 195f),
        color("esp_skel_color", "ESP Skeleton Color", "ESP Skeleton Color", G_ESP, 280f),
        color("esp_box_color", "ESP Box Color", "ESP Box Color", G_ESP, 195f),
        color("esp_line_color", "ESP Line Color", "ESP Line Color", G_ESP, 195f),
        color("esp_health_color", "ESP Health Color", "ESP Health Color", G_ESP, 120f),
        color("esp_armor_color", "ESP Armor Color", "ESP Armor Color", G_ESP, 210f),
        toggle("esp_preview", "ESP Preview", "ESP Preview", G_ESP, true),
        option("esp_profiles", "ESP Profiles", "ESP Profiles", G_ESP,
            listOf("Padrão", "Competitivo", "Minimalista", "Torneio"))
    )

    private val recoil = listOf(
        toggle("recoil_none", "No Recoil", "No Recoil", G_RECOIL, true),
        toggle("recoil_control", "Recoil Control", "Recoil Control", G_RECOIL),
        slider("recoil_vertical", "Recoil Vertical", "Recoil Vertical", G_RECOIL, 0f, 100f, 65f, "%"),
        slider("recoil_horizontal", "Recoil Horizontal", "Recoil Horizontal", G_RECOIL, 0f, 100f, 35f, "%"),
        slider("recoil_smooth", "Recoil Smooth", "Recoil Smooth", G_RECOIL, 0f, 100f, 50f, "%"),
        toggle("recoil_stabilizer", "Recoil Stabilizer", "Recoil Stabilizer", G_RECOIL),
        option("recoil_pattern", "Recoil Pattern", "Recoil Pattern", G_RECOIL,
            listOf("AK47", "M4A1", "MP40", "UMP", "SCAR")),
        toggle("recoil_preview", "Recoil Preview", "Recoil Preview", G_RECOIL, true),
        slider("recoil_spread", "Bullet Spread", "Bullet Spread", G_RECOIL, 0f, 100f, 25f, "%"),
        slider("spread_control", "Spread Control", "Spread Control", G_RECOIL, 0f, 100f, 60f, "%"),
        toggle("rapid_fire", "Rapid Fire", "Rapid Fire", G_RECOIL),
        toggle("auto_fire", "Auto Fire", "Auto Fire", G_RECOIL),
        slider("fire_rate", "Fire Rate", "Fire Rate", G_RECOIL, 100f, 1200f, 660f, "rpm"),
        toggle("auto_scope", "Auto Scope", "Auto Scope", G_RECOIL),
        toggle("quick_scope", "Quick Scope", "Quick Scope", G_RECOIL),
        toggle("weapon_stabilizer", "Weapon Stabilizer", "Weapon Stabilizer", G_RECOIL),
        option("weapon_selector", "Weapon Selector", "Weapon Selector", G_RECOIL,
            listOf("AK47", "M4A1", "MP40", "UMP", "SCAR", "M1014")),
        option("weapon_profiles", "Weapon Profiles", "Weapon Profiles", G_RECOIL,
            listOf("Assalto", "SMG", "Escopeta", "Precisão")),
        slider("accuracy_sim", "Accuracy Simulator", "Accuracy Simulator", G_RECOIL, 0f, 100f, 75f, "%"),
        toggle("shooting_range", "Shooting Range", "Shooting Range", G_RECOIL, true)
    )

    private val cross = listOf(
        toggle("cross_enable", "Crosshair Enable", "Crosshair Enable", G_CROSS, true),
        toggle("cross_dot", "Crosshair Dot", "Crosshair Dot", G_CROSS, true),
        toggle("cross_circle", "Crosshair Circle", "Crosshair Circle", G_CROSS),
        toggle("cross_cross", "Crosshair Cross", "Crosshair Cross", G_CROSS, true),
        toggle("cross_tshape", "Crosshair T-Shape", "Crosshair T-Shape", G_CROSS),
        toggle("cross_dynamic", "Crosshair Dynamic", "Crosshair Dynamic", G_CROSS),
        toggle("cross_rgb", "Crosshair RGB", "Crosshair RGB", G_CROSS),
        toggle("cross_rainbow", "Crosshair Rainbow", "Crosshair Rainbow", G_CROSS),
        slider("cross_glow", "Crosshair Glow", "Crosshair Glow", G_CROSS, 0f, 100f, 25f, "%"),
        toggle("cross_outline", "Crosshair Outline", "Crosshair Outline", G_CROSS),
        slider("cross_size", "Crosshair Size", "Crosshair Size", G_CROSS, 6f, 80f, 22f, "px"),
        slider("cross_thickness", "Crosshair Thickness", "Crosshair Thickness", G_CROSS, 1f, 10f, 2f, "px"),
        slider("cross_gap", "Crosshair Gap", "Crosshair Gap", G_CROSS, 0f, 30f, 6f, "px"),
        slider("cross_opacity", "Crosshair Opacity", "Crosshair Opacity", G_CROSS, 10f, 100f, 100f, "%"),
        color("cross_color", "Crosshair Color", "Crosshair Color", G_CROSS, 120f),
        slider("cross_rotation", "Crosshair Rotation", "Crosshair Rotation", G_CROSS, 0f, 360f, 0f, "°"),
        option("cross_pos", "Crosshair Position", "Crosshair Position", G_CROSS,
            listOf("Centro", "Superior", "Inferior")),
        option("cross_anim", "Crosshair Animation", "Crosshair Animation", G_CROSS,
            listOf("Nenhuma", "Pulso", "Rotação")),
        option("cross_presets", "Crosshair Presets", "Crosshair Presets", G_CROSS,
            listOf("Padrão", "Ponto", "Competitivo", "Sniper", "Neon")),
        toggle("cross_preview", "Crosshair Preview", "Crosshair Preview", G_CROSS, true),
        action("cross_reset", "Crosshair Reset", "Crosshair Reset", G_CROSS)
    )

    private val radar = listOf(
        toggle("radar_enable", "Radar 2D", "Radar 2D", G_RADAR, true),
        toggle("radar_circular", "Radar Circular", "Radar Circular", G_RADAR, true),
        toggle("radar_square", "Radar Quadrado", "Radar Square", G_RADAR),
        toggle("radar_rgb", "Radar RGB", "Radar RGB", G_RADAR),
        slider("radar_zoom", "Radar Zoom", "Radar Zoom", G_RADAR, 50f, 400f, 120f, "%"),
        slider("radar_rotation", "Radar Rotation", "Radar Rotation", G_RADAR, 0f, 360f, 0f, "°"),
        toggle("radar_grid", "Radar Grid", "Radar Grid", G_RADAR, true),
        slider("radar_opacity", "Radar Opacity", "Radar Opacity", G_RADAR, 10f, 100f, 90f, "%"),
        slider("radar_size", "Radar Size", "Radar Size", G_RADAR, 40f, 100f, 70f, "%"),
        option("radar_position", "Radar Position", "Radar Position", G_RADAR,
            listOf("Centro", "Canto superior esquerdo", "Canto superior direito",
                "Canto inferior esquerdo", "Canto inferior direito")),
        toggle("radar_dots", "Radar Target Dots", "Radar Target Dots", G_RADAR, true),
        toggle("radar_team_colors", "Radar Team Colors", "Radar Team Colors", G_RADAR, true),
        toggle("radar_preview", "Radar Preview", "Radar Preview", G_RADAR, true)
    )

    private val settings = listOf(
        option("app_theme", "Tema", "Theme", G_SET, listOf("Escuro", "Claro", "AMOLED")),
        color("app_accent", "Cor de destaque", "Accent color", G_SET, 195f),
        toggle("app_animations", "Animações", "Animations", G_SET, true),
        slider("app_anim_speed", "Velocidade das animações", "Animation speed", G_SET, 50f, 200f, 100f, "%"),
        slider("app_transparency", "Transparência da UI", "UI transparency", G_SET, 40f, 100f, 92f, "%"),
        toggle("app_compact", "Modo compacto", "Compact mode", G_SET),
        toggle("app_fullscreen", "Modo tela cheia", "Fullscreen mode", G_SET),
        toggle("app_notifications", "Notificações", "Notifications", G_SET, true),
        toggle("app_autosave", "Salvar automaticamente", "Auto save", G_SET, true),
        toggle("app_rgb", "Efeitos RGB", "RGB effects", G_SET),
        toggle("app_history", "Histórico de alterações", "Change history", G_SET, true),
        option("app_language", "Idioma", "Language", G_SET, listOf("Português", "English")),
        slider("bubble_size", "Tamanho da bolinha", "Bubble size", G_SET, 40f, 90f, 56f, "dp"),
        slider("bubble_opacity", "Opacidade da bolinha", "Bubble opacity", G_SET, 20f, 100f, 90f, "%"),
        toggle("overlay_crosshair", "Crosshair na sobreposição", "Overlay crosshair", G_SET),
        toggle("overlay_panel", "Painel expansível", "Expandable panel", G_SET, true),
        toggle("overlay_haptics", "Vibração ao arrastar", "Drag haptics", G_SET, true),
        slider("overlay_snap", "Encaixe nas bordas", "Edge snap", G_SET, 0f, 100f, 40f, "%"),
        action("restore_defaults", "Restaurar padrões", "Restore defaults", G_SET),
        action("export_json", "Exportar JSON", "Export JSON", G_SET),
        action("import_json", "Importar JSON", "Import JSON", G_SET),
        action("open_profiles", "Gerenciar perfis", "Manage profiles", G_SET)
    )

    val groups: List<SpecGroup> = listOf(
        SpecGroup(G_AIM, "Aimbot", "Aimbot", "crosshair", aim),
        SpecGroup(G_FOV, "Aim FOV", "Aim FOV", "radar", fov),
        SpecGroup(G_ESP, "ESP", "ESP", "visibility", esp),
        SpecGroup(G_RECOIL, "Recoil & Armas", "Recoil & Weapons", "bolt", recoil),
        SpecGroup(G_CROSS, "Crosshair Premium", "Premium Crosshair", "add", cross),
        SpecGroup(G_RADAR, "Radar", "Radar", "explore", radar),
        SpecGroup(G_SET, "Configurações", "Settings", "settings", settings)
    )

    val byKey: Map<String, Spec> = groups.flatMap { it.specs }.associateBy { it.key }
    val all: List<Spec> = groups.flatMap { it.specs }

    fun group(id: String): SpecGroup? = groups.firstOrNull { it.id == id }

    fun search(query: String, lang: String): List<Spec> {
        if (query.isBlank()) return emptyList()
        val q = query.trim().lowercase()
        return all.filter { it.text(lang).lowercase().contains(q) || it.key.contains(q) }
    }
}
