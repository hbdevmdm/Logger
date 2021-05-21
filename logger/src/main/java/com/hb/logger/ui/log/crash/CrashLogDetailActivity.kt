package com.hb.logger.ui.log.crash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.google.android.material.tabs.TabLayout
import com.hb.logger.R
import com.hb.logger.data.model.CrashLog
import com.hb.logger.data.model.EventsSequenceLog
import com.hb.logger.databinding.ActivityCrashTypeDetailBinding
import com.hb.logger.ui.log.core.LogDetailBaseActivity

class CrashLogDetailActivity : LogDetailBaseActivity() {

    private lateinit var binding: ActivityCrashTypeDetailBinding
    private lateinit var crashLog: CrashLog
    private lateinit var eventsSequenceLog: EventsSequenceLog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_crash_type_detail)
        crashLog = intent.getParcelableExtra("crashLog")
        eventsSequenceLog = intent.getParcelableExtra("eventSequenceLog")
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
            shareLog(crashLog.generateStandardLog(eventsSequenceLog), "ERROR")
        }
    }


    private fun initComponent() {
        val adapter = CrashLogPagerAdapter(supportFragmentManager)
        adapter.addFragment(CrashLogDetailFragment.createInstance(crashLog,
                CrashLogDetailFragment.CrashLogType.INFO), "Info")
        adapter.addFragment(CrashLogDetailFragment.createInstance(crashLog,
                CrashLogDetailFragment.CrashLogType.DEVICE_INFO), "Device Info")
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
        fun createIntent(context: Context, crashLog: CrashLog, eventsSequenceLog: EventsSequenceLog): Intent {
            val intent = Intent(context, CrashLogDetailActivity::class.java)
            intent.putExtra("eventSequenceLog", eventsSequenceLog)
            intent.putExtra("crashLog", crashLog)
            return intent
        }
    }
}