package com.hb.logger.data.database.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.hb.logger.data.model.CrashLog
import com.hb.logger.data.model.CustomLog
import com.hb.logger.data.model.EventsSequenceLog
import com.hb.logger.data.model.NetworkLog

@Dao
interface EventsSequenceDao {
    @Query("select * from eventssequencelog")
    fun getAll(): List<EventsSequenceLog>

    @Insert
    fun insert(eventsLog: EventsSequenceLog): Long


    @RawQuery(observedEntities = [EventsSequenceLog::class, CrashLog::class, CustomLog::class, NetworkLog::class])
    fun getAllByFilter(supportSqlSQLiteQuery: SupportSQLiteQuery): DataSource.Factory<Int, EventsSequenceLog>

    @Query("delete  from eventssequencelog where time > :time")
    fun deleteAfterTime(time: Long)

    @Query("delete  from eventssequencelog where time < :time")
    fun deleteBeforeTime(time: Long)

}
