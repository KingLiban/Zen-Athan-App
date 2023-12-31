package com.example.athanapp.network

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity(tableName = "prayers")
data class PrayerEntity(
    @PrimaryKey(autoGenerate = false)
    val readable: String,
    val fajr: String,
    val sunrise: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String,
)


@JsonClass(generateAdapter = true)
data class PrayerData(
    @Json(name = "data")
    val data: Map<String, List<Data>>
)


@JsonClass(generateAdapter = true)
data class Data(
    @Json(name = "date")
    val date: DateData,
    @Json(name = "timings")
    val timings: Timings
)

@JsonClass(generateAdapter = true)
data class DateData(
    @Json(name = "readable")
    val readable: String
)

@JsonClass(generateAdapter = true)
data class Timings(
    @Json(name = "Fajr")
    val fajr: String,
    @Json(name = "Sunrise")
    val sunrise: String,
    @Json(name = "Dhuhr")
    val dhuhr: String,
    @Json(name = "Asr")
    val asr: String,
    @Json(name = "Maghrib")
    val maghrib: String,
    @Json(name = "Isha")
    val isha: String,
)





