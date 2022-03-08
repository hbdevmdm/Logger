package com.hb.logger.ui.log

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.hb.logger.R
import com.hb.logger.data.model.*
import com.hb.logger.databinding.ActivityLogsListBinding
import com.hb.logger.databinding.DialogFilterBinding
import com.hb.logger.ui.log.crash.CrashLogDetailActivity
import com.hb.logger.ui.log.custom.CustomLogDetailActivity
import com.hb.logger.ui.log.network.NetworkLogDetailActivity
import com.hb.logger.ui.setting.LogSettingActivity
import com.hb.logger.util.CommonUtils
import java.util.*


class LogsListActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: ActivityLogsListBinding
    private lateinit var logsListAdapter: LogsListAdapter

    private var dialog: Dialog? = null
    private val filterState = FilterState()
    private val repository = LogListRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_logs_list)
        initComponent()
        handleUiEvents()
        attachObserver()
        performFilter()
    }

    /**
     * Manage ui events which binds as current layout
     * events like click events, text change, check change, etc..
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun handleUiEvents() {
        binding.ivSettings.setOnClickListener {
            startActivity(LogSettingActivity.createIntent(this))
        }

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.ivFilter.setOnClickListener {
            showFilterDialog()
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {


            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                filterState.keyword = binding.etSearch.text.toString()
                performFilter()
            }
        })
    }


    private fun performFilter() {
        repository.filterModel(filterState)
        binding.ivFilterApplied.visibility = if (filterState.isFilterApplied()) View.VISIBLE else View.GONE
    }

    private fun showFilterDialog() {
        if (dialog != null && dialog!!.isShowing)
            return

        dialog = Dialog(this, R.style.AlertDialogCustom)
        val dialogFilterBinding = DialogFilterBinding.inflate(LayoutInflater.from(this), null, false)
        CommonUtils.showDialog(dialog!!, dialogFilterBinding.root)

        dialogFilterBinding.tvType.text = filterState.filterType
        dialogFilterBinding.tvDate.text = filterState.dateTimeToDisplay

        dialogFilterBinding.ivClose.setOnClickListener {
            dialog?.dismiss()
        }

        dialogFilterBinding.tvType.setOnClickListener {
            val contextThemeWrapper = ContextThemeWrapper(this, R.style.PopupMenuOverlapAnchor)
            val popupMenu = PopupMenu(contextThemeWrapper, dialogFilterBinding.tvType)
            popupMenu.menuInflater.inflate(R.menu.menu_type, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                dialogFilterBinding.tvType.text = item.title
                true
            }
            popupMenu.show()
        }

        dialogFilterBinding.tvDate.setOnClickListener {

            val contextThemeWrapper = ContextThemeWrapper(this, R.style.PopupMenuOverlapAnchor)
            val popupMenu = PopupMenu(contextThemeWrapper, dialogFilterBinding.tvDate)
            popupMenu.menuInflater.inflate(R.menu.menu_date_time, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                dialogFilterBinding.tvDate.text = item.title
                true
            }
            popupMenu.show()
        }

        val filterChangeRunner = Runnable {
            filterState.filterType = dialogFilterBinding.tvType.text.toString()
            if (!TextUtils.isEmpty(dialogFilterBinding.tvDate.text.toString())) {
                val startDate = Calendar.getInstance(Locale.ENGLISH)
                when {
                    dialogFilterBinding.tvDate.text.toString() == getString(R.string.logger_from_today) -> {
                        startDate.set(Calendar.HOUR_OF_DAY, 0)
                        startDate.set(Calendar.MINUTE, 0)
                        startDate.set(Calendar.SECOND, 0)
                        startDate.set(Calendar.MILLISECOND, 0)
                    }
                    dialogFilterBinding.tvDate.text.toString() == getString(R.string.logger_from_yesterday) -> {
                        startDate.add(Calendar.DAY_OF_MONTH, -1)
                        startDate.set(Calendar.HOUR_OF_DAY, 0)
                        startDate.set(Calendar.MINUTE, 0)
                        startDate.set(Calendar.SECOND, 0)
                        startDate.set(Calendar.MILLISECOND, 0)
                    }
                    dialogFilterBinding.tvDate.text.toString() == getString(R.string.logger_from_last_week) -> {
                        startDate.set(Calendar.DAY_OF_WEEK, startDate.firstDayOfWeek + 1)
                        startDate.set(Calendar.HOUR_OF_DAY, 0)
                        startDate.set(Calendar.MINUTE, 0)
                        startDate.set(Calendar.SECOND, 0)
                        startDate.set(Calendar.MILLISECOND, 0)
                    }
                }
                val startTime = startDate.timeInMillis
                filterState.dateTime = startTime.toString()
            } else {
                filterState.dateTime = ""
            }
            filterState.dateTimeToDisplay = dialogFilterBinding.tvDate.text.toString()
            performFilter()
        }

        dialogFilterBinding.tvClear.setOnClickListener {
            dialogFilterBinding.tvDate.text = ""
            dialogFilterBinding.tvType.text = ""
        }

        dialogFilterBinding.tvApply.setOnClickListener {
            dialog?.dismiss()
            filterChangeRunner.run()
        }
        dialog?.show()
    }


    override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
        // use position to know the selected item
    }

    override fun onNothingSelected(arg0: AdapterView<*>) {

    }


    private fun initComponent() {

        binding.rvList.apply {
            layoutManager = LinearLayoutManager(this@LogsListActivity)
            logsListAdapter = LogsListAdapter()
            logsListAdapter.onRecyclerViewItem = object : LogsListAdapter.OnRecyclerViewItem {
                override fun onRecyclerViewItem(model: EventsSequenceLog) {
                    when {
                        model.log is NetworkLog -> startActivity(NetworkLogDetailActivity.createIntent(this@LogsListActivity, model.log as NetworkLog,model))
                        model.log is CustomLog -> startActivity(CustomLogDetailActivity.createIntent(this@LogsListActivity, model.log as CustomLog, model))
                        model.log is CrashLog -> startActivity(CrashLogDetailActivity.createIntent(this@LogsListActivity, model.log as CrashLog,model))
                    }
                }
            }
            binding.rvList.addItemDecoration(DividerItemDecoration(binding.rvList.context, DividerItemDecoration.VERTICAL))
            adapter = logsListAdapter
        }
    }


    private fun attachObserver() {
        repository.modelLiveData.observe(this, Observer {
            binding.tvNoData.visibility = if (it.size <= 0) View.VISIBLE else View.GONE
            logsListAdapter.submitList(it)
        })

    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, LogsListActivity::class.java)
        }
    }


}