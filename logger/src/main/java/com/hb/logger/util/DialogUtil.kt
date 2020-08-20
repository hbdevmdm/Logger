package com.hb.logger.util

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.*
import android.widget.TextView
import android.widget.Toast

/**
 * This class contain alert message helper
 */
object DialogUtil {

    interface IL {
        fun onSuccess()

        fun onCancel(isNeutral: Boolean)
    }

    /**
     * This method is used to show alert dialog box for force close application
     *
     * @param context - Object of Context, context from where the activity is going
     * to start.
     * @param msg     - Message String that represents alert box message
     */
    fun showAlertDialogAction(context: Activity, msg: String,
                              il: IL, positiveBtnText: String, negativeBtnText: String) {
        try {
            val alertDialogBuilder = getBuilder(context)
            alertDialogBuilder.setMessage(msg)
            alertDialogBuilder.setPositiveButton(positiveBtnText
            ) { dialog, id ->
                il.onSuccess()
                dialog.dismiss()
            }

            alertDialogBuilder.setNegativeButton(negativeBtnText
            ) { dialog, which ->
                il.onCancel(false)
                dialog.dismiss()
            }

            alertDialogBuilder
                    .setOnKeyListener { dialog, keyCode, event ->
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            il.onCancel(false)
                            dialog.dismiss()
                        }
                        false
                    }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    /**
     * Function to get alert dialog builder
     * @param context instance of [Context]
     * @return instance of [AlertDialog.Builder]
     */
    private fun getBuilder(context: Context): AlertDialog.Builder {
        val alertDialogBuilder = AlertDialog.Builder(
                context)
        alertDialogBuilder.setCancelable(false)
        return alertDialogBuilder
    }
}
