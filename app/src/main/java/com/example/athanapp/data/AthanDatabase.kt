package com.example.athanapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.athanapp.network.PrayerEntity

@Database(entities = [PrayerEntity::class], version = 1, exportSchema = false)
abstract class AthanDatabase : RoomDatabase() {
    abstract fun prayerDao(): PrayerDao
    companion object {
        @Volatile
        private var INSTANCE: AthanDatabase? = null

        fun getDatabase(context: Context): AthanDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context, AthanDatabase::class.java, "athan_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
