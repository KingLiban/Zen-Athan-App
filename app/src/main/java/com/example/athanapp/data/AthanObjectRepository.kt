package com.example.athanapp.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.athanapp.network.AthanApiService
import com.example.athanapp.network.PrayerData
import com.example.athanapp.network.PrayerEntity


interface AthanObjectRepository {
    suspend fun getAthanObjects(): List<PrayerEntity>
}
class AthanObjectNetworkRepository(
    private val athanApiService: AthanApiService,
    private val years: List<Int>,
    private val latitude: Double,
    private val longitude: Double,
    private val context: Context
) : AthanObjectRepository {

    @RequiresApi(Build.VERSION_CODES.M)
    override suspend fun getAthanObjects(): List<PrayerEntity> {
        val list: MutableList<PrayerEntity> = mutableListOf()
        if (isNetworkConnected(context)) {
            for (year in years) {
                val prayerData = athanApiService.getPrayerData(year, latitude, longitude)
                list.addAll(mapToPrayerEntity(prayerData))
            }
        }
        return list
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun isNetworkConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
        return false
    }

    private fun mapToPrayerEntity(prayerData: PrayerData): List<PrayerEntity> {
        val entities = mutableListOf<PrayerEntity>()
        for ((_, dataList) in prayerData.data) {
            for (data in dataList) {
                entities.add(
                    PrayerEntity(
                        readable = data.date.readable,
                        fajr = data.timings.fajr,
                        sunrise = data.timings.sunrise,
                        dhuhr = data.timings.dhuhr,
                        asr = data.timings.asr,
                        maghrib = data.timings.maghrib,
                        isha = data.timings.isha
                    )
                )
            }
        }
        return entities
    }


}
