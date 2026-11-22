
package com.vzyn.ffpanel.storage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.vzyn.ffpanel.core.Catalog
import com.vzyn.ffpanel.core.Kind
import com.vzyn.ffpanel.core.text
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

private val Context.settingsDataStore by preferencesDataStore(name = "vzyn_settings")

class SettingsRepository(private val context: Context) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val key = stringPreferencesKey("blob")
    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()
    private var saveJob: Job? = null

    init {
        scope.launch {
            val raw = runCatching { context.settingsDataStore.data.first()[key] }.getOrNull()
            if (!raw.isNullOrBlank()) {
                runCatching { _state.value = decode(raw) }
            }
        }
    }

    private fun scheduleSave() {
        saveJob?.cancel()
        saveJob = scope.launch {
            delay(350)
            val blob = encode(_state.value)
            runCatching { context.settingsDataStore.edit { it[key] = blob } }
        }
    }

    fun set(key: String, value: Float) {
        val st = _state.value
        val spec = Catalog.byKey[key] ?: return
        val values = st.active.values + (key to value)
        val profiles = st.profiles.map { if (it.id == st.activeId) it.copy(values = values) else it }
        val history = if (st.on("app_history") &&
            (spec.kind == Kind.TOGGLE || spec.kind == Kind.OPTION || spec.kind == Kind.COLOR)
        ) {
            val shown = when (spec.kind) {
                Kind.TOGGLE -> if (value > 0.5f) "ON" else "OFF"
                Kind.OPTION -> spec.options.getOrNull(value.toInt()) ?: value.toInt().toString()
                else -> value.toInt().toString()
            }
            (listOf(HistoryEntry(key, spec.text(st.lang), shown, System.currentTimeMillis())) + st.history)
                .take(200)
        } else st.history
        _state.value = st.copy(profiles = profiles, history = history)
        if (st.on("app_autosave")) scheduleSave()
    }

    fun resetGroup(group: String) {
        val st = _state.value
        val values = st.active.values + st.defaultsFor(group)
        val profiles = st.profiles.map { if (it.id == st.activeId) it.copy(values = values) else it }
        _state.value = st.copy(profiles = profiles)
        scheduleSave()
    }

    fun resetAll() {
        val st = _state.value
        val profiles = st.profiles.map {
            if (it.id == st.activeId) it.copy(values = st.allDefaults()) else it
        }
        _state.value = st.copy(profiles = profiles, history = emptyList())
        scheduleSave()
    }

    fun activate(id: String) {
        if (_state.value.profiles.none { it.id == id }) return
        _state.value = _state.value.copy(activeId = id)
        scheduleSave()
    }

    fun createProfile(name: String) {
        val id = UUID.randomUUID().toString()
        val st = _state.value
        _state.value = st.copy(
            profiles = st.profiles + Profile(id, name.ifBlank { "Perfil ${st.profiles.size + 1}" }),
            activeId = id
        )
        scheduleSave()
    }

    fun renameProfile(id: String, name: String) {
        if (name.isBlank()) return
        _state.value = _state.value.copy(
            profiles = _state.value.profiles.map { if (it.id == id) it.copy(name = name.trim()) else it }
        )
        scheduleSave()
    }

    fun duplicateProfile(id: String) {
        val src = _state.value.profiles.firstOrNull { it.id == id } ?: return
        val newId = UUID.randomUUID().toString()
        val st = _state.value
        _state.value = st.copy(
            profiles = st.profiles + Profile(newId, "${src.name} (cópia)", src.values),
            activeId = newId
        )
        scheduleSave()
    }

    fun deleteProfile(id: String) {
        val st = _state.value
        if (st.profiles.size <= 1) return
        val remaining = st.profiles.filterNot { it.id == id }
        _state.value = st.copy(
            profiles = remaining,
            activeId = if (st.activeId == id) remaining.first().id else st.activeId
        )
        scheduleSave()
    }

    fun exportJson(): String = encode(_state.value)

    fun importJson(raw: String): Result<Unit> = runCatching {
        _state.value = decode(raw)
        scheduleSave()
    }

    private fun encode(state: SettingsState): String {
        val root = JSONObject()
        root.put("version", 10)
        root.put("activeId", state.activeId)
        val arr = JSONArray()
        state.profiles.forEach { p ->
            val o = JSONObject()
            o.put("id", p.id)
            o.put("name", p.name)
            val v = JSONObject()
            p.values.forEach { (k, f) -> v.put(k, f.toDouble()) }
            o.put("values", v)
            arr.put(o)
        }
        root.put("profiles", arr)
        return root.toString()
    }

    private fun decode(raw: String): SettingsState {
        val root = JSONObject(raw)
        val arr = root.optJSONArray("profiles") ?: JSONArray()
        val profiles = ArrayList<Profile>()
        for (i in 0 until arr.length()) {
            val o = arr.optJSONObject(i) ?: continue
            val id = o.optString("id").ifBlank { UUID.randomUUID().toString() }
            val name = o.optString("name").ifBlank { "Perfil ${i + 1}" }
            val values = HashMap<String, Float>()
            val v = o.optJSONObject("values")
            if (v != null) {
                v.keys().forEach { k ->
                    if (Catalog.byKey.containsKey(k)) values[k] = v.optDouble(k).toFloat()
                }
            }
            profiles.add(Profile(id, name, values))
        }
        if (profiles.isEmpty()) profiles.add(Profile("default", "Padrão"))
        val active = root.optString("activeId").let { a ->
            if (profiles.any { it.id == a }) a else profiles.first().id
        }
        return SettingsState(profiles, active)
    }
}
