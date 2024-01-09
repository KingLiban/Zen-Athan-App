package com.example.athanapp.data

import android.content.Context
import android.net.ConnectivityManager
import com.example.athanapp.network.AthanApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
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
    private val longitude: Double,
) : AppContainer {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val baseUrl = "https://api.aladhan.com/"

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val retrofitService: AthanApiService by lazy {
        retrofit.create(AthanApiService::class.java)
    }

    private val connectivityManager: ConnectivityManager by lazy {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    override val athanObjectRepository: AthanObjectRepository by lazy {
        AthanObjectNetworkRepository(
            retrofitService,
            years,
            latitude,
            longitude,
            context
        )
    }

    override val prayersRepository: PrayersRepository by lazy {
        OfflinePrayersRepository(AthanDatabase.getDatabase(context).prayerDao())
    }
}
