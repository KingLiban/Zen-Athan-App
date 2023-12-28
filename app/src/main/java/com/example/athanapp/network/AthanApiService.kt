package com.example.athanapp.network

import retrofit2.http.GET

interface AthanApiService {
    @GET("Prayers")
    suspend fun getPrayerData(): List<PrayerData>
}