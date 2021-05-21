package com.hb.logger.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.hb.logger.data.model.NetworkLog

@Dao
interface NetworkLogDao {

    @Query("select * from networklog")
    fun getAll(): LiveData<MutableList<NetworkLog>>

    @Insert
    fun insert(networkLog: NetworkLog): Long

    @Query("select * from networklog where id = :id")
    fun getNetworkLogById(id: Long): NetworkLog

    @Query("delete  from networklog where requestTime > :time")
    fun deleteAfterTime(time: Long)

    @Query("delete  from networklog where requestTime < :time")
    fun deleteBeforeTime(time: Long)
}
