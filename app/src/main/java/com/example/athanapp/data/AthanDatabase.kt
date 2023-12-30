package com.example.athanapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.athanapp.network.PrayerData

@Database(entities = [PrayerData::class], version = 1, exportSchema = false)
abstract class AthanDatabase : RoomDatabase() {
    abstract fun prayerDao(): PrayerDao
    companion object {
        @Volatile
        private var Instance: AthanDatabase? = null

        fun getDatabase(context: Context): AthanDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AthanDatabase::class.java, "athan_database")
                    .fallbackToDestructiveMigration()
                    .createFromAsset("database/Prayer.db")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
