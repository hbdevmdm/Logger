package com.hb.logger.ui.log.crash

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.hb.logger.R
import com.hb.logger.data.model.CrashLog
import com.hb.logger.databinding.FragmentCrashInfoBinding
import com.hb.logger.util.CommonUtils.buildJavaScriptFunction
import com.hb.logger.util.toFullDateTimeFormat
import org.json.JSONObject

class CrashLogDetailFragment : Fragment() {

    private lateinit var binding: FragmentCrashInfoBinding
    private var crashLog: CrashLog? = null
    private var crashLogType: CrashLogType? = null


    enum class CrashLogType {
        INFO,
        DEVICE_INFO
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crashLog = arguments?.getParcelable<CrashLog>("crashLog")
        crashLogType = arguments?.getSerializable("type") as CrashLogType

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_crash_info, container, false)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.webviewCrashInfo.settings.javaScriptEnabled = true
        binding.webviewCrashInfo.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                fillDetails()
            }
        }
        binding.webviewCrashInfo.loadUrl("file:///android_asset/network.html")
    }

    private fun fillDetails() {
        when (crashLogType) {
            CrashLogType.INFO -> {

                val jsonObject = JSONObject()
                jsonObject.put("CAUSE", crashLog?.cause!!)
                jsonObject.put("DATE", crashLog?.time!!.toLong().toFullDateTimeFormat())
                jsonObject.put("STACKTRACE", crashLog?.stackTrace!!)

                binding.webviewCrashInfo.evaluateJavascript(buildJavaScriptFunction("fillCrashInfo",
                        jsonObject.toString()), null)
            }
            CrashLogType.DEVICE_INFO -> binding.webviewCrashInfo.evaluateJavascript(buildJavaScriptFunction("fillDeviceInfo",
                    crashLog?.deviceInfo ?: ""), null)
        }
    }


    companion object {
        fun createInstance(crashLog: CrashLog?, type: CrashLogType): CrashLogDetailFragment {
            val bundle = Bundle()
            bundle.putParcelable("crashLog", crashLog)
            bundle.putSerializable("type", type)
            val crashLogDetailFragment = CrashLogDetailFragment()
            crashLogDetailFragment.arguments = bundle
            return crashLogDetailFragment
        }
    }

}