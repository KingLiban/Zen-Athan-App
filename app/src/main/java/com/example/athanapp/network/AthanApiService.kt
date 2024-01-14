package com.example.athanapp.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AthanApiService {
    @GET("v1/calendar/{year}")
    suspend fun getPrayerData(
        @Path("year") year: Int,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    ): PrayerData

    @GET("v1/qibla/{latitude}/{longitude}")
    suspend fun getQiblaInfo(
        @Path("latitude") latitude: Double,
        @Path("longitude") longitude: Double
    ): ApiResponse

}

