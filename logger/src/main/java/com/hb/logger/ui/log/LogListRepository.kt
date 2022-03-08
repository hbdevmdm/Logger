package com.hb.logger.ui.log

import androidx.arch.core.util.Function
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.PagedList
import androidx.paging.toLiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.hb.logger.Logger
import com.hb.logger.data.model.EventsSequenceLog
import com.hb.logger.data.model.FilterState


class LogListRepository {

    var modelFilter: MutableLiveData<FilterState> = MutableLiveData()
    var modelLiveData: LiveData<PagedList<EventsSequenceLog>>

    init {
        modelLiveData = Transformations.switchMap(modelFilter, Function<FilterState, LiveData<PagedList<EventsSequenceLog>>> {

            val selectClause = "select e.*,nl.url as 'url' from eventsSequenceLog e "
            val joinClause = "left join CustomLog c on c.id=e.logId left join CrashLog cl on cl.id=e.logId left join NetworkLog nl on nl.id=e.logId "
            val keywordWhereClause = "(nl.url like '%${it.keyword}%' OR nl.apiMethod like '%${it.keyword}%' OR nl.responseStatusCode like '%${it.keyword}%' OR cl.stackTrace like '%${it.keyword}%' OR c.className like '%${it.keyword}%' OR c.event like '%${it.keyword}%' OR c.detail like '%${it.keyword}%')"
            val typeWhereClause = when (it.filterType) {
                "Error" -> {
                    "e.logType = 'ERROR'"
                }
                "Info" -> {
                    "e.logType = 'INFO'"
                }
                "Warning" -> {
                    "e.logType = 'WARNING'"
                }
                "Debug" -> {
                    "e.logType = 'DEBUG'"
                }
                "Network" -> {
                    "e.logType = 'NETWORK'"
                }
                else -> "(e.logType like 'NETWORK' " +
                        "or e.logType like 'ERROR' " +
                        "or e.logType like 'INFO' " +
                        "or e.logType like 'WARNING' " +
                        "or e.logType like 'DEBUG')"
            }
            val dateTimeWhereClause = "(e.time >= '${it.dateTime}')"
            val orderByClause = " order by e.id DESC"
            val simpleSQLiteQuery = SimpleSQLiteQuery(selectClause + joinClause + "where " + keywordWhereClause + " AND " + typeWhereClause + " AND " + dateTimeWhereClause + orderByClause)
            return@Function Logger.database?.eventsDao()?.getAllByFilter(simpleSQLiteQuery)?.toLiveData(10)
        })

    }


    fun filterModel(filterState: FilterState) {
        modelFilter.postValue(filterState)
    }
}
