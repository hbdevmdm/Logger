package com.hb.logger.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.hb.logger.data.model.CustomLog

@Dao
interface CustomLogDao {

    @Query("select * from customlog")
    fun getAll(): LiveData<MutableList<CustomLog>>

    @Insert
    fun insert(customLog: CustomLog): Long

    @Query("select * from customlog where id = :id")
    fun getCustomLogById(id: Long): CustomLog
}
