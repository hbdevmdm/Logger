package com.hb.logger.util

import android.app.Dialog
import android.content.Context
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.hb.logger.R


object CommonUtils {

    fun getDateDifference(startDate: Long, endDate: Long): Time {
        var different = endDate - startDate
        val secondsInMilli: Long = 1000
        val minutesInMilli = secondsInMilli * 60
        val hoursInMilli = minutesInMilli * 60
        val daysInMilli = hoursInMilli * 24

        val elapsedDays = different / daysInMilli
        different %= daysInMilli

        val elapsedHours = different / hoursInMilli
        different %= hoursInMilli

        val elapsedMinutes = different / minutesInMilli
        different %= minutesInMilli

        val elapsedSeconds = different / secondsInMilli

        val elapsedMilliSeconds = different % secondsInMilli

        return Time(elapsedHours, elapsedMinutes, elapsedSeconds, elapsedMilliSeconds)
    }

    /**
     * Show dialog.
     *
     * @param dialog the dialog
     * @param resId  the res id
     */
    fun showDialog(dialog: Dialog, view: View) {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(view)
        dialog.window!!.attributes.windowAnimations = R.style.AlertDialogCustom
        val mWindowLayoutParams = WindowManager.LayoutParams()
        val mWindow = dialog.window
        mWindowLayoutParams.copyFrom(mWindow!!.attributes)
        mWindowLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT

        val windowDimension = view.context.getWindowDimension()
        mWindowLayoutParams.width = windowDimension.x - windowDimension.x / 10


        mWindow.attributes = mWindowLayoutParams
        mWindow.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
    }

    data class Time(val hour: Long, val minute: Long, val second: Long, val milliSecond: Long)

    fun buildJavaScriptFunction(methodName: String, vararg values: String): String {
        return "javascript:" + methodName + "(" + TextUtils.join(",", values) + ")"
    }


    fun getApplicationName(context: Context):String {
      return  context.applicationInfo.loadLabel(context.packageManager).toString()
    }


}
