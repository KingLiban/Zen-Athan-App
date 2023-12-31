package com.example.athanapp.data

import android.content.Context
import com.example.athanapp.network.AthanApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

interface AppContainer {
    val athanObjectRepository: AthanObjectRepository
    val prayersRepository: PrayersRepository
}

class DefaultAppContainer(
    private val context: Context,
    private val years: List<Int>,
    private val latitude: Double,
    private val longitude: Double
) : AppContainer {

    private val baseUrl =
        "https://api.aladhan.com/"

    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val retrofitService: AthanApiService by lazy {
        retrofit.create(AthanApiService::class.java)
    }

    override val athanObjectRepository: AthanObjectRepository by lazy {
        AthanObjectNetworkRepository(
            retrofitService,
            years,
            latitude,
            longitude
        )
    }

    override val prayersRepository: PrayersRepository by lazy {
        OfflinePrayersRepository(AthanDatabase.getDatabase(context).prayerDao())
    }



}
