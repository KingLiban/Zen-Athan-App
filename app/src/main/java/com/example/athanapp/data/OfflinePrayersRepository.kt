package com.example.athanapp.data

import com.example.athanapp.network.PrayerEntity
import kotlinx.coroutines.flow.Flow

class OfflinePrayersRepository(private val prayerDao: PrayerDao): PrayersRepository {

    override suspend fun insertPrayerOnline(prayerData: PrayerEntity) = prayerDao.insertOnline(prayerData)
    override suspend fun insertPrayerOffline(prayerData: PrayerEntity) = prayerDao.insertOffline(prayerData)
    override fun getPrayer(data: String): Flow<PrayerEntity> = prayerDao.getPrayer(data)
    override fun get30DaysPrayer(startDate: String, endDate: String): Flow<List<PrayerEntity>> = prayerDao.get30DaysPrayer(startDate, endDate)

}