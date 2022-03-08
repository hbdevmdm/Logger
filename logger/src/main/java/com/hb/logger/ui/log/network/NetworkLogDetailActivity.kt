package com.hb.logger.ui.log.network

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.google.android.material.tabs.TabLayout
import com.hb.logger.R
import com.hb.logger.data.model.EventsSequenceLog
import com.hb.logger.data.model.NetworkLog
import com.hb.logger.databinding.ActivityNetworkTypeDetailBinding
import com.hb.logger.ui.log.core.LogDetailBaseActivity

class NetworkLogDetailActivity : LogDetailBaseActivity() {

    private lateinit var binding: ActivityNetworkTypeDetailBinding
    private var networkLog: NetworkLog? = null
    private lateinit var eventsSequenceLog: EventsSequenceLog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_network_type_detail)
        networkLog = intent.getParcelableExtra<NetworkLog>("networkLog")!!
        eventsSequenceLog = intent.getParcelableExtra("eventSequenceLog")!!
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
        binding.ivShareLog.setOnClickListener {
            shareLog(networkLog?.generateStandardLog(eventsSequenceLog)!!, "NETWORK")
        }
    }


    private fun initComponent() {
        val adapter = NetworkLogPagerAdapter(supportFragmentManager)
        adapter.addFragment(NetworkLogDetailFragment.createInstance(networkLog,
                NetworkLogDetailFragment.NetworkLogType.INFO), "Info")
        adapter.addFragment(NetworkLogDetailFragment.createInstance(networkLog,
                NetworkLogDetailFragment.NetworkLogType.REQUEST), "Request")
        adapter.addFragment(NetworkLogDetailFragment.createInstance(networkLog,
                NetworkLogDetailFragment.NetworkLogType.RESPONSE), "Response")
        binding.viewPager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        binding.viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout))

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

    }

    companion object {
        fun createIntent(context: Context, networkLog: NetworkLog, eventsSequenceLog: EventsSequenceLog): Intent {
            val intent = Intent(context, NetworkLogDetailActivity::class.java)
            intent.putExtra("eventSequenceLog", eventsSequenceLog)
            intent.putExtra("networkLog", networkLog)
            return intent
        }
    }


}