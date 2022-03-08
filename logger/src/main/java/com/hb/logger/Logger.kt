package com.hb.logger

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.room.Room
import com.hb.logger.data.database.AppDatabase
import com.hb.logger.data.model.CrashLog
import com.hb.logger.data.model.CustomLog
import com.hb.logger.data.model.EventsSequenceLog
import com.hb.logger.data.model.NetworkLog
import com.hb.logger.ui.log.LogsListActivity
import com.hb.logger.util.*
import org.json.JSONObject
import kotlin.system.exitProcess

class Logger() {

    lateinit var className: String

    constructor(obj: Any) : this() {
        className = obj.javaClass.simpleName
    }

    constructor(className: String) : this() {
        this.className = className
    }

    interface ExceptionCallback {
        fun onExceptionThrown(exception: Throwable)
    }


    companion object {

        var exceptionCallback: ExceptionCallback? = null
        var database: AppDatabase? = null
        var context: Context? = null
        var sessionId: Long = 0
        var user: String = ""
        var hasInitialize: Boolean = false

        /**
         * This mainly used for initialize logger
         */
        fun initializeSession(context: Context) {
            Companion.context = context
            sessionId = generateSession()
            hasInitialize = true

            database = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java, "logger"
            ).addMigrations(AppDatabase.MIGRATION_1_2).allowMainThreadQueries().build()

            if (Thread.getDefaultUncaughtExceptionHandler() != null) {
                Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
                    exceptionCallback?.onExceptionThrown(exception = exception)
                    exception.printStackTrace()
                    Logger(context.packageName).dumpCrashEvent(exception)
                    exitProcess(1)
                }
            }
        }

        /**
         * To reset logger session this will create new session
         */
        fun resetSession() {
            sessionId = generateSession()
        }

        private fun generateSession(): Long {
            return System.currentTimeMillis()
        }

        /**
         * To clear all logs from database
         */
        fun clearAllLogs() {
            database?.clearAllTables()
        }

        /**
         * To disable logger so no log will be reported
         */
        fun disableLogger() {
            hasInitialize = false
        }

        /**
         * To enable logger
         */
        fun enableLogger() {
            if (context == null) {
                throw IllegalArgumentException("Not initialize exception")
            }
            hasInitialize = true
        }

        fun isEnableLogger(): Boolean {
            return hasInitialize
        }

        /**
         * If you set user info then log will store user information so in future you can use to reproduce issue
         */
        fun setUserInfo(user: String) {
            this.user = user
        }


        fun makeCrash() {
            val x = 10 / 0
        }

        /**
         * To open log list screen
         */
        fun launchActivity() {
            val intent = Intent(context, LogsListActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context?.startActivity(intent)
        }

        fun setExceptionCallbackListener(exceptionCallback: ExceptionCallback) {
            this.exceptionCallback = exceptionCallback
        }

        fun deleteDataAfterTime(time: Long) {
            database?.crashLogDao()?.deleteAfterTime(time)
            database?.customLogDao()?.deleteAfterTime(time)
            database?.networkLogDao()?.deleteAfterTime(time)
            database?.eventsDao()?.deleteAfterTime(time)
        }

        fun deleteDataBeforeTime(time: Long) {
            database?.crashLogDao()?.deleteBeforeTime(time)
            database?.customLogDao()?.deleteBeforeTime(time)
            database?.networkLogDao()?.deleteBeforeTime(time)
            database?.eventsDao()?.deleteBeforeTime(time)
        }
    }


    private fun dumpCustomEvent(
            className: String, method: String, event: String,
            eventDescription: String, logType: String, status: String
    ) {
        if (hasInitialize) {
            database?.apply {
                val customLog = CustomLog(
                        className,
                        event,
                        method,
                        eventDescription,
                        System.currentTimeMillis().toString(),
                        user,
                        status
                )
                val rawId = customLogDao().insert(customLog)

                val eventsSequenceLog = EventsSequenceLog()
                eventsSequenceLog.logId = rawId
                eventsSequenceLog.logType = logType
                eventsSequenceLog.sessionId = sessionId
                eventsSequenceLog.time = System.currentTimeMillis().toString()

                eventsDao().insert(eventsSequenceLog)
            }
        }
    }


    fun debugEvent(tag: String, message: String, status: String = CustomLog.STATUS_INFO) {
        val stacktrace = Thread.currentThread().stackTrace
        val e = stacktrace[3]//maybe this number needs to be corrected
        val methodName = e.methodName

        Log.d(tag, message)
        dumpCustomEvent(className, methodName, tag, message, "DEBUG", status)
    }


    fun warningEvent(tag: String, message: String) {
        val stacktrace = Thread.currentThread().stackTrace
        val e = stacktrace[3]//maybe this number needs to be corrected
        val methodName = e.methodName

        Log.w("Asd", message)
        dumpCustomEvent(className, methodName, tag, message, "WARNING", CustomLog.STATUS_INFO)
    }


    fun dumpCustomEvent(
            event: String,
            eventDescription: String,
            status: String = CustomLog.STATUS_INFO
    ) {

        val stacktrace = Thread.currentThread().stackTrace
        val e = stacktrace[3]//maybe this number needs to be corrected
        val methodName = e.methodName

        dumpCustomEvent(className, methodName, event, eventDescription, "INFO", status)
    }

    fun dumpCrashEvent(throwable: Throwable) {
        if (hasInitialize) {
            database?.apply {

                val stackTraceElements = throwable.stackTrace
                val stringBuilder = StringBuilder()
                for (e in stackTraceElements) {
                    stringBuilder.append(e.toString()).append("\n")
                }

                val deviceInfoObject = JSONObject()

                deviceInfoObject.apply {
                    put("device", getDeviceModel())
                    put("sdk", getAndroidVersion())
                    put("isRooted", isRooted())
                    put("storage", String.format("%.2f gb", getAvailableInternalMemorySize()))
                    put("language", getDeviceLanguage())
                }

                context?.apply {
                    deviceInfoObject.apply {
                        put("usbDebuggingEnable", isUsbDebuggingEnabled())
                        put("memory", String.format("%.2f %%", getMemoryUsagePercentage()))
                        put("orientation", getOrientation())
                        put("battery", getBatteryPercentage())
                        put("isAppOnForeground", isAppOnForeground())
                    }
                }

                val crashLog = CrashLog(
                        throwable.toString(),
                        stringBuilder.toString(),
                        deviceInfoObject.toString(1),
                        System.currentTimeMillis().toString()
                )
                val rawId = crashLogDao().insert(crashLog)

                val eventsSequenceLog = EventsSequenceLog()
                eventsSequenceLog.logId = rawId
                eventsSequenceLog.logType = "ERROR"
                eventsSequenceLog.sessionId = sessionId
                eventsSequenceLog.time = System.currentTimeMillis().toString()
                eventsDao().insert(eventsSequenceLog)
            }
        }
    }

    fun dumpNetworkEvent(
            reqTime: String, requestUrl: String, requestMethod: String, requestParams: String,
            requestHeaders: String, responseTime: String, responseString: String,
            responseContentLength: String, responseCode: String, responseContentType: String
    ) {
        if (hasInitialize) {
            database?.apply {
                val requestLog = NetworkLog(
                        requestUrl,
                        requestMethod,
                        reqTime,
                        responseTime,
                        responseCode,
                        responseContentType,
                        responseContentLength,
                        requestParams,
                        requestHeaders,
                        responseString
                )
                val rawId = networkLogDao().insert(requestLog)

                val eventsSequenceLog = EventsSequenceLog()
                eventsSequenceLog.logId = rawId
                eventsSequenceLog.logType = "NETWORK"
                eventsSequenceLog.sessionId = sessionId
                eventsSequenceLog.time = responseTime
                eventsDao().insert(eventsSequenceLog)
            }
        }

    }


}
