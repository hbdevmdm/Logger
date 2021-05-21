package com.hb.logger.data.model

import android.os.Parcelable
import android.text.TextUtils
import androidx.room.ColumnInfo
import androidx.room.Entity
import com.hb.logger.util.CommonUtils
import com.hb.logger.util.toFullDateTimeFormat
import com.hb.logger.util.toStandardDateTimeFormat
import kotlinx.android.parcel.Parcelize
import org.json.JSONArray
import org.json.JSONObject

@Entity
@Parcelize
data class NetworkLog(
        @ColumnInfo(name = "url")
        var url: String,

        @ColumnInfo(name = "apiMethod")
        var apiMethod: String,

        @ColumnInfo(name = "requestTime")
        var requestTime: String,

        @ColumnInfo(name = "responseTime")
        var responseTime: String,

        @ColumnInfo(name = "responseStatusCode")
        var responseStatusCode: String,

        @ColumnInfo(name = "resContentType")
        var resContentType: String,

        @ColumnInfo(name = "responseSize")
        var responseSize: String,

        @ColumnInfo(name = "paramLog")
        var paramLog: String,

        @ColumnInfo(name = "headerLog")
        var headerLog: String,

        @ColumnInfo(name = "response")
        var response: String,

        var timeDuration: String? = "") : Parcelable, GenericLog() {

    fun generateLog(): String {

        val stringBuilder = StringBuilder()
        stringBuilder.append("Api Information")
        stringBuilder.append("\n--------------------\n")
        stringBuilder.append("[URL]=").append(url).append("\n")
        stringBuilder.append("[METHOD]=").append(apiMethod).append("\n")
        stringBuilder.append("[REQUEST DATE]=").append(requestTime.toLong().toFullDateTimeFormat()).append("\n")
        stringBuilder.append("[RESPONSE DATE]=").append(responseTime.toLong().toFullDateTimeFormat()).append("\n")
        stringBuilder.append("[TIME]=").append(responseTime.toLong().toFullDateTimeFormat()).append("\n")

        stringBuilder.append("\n\nRequest Details")
        stringBuilder.append("\n--------------------\n")

        stringBuilder.append(getHeaderJson())
        stringBuilder.append(getParamJson())
        stringBuilder.append(getResponseJson())

        return stringBuilder.toString()
    }


    override fun generateStandardLog(eventsSequenceLog: EventsSequenceLog): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("[${eventsSequenceLog.time.toLong().toStandardDateTimeFormat()}]")
        stringBuilder.append("[NETWORK]")
        stringBuilder.append("URL=").append(url).append("\n")
        stringBuilder.append("METHOD=").append(apiMethod).append("\n")
        stringBuilder.append("REQUEST DATE=").append(requestTime.toLong().toFullDateTimeFormat()).append("\n")
        stringBuilder.append("RESPONSE DATE=").append(responseTime.toLong().toFullDateTimeFormat()).append("\n")
        stringBuilder.append("HEADER:$headerLog").append("\n")
        stringBuilder.append("PARAMETER=$paramLog").append("\n")
        stringBuilder.append("RESPONSE=$response").append("\n")
        return stringBuilder.toString()

    }

    private fun getResponseJson(): String {
        val stringBuilder = StringBuilder()
        if (!TextUtils.isEmpty(response)) {
            stringBuilder.append("\n\nResponse Details")
            stringBuilder.append("\n--------------------\n")
            if (response.startsWith("{")) {
                val responseJSON = JSONObject(response)
                stringBuilder.append(responseJSON.toString(1))
            } else if (response.startsWith("[")) {
                val responseJSON = JSONArray(response)
                stringBuilder.append(responseJSON.toString(1))
            } else {
                stringBuilder.append(response)
            }
        }
        return stringBuilder.toString()
    }

    private fun getParamJson(): String {
        val stringBuilder = StringBuilder()
        if (!TextUtils.isEmpty(paramLog)) {
            stringBuilder.append("\nParameters\n\n")
            val paramJson = JSONArray(paramLog)

            for (i in 0 until paramJson.length()) {
                val key = paramJson.getJSONObject(i).getString("key")
                val value = paramJson.getJSONObject(i).getString("value")
                stringBuilder.append("[$key]\n").append(value).append("\n")
            }
        }
        return stringBuilder.toString()
    }

    private fun getHeaderJson(): String {
        val stringBuilder = StringBuilder()
        if (!TextUtils.isEmpty(headerLog)) {
            stringBuilder.append("\nHeaders\n\n")
            val headerJson = JSONArray(headerLog)
            for (i in 0 until headerJson.length()) {
                val key = headerJson.getJSONObject(i).getString("key")
                val value = headerJson.getJSONObject(i).getString("value")
                stringBuilder.append("[$key]\n").append(value).append("\n")
            }
        }
        return stringBuilder.toString()
    }


    fun getTimeDurationToDisplay(): String {
        if (TextUtils.isEmpty(timeDuration)) {
            val dateDifference: CommonUtils.Time = CommonUtils.getDateDifference(requestTime.toLong(), responseTime.toLong())
            val hour = dateDifference.hour
            val minute = dateDifference.minute
            val second = dateDifference.second
            val milliSecond = dateDifference.milliSecond

            if (hour <= 0 && minute <= 0 && second <= 0 && milliSecond < 1000) {
                // milliSeconds
                timeDuration = String.format("%s ms", milliSecond.toString())
            } else if (hour <= 0 && minute <= 0 && second < 60 && milliSecond < 1000) {
                // seconds + milliSeconds
                timeDuration = String.format("%s.%s s", String.format("%01d", second), milliSecond)
            } else if (hour <= 0 && minute < 60 && second < 60) {
                // minute + seconds
                timeDuration = String.format("%s.%s m", String.format("%01d", minute), second)
            } else if (hour > 0 && minute < 60) {
                // hour + minutes
                timeDuration = String.format("%s.%s h", String.format("%01d", hour), minute)
            }

        }
        return timeDuration!!
    }


}

