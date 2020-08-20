package com.hb.logger.util

import android.content.Context
import android.graphics.Point
import android.text.TextUtils
import android.view.WindowManager
import java.text.SimpleDateFormat
import java.util.*

fun String.quoted(): String {
    return "'${this}'"
}

fun String.optNA(): String {
    return if (TextUtils.isEmpty(this)) "N/A" else this
}

fun Long.toStandardDateTimeFormat(): String {
    val formatter = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss.S", Locale.ENGLISH)
    return formatter.format(Date(this))
}


fun Long.toFullDateTimeFormat(): String {
    val formatter = SimpleDateFormat("dd MMM yyyy hh:mm:ss a", Locale.ENGLISH)
    return formatter.format(Date(this))
}

fun Long.toDateTimeFormat(): String {
    val formatter = SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.ENGLISH)
    return formatter.format(Date(this))
}

fun Long.toDateFormat(): String {
    val formatter = SimpleDateFormat("ddMMMyyyy_hh-mma", Locale.ENGLISH)
    return formatter.format(Date(this))
}


fun Context.getWindowDimension(): Point {
    val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = wm.defaultDisplay
    val size = Point()
    display.getSize(size)
    return size
}

