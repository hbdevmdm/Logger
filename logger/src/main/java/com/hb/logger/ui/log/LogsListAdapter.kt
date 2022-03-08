package com.hb.logger.ui.log

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hb.logger.R
import com.hb.logger.data.model.CrashLog
import com.hb.logger.data.model.CustomLog
import com.hb.logger.data.model.EventsSequenceLog
import com.hb.logger.data.model.NetworkLog
import com.hb.logger.databinding.ItemLogsListCrashBinding
import com.hb.logger.databinding.ItemLogsListCustomBinding
import com.hb.logger.databinding.ItemLogsListNetworkBinding
import com.hb.logger.util.toFullDateTimeFormat
import java.util.*

class LogsListAdapter : PagedListAdapter<EventsSequenceLog, RecyclerView.ViewHolder>(DIFF_CALLBACK) {


    companion object {
        const val TYPE_NETWORK_LOG = 1
        const val TYPE_CUSTOM_LOG = 2
        const val TYPE_CRASH_LOG = 3

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<EventsSequenceLog>() {
            override fun areItemsTheSame(oldItem: EventsSequenceLog, newItem: EventsSequenceLog): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: EventsSequenceLog, newItem: EventsSequenceLog): Boolean {
                return oldItem == newItem
            }
            // Concert details may have changed if reloaded from the database,
            // but ID is fixed.

        }
    }

    interface OnRecyclerViewItem {
        fun onRecyclerViewItem(model: EventsSequenceLog)
    }

    var onRecyclerViewItem: OnRecyclerViewItem? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_NETWORK_LOG -> {
                val binding = ItemLogsListNetworkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                val holder = NetworkLogsViewHolder(binding)
                binding.root.setOnClickListener {
                    onRecyclerViewItem?.onRecyclerViewItem(getItem(holder.adapterPosition)!!)
                }
                holder
            }
            TYPE_CUSTOM_LOG -> {
                val binding = ItemLogsListCustomBinding.inflate(LayoutInflater.from(parent.context), parent, false)

                val holder = CustomLogsViewHolder(binding)
                binding.root.setOnClickListener {
                    onRecyclerViewItem?.onRecyclerViewItem(getItem(holder.adapterPosition)!!)
                }
                holder
            }
            else -> {
                val binding = ItemLogsListCrashBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                val holder = CrashLogsViewHolder(binding)
                binding.root.setOnClickListener {
                    onRecyclerViewItem?.onRecyclerViewItem(getItem(holder.adapterPosition)!!)
                }
                holder
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val i = getItem(position)
        i?.let {
            when {
                getItemViewType(position) == TYPE_NETWORK_LOG -> {
                    val networkLog: NetworkLog = i.getLogObj() as NetworkLog
                    (holder as NetworkLogsViewHolder).binding.tvWsStatusCode.text = networkLog.responseStatusCode
                    holder.binding.tvWsUrl.text = networkLog.url
                    holder.binding.tvWsType.text = networkLog.apiMethod

                    val requestDateToDisplay = networkLog.requestTime.toLong().toFullDateTimeFormat()
                    holder.binding.tvWsRequestTime.text = requestDateToDisplay
                    holder.binding.tvWsResponseDuration.text = networkLog.getTimeDurationToDisplay()

                    val color = when (networkLog.responseStatusCode) {
                        "200", "201", "202", "203", "204" -> {
                            R.color.colorGreen
                        }
                        else -> {
                            R.color.colorRed
                        }
                    }

                    holder.binding.llTag.setBackgroundColor(ContextCompat.getColor(holder.binding.root.context, color))

                }
                getItemViewType(position) == TYPE_CUSTOM_LOG -> {
                    val customLog: CustomLog = i.getLogObj() as CustomLog
                    (holder as CustomLogsViewHolder).binding.tvWsScreenType.text = customLog.className
                    holder.binding.tvWsAction.text = customLog.event
                    holder.binding.tvUserName.text = customLog.user
                    val date = customLog.time.toLong().toFullDateTimeFormat()
                    holder.binding.tvUserName.text = customLog.user
                    holder.binding.tvInfo.text = holder.binding.tvInfo.context.getString(R.string.logger_logType, getItem(holder.adapterPosition)!!.logType.substring(0, 1).toUpperCase(Locale.getDefault()), getItem(holder.adapterPosition)!!.logType.substring(1).toLowerCase(Locale.getDefault()))
                    holder.binding.tvWsReqTime.text = date
                    val color = when (customLog.status) {
                        CustomLog.STATUS_ERROR -> {
                            R.color.colorRed
                        }
                        CustomLog.STATUS_WARNING -> {
                            R.color.colorYellow
                        }
                        CustomLog.STATUS_SUCCESS -> {
                            R.color.colorGreen
                        }
                        else -> {
                            R.color.colorLightGray
                        }
                    }

                    holder.binding.llTag.setBackgroundColor(ContextCompat.getColor(holder.binding.root.context, color))


                }
                getItemViewType(position) == TYPE_CRASH_LOG -> {
                    val crashLog: CrashLog = i.getLogObj() as CrashLog
                    (holder as CrashLogsViewHolder).binding.tvCrashCause.text = crashLog.cause
                    holder.binding.tvErrorTime.text = crashLog.time.toLong().toFullDateTimeFormat()
                }
            }
        }

    }


    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)?.logType) {
            EventsSequenceLog.LOG_TYPE_NETWORK -> {
                TYPE_NETWORK_LOG
            }
            EventsSequenceLog.LOG_TYPE_ERROR -> {
                TYPE_CRASH_LOG
            }
            EventsSequenceLog.LOG_TYPE_INFO, EventsSequenceLog.LOG_TYPE_DEBUG, EventsSequenceLog.LOG_TYPE_WARNING -> {
                TYPE_CUSTOM_LOG
            }
            else -> -1
        }
    }


    override fun onCurrentListChanged(previousList: PagedList<EventsSequenceLog>?, currentList: PagedList<EventsSequenceLog>?) {
        super.onCurrentListChanged(previousList, currentList)
    }

    class NetworkLogsViewHolder(var binding: ItemLogsListNetworkBinding) : RecyclerView.ViewHolder(binding.root)
    class CustomLogsViewHolder(var binding: ItemLogsListCustomBinding) : RecyclerView.ViewHolder(binding.root)
    class CrashLogsViewHolder(var binding: ItemLogsListCrashBinding) : RecyclerView.ViewHolder(binding.root)

}