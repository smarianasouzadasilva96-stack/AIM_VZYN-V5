 package com.vzyn.ffpanel.overlay

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.vzyn.ffpanel.R

object NotificationHelper {
    const val CHANNEL_ID = "vzyn_overlay"
    const val NOTIFICATION_ID = 1001

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        if (manager.getNotificationChannel(CHANNEL_ID) == null) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.channel_overlay),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = context.getString(R.string.channel_overlay_desc)
                setShowBadge(false)
            }
            manager.createNotificationChannel(channel)
        }
    }
}
