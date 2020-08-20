package com.hb.logger.data.model

import android.os.Parcelable
import androidx.room.Entity
import com.hb.logger.util.optNA
import com.hb.logger.util.toFullDateTimeFormat
import com.hb.logger.util.toStandardDateTimeFormat
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class CustomLog(var className: String,
                     var event: String,
                     var method: String,
                     var detail: String,
                     var time: String,
                     var user: String,
                     var status: String = STATUS_INFO) : GenericLog(), Parcelable {
    fun generateLog(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("Event Information")
        stringBuilder.append("\n--------------------\n")
        stringBuilder.append("[CLASS]=").append(className).append("\n")
        stringBuilder.append("[METHOD]=").append(method).append("\n")
        stringBuilder.append("[EVENT]=").append(event).append("\n")
        stringBuilder.append("[DETAIL]=").append(detail).append("\n")
        stringBuilder.append("[TIME]=").append(time.toLong().toFullDateTimeFormat()).append("\n")
        stringBuilder.append("[USER]=").append(user.optNA()).append("\n")
        return stringBuilder.toString()
    }

    override fun generateStandardLog(eventsSequenceLog: EventsSequenceLog): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("[${time.toLong().toStandardDateTimeFormat()}]")
        stringBuilder.append("[${eventsSequenceLog.logType}]")
        stringBuilder.append("CLASS=").append(className).append("\n")
        stringBuilder.append("METHOD=").append(method).append("\n")
        stringBuilder.append("EVENT=").append(event).append("\n")
        stringBuilder.append("DETAIL=").append(detail).append("\n")
        stringBuilder.append("USER=").append(user.optNA()).append("\n")
        return stringBuilder.toString()
    }

    companion object {
        const val STATUS_INFO = "STATUS_INFO"
        const val STATUS_WARNING = "STATUS_WARNING"
        const val STATUS_ERROR = "STATUS_ERROR"
        const val STATUS_SUCCESS = "STATUS_SUCCESS"
    }
}