package com.example.athanapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.athanapp.network.PrayerData
import com.example.athanapp.network.PrayerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PrayerDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOffline(prayerEntity: PrayerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOnline(prayerEntity: PrayerEntity)

    @Query("SELECT * from prayers where readable = :date")
    fun getPrayer(date: String): Flow<PrayerEntity>

    @Query("SELECT * FROM prayers WHERE readable BETWEEN :startDate AND :endDate")
    fun get30DaysPrayer(startDate: String, endDate: String): Flow<List<PrayerEntity>>


}