package com.example.athanapp.data

import com.example.athanapp.network.PrayerData
import com.example.athanapp.network.PrayerEntity
import kotlinx.coroutines.flow.Flow

class OfflinePrayersRepository(private val prayerDao: PrayerDao): PrayersRepository {

    override suspend fun insertPrayer(prayerEntity: PrayerEntity) = prayerDao.insert(prayerEntity)

    override suspend fun clearAllPrayers() = prayerDao.clearAllPrayers()

    override fun getPrayer(data: String): Flow<PrayerEntity> = prayerDao.getPrayer(data)

}