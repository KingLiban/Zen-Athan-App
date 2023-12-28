package com.example.athanapp.data

import com.example.athanapp.network.AthanApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

interface AppContainer {
    val athanObjectRepository: AthanObjectRepository
}

class DefaultAppContainer(baseUrl: String) : AppContainer {

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl)
        .build()

    private val retrofitService: AthanApiService by lazy {
        retrofit.create(AthanApiService::class.java)
    }

    override val athanObjectRepository: AthanObjectRepository by lazy {
        AthanObjectNetworkRepository(retrofitService)

    }

}