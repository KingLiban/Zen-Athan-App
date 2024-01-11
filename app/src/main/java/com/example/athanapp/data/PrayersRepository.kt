package com.example.athanapp.data

import com.example.athanapp.network.PrayerData
import com.example.athanapp.network.PrayerEntity
import kotlinx.coroutines.flow.Flow

interface PrayersRepository {
    suspend fun insertPrayerOnline(prayerData: PrayerEntity)

    suspend fun insertPrayerOffline(prayerData: PrayerEntity)

    fun getPrayer(data: String): Flow<PrayerEntity>
}