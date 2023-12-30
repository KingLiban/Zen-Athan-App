package com.example.athanapp.data

import android.content.Context
import com.example.athanapp.network.AthanApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

interface AppContainer {
    val athanObjectRepositories: List<AthanObjectRepository>
    val prayersRepository: PrayersRepository
}

class DefaultAppContainer(
    private val baseUrls: ArrayList<String>,
    private val context: Context
) : AppContainer {

    private val retrofitServices: List<AthanApiService> = baseUrls.map { baseUrl ->
        val retrofit: Retrofit = Retrofit.Builder()
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .baseUrl(baseUrl)
            .build()

        retrofit.create(AthanApiService::class.java)
    }

    override val athanObjectRepositories: List<AthanObjectRepository> =
        retrofitServices.map { retrofitService ->
            AthanObjectNetworkRepository(retrofitService)
        }
    override val prayersRepository: PrayersRepository by lazy {
        OfflinePrayersRepository(AthanDatabase.getDatabase(context).prayerDao())
    }


}
