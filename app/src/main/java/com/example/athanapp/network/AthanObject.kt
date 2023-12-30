package com.example.athanapp.network

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity(tableName = "prayers")
@Serializable
data class PrayerData(
    @PrimaryKey(autoGenerate = false)
    val readable: String,
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


