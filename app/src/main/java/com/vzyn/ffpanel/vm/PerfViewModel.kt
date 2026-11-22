package com.vzyn.ffpanel.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.vzyn.ffpanel.perf.PerfSampler
import com.vzyn.ffpanel.perf.PerfSnapshot
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class PerfViewModel(app: Application) : AndroidViewModel(app) {

    private val sampler = PerfSampler(app)
    private val _snapshot = MutableStateFlow<PerfSnapshot?>(null)
    val snapshot: StateFlow<PerfSnapshot?> = _snapshot.asStateFlow()
    private val _history = MutableStateFlow<List<Float>>(emptyList())
    val history: StateFlow<List<Float>> = _history.asStateFlow()

    init {
        viewModelScope.launch {
            while (isActive) {
                val snap = sampler.sample()
                _snapshot.value = snap
                _history.value = (_history.value + snap.cpuPercent).takeLast(60)
                delay(1000)
            }
        }
    }

    companion object {
        fun factory(app: Application): ViewModelProvider.Factory = viewModelFactory {
            initializer { PerfViewModel(app) }
        }
    }
}
