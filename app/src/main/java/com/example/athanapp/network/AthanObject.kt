package com.example.athanapp.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//import retrofit2.http.GET

@Serializable
data class Timings(
    @SerialName(value = "Fajr")
    val fajr: String,
    @SerialName(value = "Sunrise")
    val sunrise: String,
    @SerialName(value = "Dhuhr")
    val dhuhr: String,
    @SerialName(value = "Asr")
    val asr: String,
    @SerialName(value = "Maghrib")
    val maghrib: String,
    @SerialName(value = "Isha")
    val isha: String,
)

data class PrayerData(
    val timings: Timings,
    val date: Date
)

data class Date(
    val readable: String,
    val timestamp: String
)


