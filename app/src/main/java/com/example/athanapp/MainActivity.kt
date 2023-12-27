package com.example.athanapp

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.example.athanapp.data.AppContainer
import com.example.athanapp.data.DefaultAppContainer
import com.example.athanapp.ui.screens.HomeBody
import com.example.athanapp.ui.screens.Menu
import com.example.compose.AppTheme

class MainActivity : ComponentActivity() {
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var isInternetPermissionGranted = false
    private var isLocationPermissionGranted = false
    private var isNotificationPermissionGranted = false

    private var appContainer: AppContainer = DefaultAppContainer()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appContainer = DefaultAppContainer()

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
            initializeApp()
        }
    }

    private fun initializeApp() {
//        appContainer.up
    }

    private fun fetchCoarseLocation(): String {
        val locationManager = getSystemService(LOCATION_SERVICE) as? LocationManager
        val coarseLocationProvider = LocationManager.NETWORK_PROVIDER
        var coarseLocation = "Not available"

        try {
            val lastKnownLocation = locationManager?.getLastKnownLocation(coarseLocationProvider)
            if (lastKnownLocation != null) {
                coarseLocation = "Latitude: ${lastKnownLocation.latitude}, Longitude: ${lastKnownLocation.longitude}"
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }

        return coarseLocation
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

}
