package com.example.athanapp.data

import androidx.lifecycle.LiveData
import com.example.athanapp.network.PrayerData
import com.example.athanapp.network.PrayerEntity
import kotlinx.coroutines.flow.Flow

interface PrayersRepository {
    suspend fun insertPrayerOnline(prayerData: PrayerEntity)

    suspend fun insertPrayerOffline(prayerData: PrayerEntity)

    fun getPrayer(data: String): Flow<PrayerEntity>

    fun get30DaysPrayer(startDate: String, endDate: String): Flow<List<PrayerEntity>>
}