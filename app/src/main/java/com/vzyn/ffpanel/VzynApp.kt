package com.vzyn.ffpanel

import android.app.Application
import com.vzyn.ffpanel.overlay.NotificationHelper
import com.vzyn.ffpanel.storage.SettingsRepository

class VzynApp : Application() {
    val repo: SettingsRepository by lazy { SettingsRepository(this) }

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.ensureChannel(this)
    }
}
