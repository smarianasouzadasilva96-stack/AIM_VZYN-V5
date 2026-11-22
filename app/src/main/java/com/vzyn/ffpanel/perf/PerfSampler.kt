package com.vzyn.ffpanel.perf

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.Process
import android.os.StatFs
import android.os.SystemClock
import android.net.TrafficStats

data class PerfSnapshot(
    val ramUsedMb: Long,
    val ramTotalMb: Long,
    val batteryPercent: Int,
    val batteryTempC: Float,
    val storageUsedMb: Long,
    val storageTotalMb: Long,
    val cpuPercent: Float,
    val appHeapMb: Long,
    val networkMbps: Float
)

class PerfSampler(private val context: Context) {

    private var lastCpuMs = 0L
    private var lastWall = 0L
    private var lastRx = -1L
    private var lastNetworkMs = 0L

    fun sample(): PerfSnapshot {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val mi = ActivityManager.MemoryInfo()
        am.getMemoryInfo(mi)
        val totalMb = mi.totalMem / (1024L * 1024L)
        val usedMb = (mi.totalMem - mi.availMem) / (1024L * 1024L)

        val battery = context.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
        val level = battery?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = battery?.getIntExtra(BatteryManager.EXTRA_SCALE, 100) ?: 100
        val pct = if (level >= 0 && scale > 0) level * 100 / scale else -1
        val temp = (battery?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 0) / 10f

        val stat = StatFs(Environment.getDataDirectory().absolutePath)
        val blockSize = stat.blockSizeLong
        val storageTotal = stat.blockCountLong * blockSize / (1024L * 1024L)
        val storageFree = stat.availableBlocksLong * blockSize / (1024L * 1024L)

        val runtime = Runtime.getRuntime()
        val heapMb = (runtime.totalMemory() - runtime.freeMemory()) / (1024L * 1024L)

        val cpuNow = Process.getElapsedCpuTime()
        val wallNow = SystemClock.elapsedRealtime()
        val cpuPercent = if (lastWall > 0L) {
            val cpuDelta = (cpuNow - lastCpuMs).toFloat()
            val wallDelta = (wallNow - lastWall).toFloat().coerceAtLeast(1f)
            (cpuDelta / wallDelta * 100f / cores()).coerceIn(0f, 100f)
        } else 0f
        lastCpuMs = cpuNow
        lastWall = wallNow

        return PerfSnapshot(
            ramUsedMb = usedMb,
            ramTotalMb = totalMb,
            batteryPercent = pct,
            batteryTempC = temp,
            storageUsedMb = storageTotal - storageFree,
            storageTotalMb = storageTotal,
            cpuPercent = cpuPercent,
            appHeapMb = heapMb,
            networkMbps = networkThroughput()
        )
    }

    private fun cores(): Float =
        Runtime.getRuntime().availableProcessors().coerceAtLeast(1).toFloat()

    private fun networkThroughput(): Float {
        val rx = TrafficStats.getUidRxBytes(Process.myUid())
        val now = SystemClock.elapsedRealtime()
        val result = if (rx != TrafficStats.UNSUPPORTED &&
            lastRx >= 0L && now > lastNetworkMs && rx >= lastRx
        ) {
            val bits = (rx - lastRx).toDouble() * 8.0
            (bits * 1000.0 / (now - lastNetworkMs) / 1_000_000.0).toFloat()
        } else 0f
        lastRx = rx
        lastNetworkMs = now
        return result.coerceAtLeast(0f)
    }

    fun deviceSummary(): String = buildString {
        append(Build.MANUFACTURER.replaceFirstChar { it.uppercase() })
        append(' ')
        append(Build.MODEL)
        append(" · Android ")
        append(Build.VERSION.RELEASE)
        append(" · API ")
        append(Build.VERSION.SDK_INT)
        append(" · ")
        append(Runtime.getRuntime().availableProcessors())
        append(" núcleos")
    }
}
