package com.hb.logger.ui.log.network

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
import com.hb.logger.data.model.NetworkLog
import com.hb.logger.databinding.FragmentNetworkInfoBinding
import com.hb.logger.util.CommonUtils.buildJavaScriptFunction
import com.hb.logger.util.optNA
import com.hb.logger.util.quoted
import com.hb.logger.util.toFullDateTimeFormat

class NetworkLogDetailFragment : Fragment() {

    private lateinit var binding: FragmentNetworkInfoBinding
    private lateinit var networkLog: NetworkLog
    private var networkLogType: NetworkLogType? = null

    enum class NetworkLogType {
        INFO,
        REQUEST,
        RESPONSE
    }

    companion object {
        fun createInstance(networkLog: NetworkLog?, type: NetworkLogType): NetworkLogDetailFragment {
            val bundle = Bundle()
            bundle.putParcelable("networkLog", networkLog)
            bundle.putSerializable("type", type)
            val networkInfoFragment = NetworkLogDetailFragment()
            networkInfoFragment.arguments = bundle
            return networkInfoFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        networkLog = arguments?.getParcelable<NetworkLog>("networkLog")!!
        networkLogType = arguments?.getSerializable("type") as NetworkLogType


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_network_info, container, false)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.webviewNetworkInfo.settings.javaScriptEnabled = true
        binding.webviewNetworkInfo.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                fillDetail()
            }
        }
        binding.webviewNetworkInfo.loadUrl("file:///android_asset/network.html")
    }

    private fun fillDetail() {
        when (networkLogType) {

            NetworkLogType.INFO -> binding.webviewNetworkInfo.evaluateJavascript(buildJavaScriptFunction("fillInfo",
                    networkLog.url.optNA().quoted(),
                    networkLog.apiMethod.optNA().quoted(),
                    networkLog.responseStatusCode.optNA().quoted(),
                    networkLog.requestTime.toLong().toFullDateTimeFormat().quoted(),
                    networkLog.responseTime.toLong().toFullDateTimeFormat().quoted(),
                    networkLog.getTimeDurationToDisplay().quoted()),
                    null)
            NetworkLogType.REQUEST -> binding.webviewNetworkInfo.evaluateJavascript(buildJavaScriptFunction("fillRequest",
                    networkLog.headerLog, networkLog.paramLog), null)
            NetworkLogType.RESPONSE -> binding.webviewNetworkInfo.evaluateJavascript(buildJavaScriptFunction("fillResponse",
                    networkLog.response), null)
        }
    }

}