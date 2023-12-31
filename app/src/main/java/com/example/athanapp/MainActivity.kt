package com.example.athanapp

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.athanapp.data.AppContainer
import com.example.athanapp.data.DefaultAppContainer
import com.example.athanapp.ui.screens.Menu
import com.example.compose.AppTheme
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.Calendar


class MainActivity : ComponentActivity() {
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var isInternetPermissionGranted = false
    private var isLocationPermissionGranted = false
    private var isNotificationPermissionGranted = false

    private lateinit var appContainer: AppContainer

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            isInternetPermissionGranted = permissions[Manifest.permission.INTERNET] ?: isInternetPermissionGranted
            isLocationPermissionGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: isLocationPermissionGranted
            isNotificationPermissionGranted = permissions[Manifest.permission.POST_NOTIFICATIONS] ?: isNotificationPermissionGranted

            if (!isLocationPermissionGranted) {
                showLocationPermissionDeniedMessage()
            }
        }

        checkAndRequestPermissions()

        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Menu()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkAndRequestPermissions() {
        isInternetPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.INTERNET
        ) == PackageManager.PERMISSION_GRANTED

        isNotificationPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        isLocationPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!isLocationPermissionGranted) {
            val permissionRequest: MutableList<String> = ArrayList()
            if (!isInternetPermissionGranted) {
                permissionRequest.add(Manifest.permission.INTERNET)
            }

            if (!isLocationPermissionGranted) {
                permissionRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
            }

            if (!isNotificationPermissionGranted) {
                permissionRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }

            permissionLauncher.launch(permissionRequest.toTypedArray())
        } else {
            val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location ->
                    GlobalScope.launch(Dispatchers.Main) {
                        initializeApp(location)
                    }
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                }
        }
    }

    private fun showLocationPermissionDeniedMessage() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Location is needed")
        builder.setMessage("Location permission is required at least once for the app to work as intended. Please grant the permission in your settings.")
        builder.setPositiveButton("OK") { _, _ ->
            finish()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private suspend fun initializeApp(location: Location?) {
        if (location != null) {
            var currentYear = Calendar.getInstance().get(Calendar.YEAR)
            println(currentYear)
            val (latitude, longitude) = Pair(location.latitude, location.longitude)
            val years = listOf<Int>(currentYear, currentYear+1, currentYear+2)

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
