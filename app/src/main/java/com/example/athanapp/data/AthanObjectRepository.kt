package com.example.athanapp.data

import com.example.athanapp.network.AthanApiService
import com.example.athanapp.network.PrayerData
import com.example.athanapp.network.PrayerEntity

interface AthanObjectRepository {
    suspend fun getAthanObjects(): List<PrayerEntity>
}
class AthanObjectNetworkRepository(
    private val athanApiService: AthanApiService,
    private val years: List<Int>,
    private val latitude: Double,
    private val longitude: Double
) : AthanObjectRepository {

    override suspend fun getAthanObjects(): List<PrayerEntity> {
        val list: MutableList<PrayerEntity> = mutableListOf()
        for (year in years) {
            val prayerData = athanApiService.getPrayerData(year, latitude, longitude)
            list.addAll(mapToPrayerEntity(prayerData))
        }
        return list
    }


    private fun mapToPrayerEntity(prayerData: PrayerData): List<PrayerEntity> {
        val entities = mutableListOf<PrayerEntity>()
        for ((_, dataList) in prayerData.data) {
            for (data in dataList) {
                entities.add(
                    PrayerEntity(
                        readable = data.date.readable,
                        fajr = data.timings.fajr,
                        sunrise = data.timings.sunrise,
                        dhuhr = data.timings.dhuhr,
                        asr = data.timings.asr,
                        maghrib = data.timings.maghrib,
                        isha = data.timings.isha
                    )
                )
            }
        }
        return entities
    }


}
