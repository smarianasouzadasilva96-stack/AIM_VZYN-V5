package com.vzyn.ffpanel.storage

import androidx.compose.runtime.Immutable
import com.vzyn.ffpanel.core.Catalog
import com.vzyn.ffpanel.core.Spec
import com.vzyn.ffpanel.core.text

@Immutable
data class Profile(
    val id: String,
    val name: String,
    val values: Map<String, Float> = emptyMap()
)

@Immutable
data class HistoryEntry(
    val key: String,
    val label: String,
    val value: String,
    val time: Long
)

@Immutable
data class SettingsState(
    val profiles: List<Profile> = listOf(Profile("default", "Padrão")),
    val activeId: String = "default",
    val history: List<HistoryEntry> = emptyList()
) {
    val active: Profile
        get() = profiles.firstOrNull { it.id == activeId } ?: profiles.first()

    fun raw(key: String): Float = active.values[key] ?: Catalog.byKey[key]?.default ?: 0f
    fun on(key: String): Boolean = raw(key) > 0.5f
    fun num(key: String): Float = raw(key)
    fun idx(key: String): Int = raw(key).toInt().coerceAtLeast(0)
    fun hue(key: String): Float = raw(key)
    fun optionText(key: String): String = Catalog.byKey[key]?.options?.getOrNull(idx(key)) ?: ""

    val lang: String get() = if (idx("app_language") == 1) "en" else "pt"
    val isEn: Boolean get() = lang == "en"

    fun spec(key: String): Spec? = Catalog.byKey[key]
    fun label(key: String): String = Catalog.byKey[key]?.text(lang) ?: key

    fun defaultsFor(group: String): Map<String, Float> =
        Catalog.all.filter { it.group == group }.associate { it.key to it.default }

    fun allDefaults(): Map<String, Float> = Catalog.all.associate { it.key to it.default }
}
