package com.hb.logger.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.hb.logger.data.model.CrashLog

@Dao
interface CrashLogDao {

    @Query("select * from crashlog")
    fun getAll(): LiveData<MutableList<CrashLog>>

    @Insert
    fun insert(crashLog: CrashLog): Long

    @Query("select * from crashlog where id = :id")
    fun getCrashLogById(id: Long): CrashLog

    @Query("delete  from crashlog where time > :time")
    fun deleteAfterTime(time: Long)

    @Query("delete  from crashlog where time < :time")
    fun deleteBeforeTime(time: Long)
}
