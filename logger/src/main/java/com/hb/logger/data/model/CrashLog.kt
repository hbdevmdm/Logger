package com.hb.logger.data.model

import android.os.Parcelable
import androidx.room.Entity
import com.hb.logger.util.toFullDateTimeFormat
import com.hb.logger.util.toStandardDateTimeFormat
import kotlinx.android.parcel.Parcelize
import org.json.JSONObject

@Entity
@Parcelize
data class CrashLog(var cause: String,
                    var stackTrace: String,
                    var deviceInfo: String,
                    var time: String) : Parcelable, GenericLog() {
    fun generateLog(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("Error Information")
        stringBuilder.append("\n--------------------\n")
        stringBuilder.append("[CAUSE]=").append(cause).append("\n")
        stringBuilder.append("[TIME]=").append(time.toLong().toFullDateTimeFormat()).append("\n")
        stringBuilder.append("[STACKTRACE]").append("\n").append(stackTrace).append("\n")
        stringBuilder.append("\n\nDevice Information")
        stringBuilder.append("\n--------------------\n")
        val jsonObject = JSONObject(deviceInfo)
        stringBuilder.append(jsonObject.toString(1))
        return stringBuilder.toString()
    }

    override fun generateStandardLog(eventsSequenceLog: EventsSequenceLog): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("[${time.toLong().toStandardDateTimeFormat()}]")
        stringBuilder.append("[ERROR]")
        stringBuilder.append("CAUSE=").append(cause).append("\n")
        stringBuilder.append("TIME=").append(time.toLong().toStandardDateTimeFormat()).append("\n")
        stringBuilder.append("STACKTRACE").append("\n").append(stackTrace).append("\n")
        stringBuilder.append("DEVICE INFO=")
        val jsonObject = JSONObject(deviceInfo)
        stringBuilder.append(jsonObject.toString(1))
        return stringBuilder.toString()
    }
}