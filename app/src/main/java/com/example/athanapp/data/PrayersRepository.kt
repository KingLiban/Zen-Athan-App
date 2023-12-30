package com.example.athanapp.data

import com.example.athanapp.network.PrayerData
import kotlinx.coroutines.flow.Flow

interface PrayersRepository {
    suspend fun insertPrayer(prayerData: PrayerData)

    suspend fun clearAllPrayers()

    fun getPrayer(data: String): Flow<PrayerData>
}