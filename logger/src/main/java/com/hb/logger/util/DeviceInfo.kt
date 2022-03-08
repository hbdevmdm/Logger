package com.hb.logger.util

import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager.EXTRA_LEVEL
import android.os.BatteryManager.EXTRA_SCALE
import android.os.Build
import android.os.Environment.getDataDirectory
import android.os.StatFs
import android.provider.Settings
import java.io.IOException
import java.io.RandomAccessFile
import java.util.*


fun getDeviceModel(): String {
    val manufacturer = Build.MANUFACTURER
    val model = Build.MODEL
    return "$manufacturer $model"
}

fun getAndroidVersion(): String {
    return android.os.Build.VERSION.SDK_INT.toString()
}

fun isRooted(): Boolean {
    var process: Process? = null
    try {
        process = Runtime.getRuntime().exec("su")
        return true
    } catch (e: Exception) {
        return false
    } finally {
        if (process != null) {
            try {
                process.destroy()
            } catch (e: Exception) {
            }

        }
    }
}

fun getDeviceLanguage(): String {
    return Locale.getDefault().displayLanguage
}

fun Context.isUsbDebuggingEnabled(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        Settings.Secure.getInt(contentResolver, Settings.Global.ADB_ENABLED, 0) == 1
    } else {
        Settings.Secure.getInt(contentResolver, Settings.Secure.ADB_ENABLED, 0) == 1
    }
}


fun Context.getMemoryUsagePercentage(): Double {
    var totalMemory: Long = 0

    val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
    val mi = ActivityManager.MemoryInfo()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        activityManager.getMemoryInfo(mi)
        totalMemory = mi.totalMem
    } else {
        var reader: RandomAccessFile? = null
        val load: String
        try {
            reader = RandomAccessFile("/proc/meminfo", "r")
            load = reader.readLine().replace("\\D+", "")
            totalMemory = Integer.parseInt(load).toLong()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (reader != null) {
                try {
                    reader.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
    }
    return (mi.availMem / totalMemory.toDouble() * 100.0)
}

fun getAvailableInternalMemorySize(): Float {
    val path = getDataDirectory()
    val stat = StatFs(path.path)
    val blockSize: Long
    val availableBlocks: Long
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
        blockSize = stat.blockSizeLong
        availableBlocks = stat.availableBlocksLong
    } else {
        blockSize = stat.blockSize.toLong()
        availableBlocks = stat.availableBlocks.toLong()
    }
    val valInBytes = availableBlocks * blockSize
    return valInBytes.toFloat() / (1024 * 1024 * 1024)

}

fun Context.getOrientation(): String {
    return when (resources.configuration.orientation) {
        0 -> "Undefined"
        1 -> "Portrait"
        2 -> "Landscape"
        3 -> "Square"
        else -> "Undefined"
    }
}

fun Context.getBatteryPercentage(): Int {
    var percentage = 0
    val batFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    val batteryStatus = registerReceiver(null, batFilter)
    if (batteryStatus != null) {
        val level = batteryStatus.getIntExtra(EXTRA_LEVEL, -1)
        val scale = batteryStatus.getIntExtra(EXTRA_SCALE, -1)
        percentage = (level / scale.toFloat() * 100).toInt()
    }
    return percentage
}

fun Context.isAppOnForeground(): Boolean {
    val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
    val appProcesses = activityManager.runningAppProcesses ?: return false
    for (appProcess in appProcesses) {
        if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName == packageName) {
            return true
        }
    }
    return false
}