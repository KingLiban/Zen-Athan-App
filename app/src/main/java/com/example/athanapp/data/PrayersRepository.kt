package com.example.athanapp.data

import com.example.athanapp.network.PrayerData
import com.example.athanapp.network.PrayerEntity
import kotlinx.coroutines.flow.Flow

interface PrayersRepository {
    suspend fun insertPrayer(prayerData: PrayerEntity)

    suspend fun clearAllPrayers()

    fun getPrayer(data: String): Flow<PrayerEntity>
}