package com.vzyn.ffpanel.overlay

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.vzyn.ffpanel.MainActivity
import com.vzyn.ffpanel.R
import com.vzyn.ffpanel.VzynApp
import com.vzyn.ffpanel.storage.SettingsState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.abs

class OverlayService : LifecycleService() {

    private lateinit var wm: WindowManager
    private var bubble: View? = null
    private var panel: View? = null
    private var cross: CrosshairView? = null
    private var observeJob: Job? = null
    private lateinit var bubbleParams: WindowManager.LayoutParams
    private lateinit var crossParams: WindowManager.LayoutParams

    private val repo get() = (application as VzynApp).repo

    override fun onCreate() {
        super.onCreate()
        isRunning = true
        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        startForegroundCompat()
        addCrosshair()
        addBubble()
        observeJob = lifecycleScope.launch {
            repo.state.collect { applyState(it) }
        }
    }

    private fun startForegroundCompat() {
        val openIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val stopIntent = PendingIntent.getService(
            this, 1,
            Intent(this, OverlayService::class.java).setAction(ACTION_STOP),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification: Notification = NotificationCompat.Builder(this, NotificationHelper.CHANNEL_ID)
            .setContentTitle("VZYN FF PANEL V10")
            .setContentText("Sobreposição ativa")
            .setSmallIcon(R.drawable.ic_vzyn)
            .setOngoing(true)
            .setContentIntent(openIntent)
            .addAction(0, "Encerrar", stopIntent)
            .build()

        ServiceCompat.startForeground(
            this,
            NotificationHelper.NOTIFICATION_ID,
            notification,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE else 0
        )
    }

    private fun addCrosshair() {
        val view = CrosshairView(this)
        crossParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            overlayType(),
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply { gravity = Gravity.TOP or Gravity.START }
        runCatching { wm.addView(view, crossParams) }
        cross = view
    }

    private fun addBubble() {
        val state = repo.state.value
        val sizePx = dp(state.num("bubble_size"))
        val view = TextView(this).apply {
            text = "V"
            setTextColor(Color.WHITE)
            textSize = 18f
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            background = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(0xFF00BFFF.toInt(), 0xFF8A2BE2.toInt())
            ).apply { shape = GradientDrawable.OVAL }
        }

        bubbleParams = WindowManager.LayoutParams(
            sizePx, sizePx,
            overlayType(),
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 24
            y = resources.displayMetrics.heightPixels / 3
        }

        var downX = 0f
        var downY = 0f
        var startX = 0
        var startY = 0

        view.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    downX = event.rawX
                    downY = event.rawY
                    startX = bubbleParams.x
                    startY = bubbleParams.y
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    bubbleParams.x = (startX + (event.rawX - downX)).toInt()
                    bubbleParams.y = (startY + (event.rawY - downY)).toInt()
                    runCatching { wm.updateViewLayout(view, bubbleParams) }
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (abs(event.rawX - downX) < 12f && abs(event.rawY - downY) < 12f) {
                        togglePanel()
                    } else {
                        snapToEdge()
                    }
                    true
                }
                else -> false
            }
        }

        runCatching { wm.addView(view, bubbleParams) }
        bubble = view
    }

    private fun snapToEdge() {
        val view = bubble ?: return
        val threshold = dp(repo.state.value.num("overlay_snap")) / 2f
        val width = resources.displayMetrics.widthPixels
        bubbleParams.x = if (bubbleParams.x + bubbleParams.width / 2f < width / 2f - threshold) 12
        else (width - bubbleParams.width - 12).coerceAtLeast(0)
        runCatching { wm.updateViewLayout(view, bubbleParams) }
    }

    private fun togglePanel() {
        if (panel != null) {
            panel?.let { runCatching { wm.removeView(it) } }
            panel = null
            return
        }
        if (!repo.state.value.on("overlay_panel")) return

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(10f), dp(10f), dp(10f), dp(10f))
            background = GradientDrawable().apply {
                cornerRadius = dp(16f).toFloat()
                setColor(0xF210141F.toInt())
                setStroke(dp(1f), 0xFF00BFFF.toInt())
            }
        }

        container.addView(menuButton("Abrir VZYN") { openApp() })
        container.addView(menuButton("Crosshair") {
            val current = repo.state.value.on("overlay_crosshair")
            repo.set("overlay_crosshair", if (current) 0f else 1f)
        })
        container.addView(menuButton("Free Fire") { launchFreeFire() })
        container.addView(menuButton("Encerrar") { stopSelf() })

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            overlayType(),
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = bubbleParams.x
            y = bubbleParams.y + bubbleParams.height + dp(8f)
        }
        runCatching { wm.addView(container, params) }
        panel = container
    }

    private fun menuButton(label: String, onClick: () -> Unit): TextView =
        TextView(this).apply {
            text = label
            setTextColor(0xFFEAF2FF.toInt())
            textSize = 12f
            setPadding(dp(8f), dp(9f), dp(8f), dp(9f))
            setOnClickListener { onClick() }
        }

    private fun openApp() {
        startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    private fun launchFreeFire() {
        val packages = listOf("com.dts.freefireth", "com.dts.freefiremax")
        val intent = packages.asSequence()
            .mapNotNull { packageManager.getLaunchIntentForPackage(it) }
            .firstOrNull()
            ?: Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.dts.freefireth"))
        runCatching { startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
    }

    private fun applyState(state: SettingsState) {
        val view = cross ?: return
        val enabled = state.on("overlay_crosshair") && state.on("cross_enable")
        val density = resources.displayMetrics.density
        view.config = CrossConfig(
            enabled = enabled,
            dot = state.on("cross_dot"),
            circle = state.on("cross_circle"),
            cross = state.on("cross_cross"),
            tShape = state.on("cross_tshape"),
            size = state.num("cross_size") * density,
            thickness = state.num("cross_thickness") * density,
            gap = state.num("cross_gap") * density,
            opacity = state.num("cross_opacity") / 100f,
            hue = state.hue("cross_color"),
            glow = state.num("cross_glow") / 100f,
            rotation = state.num("cross_rotation"),
            position = state.idx("cross_pos")
        )

        bubble?.let {
            val size = dp(state.num("bubble_size"))
            if (bubbleParams.width != size) {
                bubbleParams.width = size
                bubbleParams.height = size
                runCatching { wm.updateViewLayout(it, bubbleParams) }
            }
            it.alpha = (state.num("bubble_opacity") / 100f).coerceIn(0.2f, 1f)
        }
    }

    private fun overlayType(): Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else WindowManager.LayoutParams.TYPE_PHONE

    private fun dp(value: Float): Int = (value * resources.displayMetrics.density).toInt()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        if (intent?.action == ACTION_STOP) stopSelf()
        return START_STICKY
    }

    override fun onDestroy() {
        observeJob?.cancel()
        bubble?.let { runCatching { wm.removeView(it) } }
        panel?.let { runCatching { wm.removeView(it) } }
        cross?.let { runCatching { wm.removeView(it) } }
        bubble = null
        panel = null
        cross = null
        isRunning = false
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    companion object {
        const val ACTION_STOP = "com.vzyn.ffpanel.STOP_OVERLAY"

        @Volatile
        var isRunning = false
            private set
    }
}
