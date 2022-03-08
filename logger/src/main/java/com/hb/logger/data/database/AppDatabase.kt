package com.hb.logger.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hb.logger.data.database.dao.CrashLogDao
import com.hb.logger.data.database.dao.CustomLogDao
import com.hb.logger.data.database.dao.EventsSequenceDao
import com.hb.logger.data.database.dao.NetworkLogDao
import com.hb.logger.data.model.CrashLog
import com.hb.logger.data.model.CustomLog
import com.hb.logger.data.model.EventsSequenceLog
import com.hb.logger.data.model.NetworkLog

@Database(entities = [NetworkLog::class, CrashLog::class, CustomLog::class, EventsSequenceLog::class], version = 2)
public abstract class AppDatabase : RoomDatabase() {
    abstract fun networkLogDao(): NetworkLogDao

    abstract fun customLogDao(): CustomLogDao

    abstract fun crashLogDao(): CrashLogDao

    abstract fun eventsDao(): EventsSequenceDao


    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("alter table `CustomLog` ADD COLUMN status TEXT NOT NULL DEFAULT " + CustomLog.STATUS_INFO)
            }
        }
    }
}