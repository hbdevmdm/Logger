package com.hb.logger.ui.setting

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import com.hb.logger.Logger
import com.hb.logger.R
import com.hb.logger.databinding.ActivityLogSettingsBinding
import com.hb.logger.util.*
import com.master.permissionhelper.PermissionHelper
import java.io.File
import java.io.PrintWriter

class LogSettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogSettingsBinding
    private var permissionHelper: PermissionHelper? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_log_settings)
        initComponent()
        handleUiEvents()
    }

    /**
     * Manage ui events which binds as current layout
     * events like click events, text change, check change, etc..
     */
    private fun handleUiEvents() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
        binding.switchLogging.setOnClickListener {
            if (binding.switchLogging.isChecked) {
                Logger.enableLogger()
                Toast.makeText(this@LogSettingActivity, "Logger enabled", Toast.LENGTH_LONG).show()
            } else {
                Logger.disableLogger()
                Toast.makeText(this@LogSettingActivity, "Logger disabled", Toast.LENGTH_LONG).show()
            }
        }
        binding.tvClearData.setOnClickListener {

            DialogUtil.showAlertDialogAction(this, getString(R.string.logger_clear_data_alert_message), object : DialogUtil.IL {
                override fun onSuccess() {
                    Logger.clearAllLogs()
                    Logger.resetSession()
                    Toast.makeText(this@LogSettingActivity, "All Log Data Cleared", Toast.LENGTH_LONG).show()
                    finish()
                }

                override fun onCancel(isNeutral: Boolean) {
                }

            }, getString(R.string.logger_clear), getString(R.string.logger_cancel))


        }
        binding.tvExportLog.setOnClickListener {
            AlertDialog.Builder(this@LogSettingActivity)
                    .setTitle("Export Log")
                    .setPositiveButton("As Database") { dialog, _ ->
                        ShareUtils.extractDatabaseAndShare(application){
                            ShareUtils.shareFile(application, it)
                        }
                        dialog.dismiss()
                    }
                    .setNegativeButton("As Log File") { dialog, _ ->
                        permissionHelper?.requestAll {
                            ShareUtils.extractAndShareLog(application){
                                ShareUtils.shareFile(application,it!!)
                            }
                        }
                        dialog.dismiss()
                    }.show()
        }
        binding.switchLogging.isChecked = Logger.isEnableLogger()
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionHelper?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun initComponent() {
        permissionHelper = PermissionHelper(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))
        permissionHelper?.denied { boolean ->
            if (boolean) {
                DialogUtil.showAlertDialogAction(this, getString(R.string.logger_permission_need_message), object : DialogUtil.IL {
                    override fun onSuccess() {
                        permissionHelper?.openAppDetailsActivity()
                    }

                    override fun onCancel(isNeutral: Boolean) {
                    }

                }, getString(R.string.logger_settings), getString(R.string.logger_cancel))
            }
        }
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, LogSettingActivity::class.java)
        }
    }
}