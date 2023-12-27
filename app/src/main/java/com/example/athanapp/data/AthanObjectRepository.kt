package com.example.athanapp.data

import com.example.athanapp.network.AthanApiService
import com.example.athanapp.network.PrayerData

interface AthanObjectRepository {
    suspend fun getAmphibiansObject(): List<PrayerData>
}

class AthanObjectNetworkRepository(
    private val amphibiansApiService: AthanApiService
) : AthanObjectRepository {
    override suspend fun getAmphibiansObject(): List<PrayerData> = amphibiansApiService.getPrayerData()

}