package com.vzyn.ffpanel.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.vzyn.ffpanel.storage.SettingsRepository
import com.vzyn.ffpanel.storage.SettingsState
import kotlinx.coroutines.flow.StateFlow
class SettingsViewModel(private val repo: SettingsRepository) : ViewModel() {
    val state: StateFlow<SettingsState> = repo.state

    fun set(key: String, value: Float) = repo.set(key, value)
    fun toggle(key: String, current: Boolean) = repo.set(key, if (current) 0f else 1f)
    fun resetGroup(group: String) = repo.resetGroup(group)
    fun resetAll() = repo.resetAll()
    fun activate(id: String) = repo.activate(id)
    fun create(name: String) = repo.createProfile(name)
    fun rename(id: String, name: String) = repo.renameProfile(id, name)
    fun duplicate(id: String) = repo.duplicateProfile(id)
    fun delete(id: String) = repo.deleteProfile(id)
    fun export(): String = repo.exportJson()
    fun import(raw: String): Result<Unit> = repo.importJson(raw)

    companion object {
        fun factory(repo: SettingsRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer { SettingsViewModel(repo) }
        }
    }
}
