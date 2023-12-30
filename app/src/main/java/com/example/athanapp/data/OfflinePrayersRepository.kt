package com.example.athanapp.data

import com.example.athanapp.network.PrayerData
import kotlinx.coroutines.flow.Flow

class OfflinePrayersRepository(private val prayerDao: PrayerDao): PrayersRepository {
    override suspend fun insertPrayer(prayerData: PrayerData) = prayerDao.insert(prayerData)
    override suspend fun clearAllPrayers() = prayerDao.clearAllPrayers()
    override fun getPrayer(data: String): Flow<PrayerData> = prayerDao.getPrayer(data)

}