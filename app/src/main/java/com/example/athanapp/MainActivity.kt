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
import androidx.compose.runtime.collectAsState
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.athanapp.ui.navigation.Athan
import com.example.athanapp.ui.navigation.AthanApp
import com.example.athanapp.ui.screens.PreferencesViewModel
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

        // checks permissions, if location granted then it sets the boolean to true
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                isLocationPermissionGranted =
                    permissions[COARSE_LOCATION]
                        ?: isLocationPermissionGranted
                isNotificationPermissionGranted =
                    permissions[NOTIFICATION]
                        ?: isNotificationPermissionGranted

                if (isLocationPermissionGranted) {
                    isLocationGranted = true
                }
            }

        isNotificationPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            NOTIFICATION
        ) == PackageManager.PERMISSION_GRANTED

        // Not ideal
        if (!isLocationPermissionGranted) {
            val permissionRequest: MutableList<String> = ArrayList()

            if (!isNotificationPermissionGranted) {
                permissionRequest.add(NOTIFICATION)
            }

            permissionLauncher.launch(permissionRequest.toTypedArray())
        } else {
            (application as AthanApplication).onLocationPermissionGranted()
        }

        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val userPreferencesRepository = (application as AthanApplication).userPreferencesRepository
                    val viewModel = PreferencesViewModel(userPreferencesRepository)
                    val uiState by viewModel.uiState.collectAsState()


                    if (!uiState.isStartScreen) {

                        AthanApp()
                    } else {
                        App()
                    }

                }
            }
        }
    }

    enum class AthanClass {
        Start,
        Go,
    }

    @Composable fun App(
    ) {
        val userPreferencesRepository = (application as AthanApplication).userPreferencesRepository
        val viewModel = PreferencesViewModel(userPreferencesRepository)
        val uiState by viewModel.uiState.collectAsState()

        val navController: NavHostController = rememberNavController()

        val startDestination = if (uiState.isStartScreen) AthanClass.Start.name else AthanClass.Go.name

        // If we don't need the start screen then still we can skip to getting the location
        // Also we need to make an option if the user turns off their wifi
        if (!uiState.isStartScreen)  (application as AthanApplication).onLocationPermissionGranted()
        // what does he even do?

        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            composable(route = AthanClass.Go.name) {
                AthanApp()
            }
            composable(route = AthanClass.Start.name) {
                startUp(viewModel, navController)
            }
        }
    }
    @Composable
    private fun startUp(
        viewModel: PreferencesViewModel,
        navController: NavHostController
    ) {

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
                        viewModel.requiresOnBoarding(false)
                        navController.navigate(AthanClass.Go.name)
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
            COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val isFineLocationPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!isCoarseLocationPermissionGranted || !isFineLocationPermissionGranted) {
            val permissionRequest: MutableList<String> = ArrayList()

            if (!isCoarseLocationPermissionGranted) {
                permissionRequest.add(COARSE_LOCATION)
            }

            if (!isFineLocationPermissionGranted) {
                permissionRequest.add(FINE_LOCATION)
            }
            permissionLauncher.launch(permissionRequest.toTypedArray())

            if (isCoarseLocationPermissionGranted) {
                isLocationGranted = true
            }
        } else {
            isLocationGranted = true
        }
    }

//    @Preview
//    @Composable
//    fun StartUpPreview() {
//        startUp(navController = NavHostController(this))
//    }

}
