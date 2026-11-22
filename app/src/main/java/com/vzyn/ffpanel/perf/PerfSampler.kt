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
import java.io.File

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
    private var lastRx = 0L

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
        val rx = File("/proc/self/net/dev").let { f ->
            runCatching {
                f.readLines().drop(1).sumOf { line ->
                    line.substringAfter(':').trim().split(Regex("\\s+"))
                        .getOrNull(0)?.toLongOrNull() ?: 0L
                }
            }.getOrDefault(0L)
        }
        val delta = rx - lastRx
        lastRx = rx
        return if (delta <= 0) 0f else (delta / 1024f / 1024f * 8f)
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
