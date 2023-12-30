package com.example.athanapp.data

import com.example.athanapp.network.AthanApiService
import com.example.athanapp.network.PrayerData

interface AthanObjectRepository {
    suspend fun getAthanObject(): List<PrayerData>
}
class AthanObjectNetworkRepository(
    private val athanApiService: AthanApiService
) : AthanObjectRepository {

    override suspend fun getAthanObject(): List<PrayerData> = athanApiService.getPrayerData()

}