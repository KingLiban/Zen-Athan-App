package com.example.athanapp.data

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
    suspend fun insert(prayerEntity: PrayerEntity)

    @Query("DELETE FROM prayers")
    suspend fun clearAllPrayers()

    @Query("SELECT * from prayers where readable = :date")
    fun getPrayer(date: String): Flow<PrayerEntity>

}