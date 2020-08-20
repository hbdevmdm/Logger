package com.hb.logger.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.hb.logger.Logger
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class EventsSequenceLog(

        @PrimaryKey(autoGenerate = true)
        var id: Long = 0,

        @ColumnInfo(name = "sessionId")
        var sessionId: Long = 0,

        @ColumnInfo(name = "logType")
        var logType: String = "", // ERROR, CUSTOM, NETWORK

        @ColumnInfo(name = "logId")
        var logId: Long = 0,

        @ColumnInfo(name = "time")
        var time: String = "",

        @Ignore
        var log: GenericLog? = null):Parcelable {
    fun getLogObj(): GenericLog? {

        if (log == null) {
            when (logType) {
                LOG_TYPE_NETWORK -> {
                    val networkLog: NetworkLog = Logger.database?.networkLogDao()!!.getNetworkLogById(logId)
                    log = networkLog
                }
                LOG_TYPE_ERROR -> {
                    val crashLog: CrashLog = Logger.database?.crashLogDao()!!.getCrashLogById(logId)
                    log = crashLog
                }
                LOG_TYPE_INFO, LOG_TYPE_DEBUG, LOG_TYPE_WARNING -> {
                    val customLog: CustomLog = Logger.database?.customLogDao()!!.getCustomLogById(logId)
                    log = customLog
                }
            }
        }
        return log

    }

    companion object{
        const val LOG_TYPE_NETWORK="NETWORK"
        const val LOG_TYPE_ERROR="ERROR"
        const val LOG_TYPE_INFO="INFO"
        const val LOG_TYPE_DEBUG="DEBUG"
        const val LOG_TYPE_WARNING="WARNING"
    }
}


