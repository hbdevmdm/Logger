package com.hb.logger.ui.log.custom

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import com.hb.logger.R
import com.hb.logger.data.model.CustomLog
import com.hb.logger.data.model.EventsSequenceLog
import com.hb.logger.databinding.ActivityCustomLogDetailBinding
import com.hb.logger.ui.log.core.LogDetailBaseActivity
import com.hb.logger.util.CommonUtils
import com.hb.logger.util.optNA
import com.hb.logger.util.quoted
import com.hb.logger.util.toFullDateTimeFormat

class CustomLogDetailActivity : LogDetailBaseActivity() {

    private lateinit var binding: ActivityCustomLogDetailBinding
    private lateinit var customLog: CustomLog
    private lateinit var eventsSequenceLog: EventsSequenceLog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_custom_log_detail)
        customLog = intent.getParcelableExtra("customLog")!!
        eventsSequenceLog = intent.getParcelableExtra("eventSequenceLog")!!

        handleUiEvents()
        setData()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setData() {
        binding.webviewCustomInfo.settings.javaScriptEnabled = true
        binding.webviewCustomInfo.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                fillDetails()
            }
        }
        binding.webviewCustomInfo.loadUrl("file:///android_asset/network.html")
    }

    private fun fillDetails() {
        binding.webviewCustomInfo.evaluateJavascript(CommonUtils.buildJavaScriptFunction("fillEventInfo",
                eventsSequenceLog.logType.quoted(),
                customLog.method.quoted(),
                customLog.className.quoted(),
                customLog.event.optNA().quoted(),
                customLog.detail.optNA().quoted(),
                customLog.user.optNA().quoted(),
                customLog.time.toLong().toFullDateTimeFormat().quoted()),
                null)
    }


    companion object {
        fun createIntent(context: Context, customLog: CustomLog, eventsSequenceLog: EventsSequenceLog): Intent {
            val intent = Intent(context, CustomLogDetailActivity::class.java)
            intent.putExtra("customLog", customLog)
            intent.putExtra("eventSequenceLog", eventsSequenceLog)
            return intent
        }
    }

    /**
     * Manage ui events which binds as current layout
     * events like click events, text change, check change, etc..
     */
    private fun handleUiEvents() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
        binding.ivShareLog.setOnClickListener {
            shareLog(customLog.generateStandardLog(eventsSequenceLog), eventsSequenceLog.logType)
        }
    }

}