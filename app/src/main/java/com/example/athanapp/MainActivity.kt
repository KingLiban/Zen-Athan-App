package com.example.athanapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.athanapp.ui.navigation.AthanApp
import com.example.athanapp.ui.screens.Menu
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

    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                isLocationPermissionGranted =
                    permissions[COARSE_LOCATION] ?: isLocationPermissionGranted
                    permissions[FINE_LOCATION] ?: isLocationPermissionGranted
                isNotificationPermissionGranted =
                    permissions[NOTIFICATION] ?: isNotificationPermissionGranted

                if (isLocationPermissionGranted) {
                    isLocationGranted = true
                }
            }

        isNotificationPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            NOTIFICATION
        ) == PackageManager.PERMISSION_GRANTED

        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    App()
                }
            }
        }
    }

    enum class AppStatus {
        LOADING,
        START,
        GO
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Composable fun App() {
        val userPreferencesRepository = (application as AthanApplication).userPreferencesRepository
        val viewModel = PreferencesViewModel(userPreferencesRepository)
        val uiState by viewModel.uiState.collectAsState()

        val navController: NavHostController = rememberNavController()

        var appStatus by remember { mutableStateOf(AppStatus.LOADING) }

        LaunchedEffect(uiState) {
            appStatus = when {
                uiState.isStartScreen -> AppStatus.START
                else -> AppStatus.GO
            }
        }

        val startDestination = when (appStatus) {
            AppStatus.LOADING -> AppStatus.LOADING.name
            AppStatus.START -> AppStatus.START.name
            AppStatus.GO -> AppStatus.GO.name
        }

        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            composable(route = AppStatus.LOADING.name) {
                Menu()
            }
            composable(route = AppStatus.GO.name) {
                AthanApp()
            }
            composable(route = AppStatus.START.name) {
                StartUp(viewModel, navController)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Composable
    private fun StartUp(
        viewModel: PreferencesViewModel,
        navController: NavHostController
    ) {
        val permissionRequest: MutableList<String> = ArrayList()
        if (!isNotificationPermissionGranted) {
            permissionRequest.add(NOTIFICATION)
        }
        permissionLauncher.launch(permissionRequest.toTypedArray())

        val context = LocalContext.current

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
            var isNetworkConnected by remember {
                mutableStateOf(isNetworkConnected(context))
            }

            if (isLocationGranted && isNetworkConnected) {
                (application as AthanApplication).onLocationPermissionGranted()
                Button(
                    onClick = {
                        viewModel.requiresOnBoarding(false)
                        navController.navigate(AppStatus.GO.name)
                    },
                    modifier = Modifier
                        .padding(8.dp)
                ) {
                    Text("Continue")
                }
            } else if (!isNetworkConnected){
                AlertDialog(
                    onDismissRequest = { /*TODO*/ },
                    title = { Text("No Internet Connection") },
                    text = { Text("Please turn on your internet connection to continue.") },
                    confirmButton = {
                        Button(onClick = {
                            isNetworkConnected = isNetworkConnected(context)
                        }) {
                            Text("OK")
                        }
                    }
                )
            }
        }
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
