package com.example.athanapp

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.example.athanapp.data.AppContainer
import com.example.athanapp.data.DefaultAppContainer
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.Calendar

class AthanApplication : Application() {

    private lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
        onLocationPermissionGranted()
    }

    fun onLocationPermissionGranted() {
        GlobalScope.launch(Dispatchers.Main) {
            var location = getLocation()
            initializeApp(location)
        }
    }
    private suspend fun getLocation(): Location? {
        return GlobalScope.async(Dispatchers.IO) {
            val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this@AthanApplication)
            return@async try {
                if (ActivityCompat.checkSelfPermission(
                        this@AthanApplication,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this@AthanApplication,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return@async null
                }

                val locationTask = fusedLocationProviderClient.lastLocation

                return@async Tasks.await<Location?>(locationTask)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }.await()
    }

    private suspend fun initializeApp(location: Location?) {
        if (location != null) {
            var currentYear = Calendar.getInstance().get(Calendar.YEAR)
            println(currentYear)
            val (latitude, longitude) = Pair(location.latitude, location.longitude)
            val years = listOf(currentYear, currentYear + 1, currentYear + 2)

            appContainer = DefaultAppContainer(
                this,
                years,
                latitude,
                longitude
            )
            updateDatabase(appContainer)
        } else {
            println("Location is null")
        }
    }

    private suspend fun updateDatabase(appContainer: AppContainer) {
        val prayerDao = appContainer.prayersRepository
        prayerDao.clearAllPrayers()

        val prayerEntities = appContainer.athanObjectRepository.getAthanObjects()

        for (prayerEntity in prayerEntities) {
            prayerDao.insertPrayer(prayerEntity)
        }
    }



}