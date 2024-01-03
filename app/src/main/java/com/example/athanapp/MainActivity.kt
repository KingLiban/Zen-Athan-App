package com.example.athanapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.athanapp.ui.screens.QiblaMenu
import com.example.athanapp.ui.theme.Typography
import com.example.compose.AppTheme


class MainActivity : ComponentActivity() {

    companion object {
        private const val COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
        private const val FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        private const val NOTIFICATION = Manifest.permission.POST_NOTIFICATIONS
    }

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var isLocationPermissionGranted = false
    private var isNotificationPermissionGranted = false

    private var isLocationGranted by mutableStateOf(false)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                isLocationPermissionGranted =
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION]
                        ?: isLocationPermissionGranted
                isNotificationPermissionGranted =
                    permissions[Manifest.permission.POST_NOTIFICATIONS]
                        ?: isNotificationPermissionGranted

            }

        isNotificationPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            NOTIFICATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!isLocationPermissionGranted) {
            val permissionRequest: MutableList<String> = ArrayList()

            if (!isNotificationPermissionGranted) {
                permissionRequest.add(NOTIFICATION)
            }

            permissionLauncher.launch(permissionRequest.toTypedArray())
        }

        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StartUp()

                }
            }
        }
    }
    @Composable
    fun StartUp() {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.rectangle),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(50.dp)
                    .fillMaxSize(),
            ) {
                Text(
                    text = "Welcome",
                    style = Typography.displayLarge,
                    color = Color.White
                )

                Spacer(modifier = Modifier.padding(7.dp))
                Text(
                    text = "Please give a moment to get everything started.",
                    style = Typography.displayMedium,
                    color = Color(156, 180, 216),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.padding(48.dp))

                Button(onClick = { requestLocationAccess() }) {
                    Text(
                        text = "Give Location Access",
                        style = Typography.displayMedium,
                    )
                }

                Spacer(modifier = Modifier.padding(30.dp))
                Image(
                    painter = painterResource(id = R.drawable.logo_placeholder),
                    contentDescription = "",
                    modifier = Modifier.size(136.dp)
                )

            }

        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            if (isLocationGranted) {
                (application as AthanApplication).onLocationPermissionGranted()

                Button(
                    onClick = {
                        // Handle continue button click action here
                    },
                    modifier = Modifier
                        .padding(8.dp)
                ) {
                    Text("Continue")
                }
            }
        }

    }

    private fun requestLocationAccess() {
        val isCoarseLocationPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val isFineLocationPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!isCoarseLocationPermissionGranted || !isFineLocationPermissionGranted) {
            val permissionRequest: MutableList<String> = ArrayList()

            if (!isCoarseLocationPermissionGranted) {
                permissionRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
            }

            if (!isFineLocationPermissionGranted) {
                permissionRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            permissionLauncher.launch(permissionRequest.toTypedArray())
        } else {
            isLocationGranted = true
        }
    }

    @Preview
    @Composable
    fun StartUpPreview() {
        StartUp()
    }

}
