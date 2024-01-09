package com.example.athanapp

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.app.ActivityCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.athanapp.data.AppContainer
import com.example.athanapp.data.DefaultAppContainer
import com.example.athanapp.data.UserPreferencesRepository
import com.example.athanapp.ui.screens.PreferencesViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Calendar
import java.util.Locale

private const val LAYOUT_PREFERENCE_NAME = "layout_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = LAYOUT_PREFERENCE_NAME
)


class AthanApplication : Application() {

    lateinit var userPreferencesRepository: UserPreferencesRepository
    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
        onLocationPermissionGranted()
        userPreferencesRepository = UserPreferencesRepository(dataStore)
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun onLocationPermissionGranted() {
        GlobalScope.launch(Dispatchers.Main) {
            val (location, city) = getLocation()
            initializeApp(location, city)
        }
    }
    @OptIn(DelicateCoroutinesApi::class)
    private suspend fun getLocation(): Pair<Location?, String?> {
        return GlobalScope.async(Dispatchers.IO) {
            val fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(this@AthanApplication)
            return@async try {
                if (ActivityCompat.checkSelfPermission(
                        this@AthanApplication,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this@AthanApplication,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return@async Pair(null, null)
                }

                val locationTask = fusedLocationProviderClient.lastLocation
                val location = Tasks.await<Location?>(locationTask)

                val cityName = if (location != null) {
                    val geocoder = Geocoder(this@AthanApplication, Locale.getDefault())
                    try {
                        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        addresses?.firstOrNull()?.locality
                    } catch (e: IOException) {
                        e.printStackTrace()
                        null
                    }
                } else {
                    null
                }

                Pair(location, cityName)
            } catch (e: Exception) {
                e.printStackTrace()
                Pair(null, null)
            }
        }.await()
    }


    private suspend fun initializeApp(location: Location?, city: String?) {
        if (location != null) {
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            println(currentYear)
            val (latitude, longitude) = Pair(location.latitude, location.longitude)
            val years = listOf(currentYear, currentYear + 1, currentYear + 2)

            appContainer = DefaultAppContainer(
                this,
                years,
                latitude,
                longitude,
            )
            updateDatabase(appContainer)
            val viewModel = PreferencesViewModel(userPreferencesRepository)
            viewModel.setCityName(city)
        } else {
            println("Location is null")
        }
    }

    private suspend fun updateDatabase(appContainer: AppContainer) {
        val prayerDao = appContainer.prayersRepository

        val prayerEntities = appContainer.athanObjectRepository.getAthanObjects()
        
        if (prayerEntities.isNotEmpty()) {
            for (prayerEntity in prayerEntities) {
                prayerDao.insertPrayer(prayerEntity)
            }
        }
        
    }



}
