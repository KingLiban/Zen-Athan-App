package com.example.athanapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.provider.Settings
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.athanapp.ui.navigation.AthanApp
import com.example.athanapp.ui.screens.AthanViewModel
import com.example.athanapp.ui.screens.PreferencesViewModel
import com.example.athanapp.ui.screens.SensorViewModel
import com.example.athanapp.ui.screens.Splash
import com.example.athanapp.ui.theme.Typography
import com.example.athanapp.ui.theme.AppTheme
import java.text.DateFormat
import java.util.Date
import java.util.Locale


class MainActivity : ComponentActivity(), SensorEventListener {
    private companion object {
        private const val COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
        private const val FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        private const val NOTIFICATION = Manifest.permission.POST_NOTIFICATIONS
        @RequiresApi(Build.VERSION_CODES.S)
        private const val ALARM = Manifest.permission.SCHEDULE_EXACT_ALARM
    }

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    private var isLocationPermissionGranted = false
    private var isNotificationPermissionGranted = false
    private var isAlarmPermissionGranted = false

    private var mSensorManager: SensorManager? = null
    private var mAccelerometer: Sensor? = null
    private var mMagnetometer: Sensor? = null
    private var mGravity: Sensor? = null

    private var haveGravity = false
    private var haveAccelerometer = false
    private var haveMagnetometer = false

    private var gData = FloatArray(3)
    private var mData = FloatArray(3)
    private val rMat = FloatArray(9)
    private val iMat = FloatArray(9)
    private val orientation = FloatArray(3)

    private var sensorViewModel = SensorViewModel()
    private val openAlertDialog = mutableStateOf(false)

    private var isReady = false

    @SuppressLint("SuspiciousIndentation", "SourceLockedOrientationActivity")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                isLocationPermissionGranted =
                    permissions[COARSE_LOCATION] == true || permissions[FINE_LOCATION] == true || isLocationPermissionGranted
                isNotificationPermissionGranted =
                    permissions[NOTIFICATION] ?: isNotificationPermissionGranted
                isAlarmPermissionGranted =
                    permissions[ALARM] ?: isAlarmPermissionGranted
                if (isLocationPermissionGranted) {
                    println("Location permission granted")
                    (application as AthanApplication).onLocationPermissionGranted()
                }
                if (isNotificationPermissionGranted) {
                    println("Notification permission granted")
                }
            }

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT // scuff for tablets and folded phones

        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mGravity = mSensorManager!!.getDefaultSensor(Sensor.TYPE_GRAVITY)
        haveGravity = mSensorManager!!.registerListener(this, mGravity, SensorManager.SENSOR_DELAY_GAME)
        mAccelerometer = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        haveAccelerometer = mSensorManager!!.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME)
        mMagnetometer = mSensorManager!!.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        haveMagnetometer = mSensorManager!!.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME)

        if (haveGravity) {
            mSensorManager!!.unregisterListener(this, mAccelerometer)
        }

        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    App(sensorViewModel)
                }
            }
        }
    }

    private enum class AppStatus {
        LOADING,
        START,
        GO
    }

    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Composable
    private fun App(
        sensorViewModel: SensorViewModel,
    ) {
        val preferencesViewModel: PreferencesViewModel = viewModel(factory = PreferencesViewModel.Factory)
        val preferencesUiState by preferencesViewModel.uiState.collectAsState()
        val navController: NavHostController = rememberNavController()
        var appStatus by remember { mutableStateOf(AppStatus.LOADING) }

        LaunchedEffect(preferencesUiState) {
            appStatus = when {
                preferencesUiState.isStartScreen -> AppStatus.START
                else -> AppStatus.GO
            }
        }

        val startDestination = when (appStatus) {
            AppStatus.START -> AppStatus.START.name
            AppStatus.LOADING -> AppStatus.LOADING.name
            AppStatus.GO -> AppStatus.GO.name
        }

        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            composable(route = AppStatus.LOADING.name) {
                Splash()
            }
            composable(route = AppStatus.GO.name) {
                val athanViewModel: AthanViewModel = viewModel(factory = AthanViewModel.Factory)
                isNotificationPermissionGranted =
                    ContextCompat.checkSelfPermission(
                        this@MainActivity,
                        NOTIFICATION
                    ) == PackageManager.PERMISSION_GRANTED
                if (isNotificationPermissionGranted) {
                    val is12Hour = preferencesUiState.is12Hour
                    athanViewModel.onPermissionsAndDatabaseReady(is12Hour)
                    preferencesViewModel.onNotificationsClicked(false)
                }

                AthanApp(sensorViewModel, athanViewModel, preferencesViewModel)
            }
            composable(route = AppStatus.START.name) {
                StartUp(preferencesViewModel, navController)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Composable
    private fun StartUp(
        preferencesViewModel: PreferencesViewModel,
        navController: NavHostController,
    ) {
        when {
            openAlertDialog.value -> {
                AlertDialog(
                    onDismissRequest = { TODO() },
                    confirmButton = {
                        Button(onClick = { requestAlarmPermission() }) {
                            Text(text = "OK")
                        }
                    },
                    title = { Text("Notification Access Requirements") },
                    text = { Text("In order to receive notifications you must give BOTH alarm permissions and post notification requirements.") },
                    dismissButton = {
                        Button(onClick = { openAlertDialog.value = false }) {
                            Text(text = "Cancel")
                        }
                    }
                )
            }
        }

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
                    text = "Salam!",
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
                Column {
                    Button(
                        onClick = { requestLocationAccess() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Give Location Access",
                            style = Typography.displayMedium,
                        )
                    }

                    Button(
                        onClick = { openAlertDialog.value = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Give Notification Access",
                            style = Typography.displayMedium,
                        )
                    }
                }

                Spacer(modifier = Modifier.padding(30.dp))
                Image(
                    painter = painterResource(id = R.drawable.my_logo),
                    contentDescription = "",
                    modifier = Modifier
                        .size(136.dp)
                        .clip(CircleShape)
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

            if (isLocationPermissionGranted && isNetworkConnected) {
                val appContainerUiState by (application as AthanApplication).appContainerViewModel.uiState.collectAsState()
                isReady = appContainerUiState.isAppContainerReady
                val timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault())
                val currentTime = Date()
                val formattedTime = timeFormat.format(currentTime)
                val is12HourFormat = formattedTime.contains("AM", ignoreCase = true) || formattedTime.contains("PM", ignoreCase = true)
                LoadingScreen(isReady) {
                    preferencesViewModel.onPrayerTimesClicked(!is12HourFormat)
                    preferencesViewModel.requiresOnBoarding(false)
                    navController.navigate(AppStatus.GO.name)
                }

            } else if (!isNetworkConnected) {
                AlertDialog(
                    onDismissRequest = { TODO() },
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = ContextCompat.getSystemService(this, AlarmManager::class.java)
            if (alarmManager?.canScheduleExactAlarms() == false) {
                Intent().also { intent ->
                    intent.action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                    this.startActivity(intent)
                }
            }
        }

        val permissionRequest: MutableList<String> = ArrayList()
        if (!isNotificationPermissionGranted) {
            permissionRequest.add(NOTIFICATION)
        }

        permissionLauncher.launch(permissionRequest.toTypedArray())
        openAlertDialog.value = false
    }

    @Composable
    private fun LoadingScreen(isReady: Boolean, onContinueClick: () -> Unit) {
        if (isReady) {
            Button(
                onClick = {
                    onContinueClick()
                },
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Text("Continue")
            }
        } else {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(30.dp)
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isNetworkConnected(context: Context): Boolean {
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

        val permissionRequest: MutableList<String> = ArrayList()

        if (!isCoarseLocationPermissionGranted) {
            permissionRequest.add(COARSE_LOCATION)
        }
        if (!isFineLocationPermissionGranted) {
            permissionRequest.add(FINE_LOCATION)
        }
        permissionLauncher.launch(permissionRequest.toTypedArray())
    }

    override fun onResume() {
        super.onResume()
        mAccelerometer?.let {
            mSensorManager?.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_GAME,
                SensorManager.SENSOR_DELAY_UI
            )
        }

        mMagnetometer?.let {
            mSensorManager?.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_GAME,
                SensorManager.SENSOR_DELAY_UI
            )
        }
    }

    override fun onPause() {
        super.onPause()
        mSensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_GRAVITY -> gData = event.values.clone()
            Sensor.TYPE_ACCELEROMETER -> gData = event.values.clone()
            Sensor.TYPE_MAGNETIC_FIELD -> mData = event.values.clone()
            else -> return
        }

        if (SensorManager.getRotationMatrix(rMat, iMat, gData, mData)) {
            val mAzimuth = ((Math.toDegrees(
                SensorManager.getOrientation(
                    rMat,
                    orientation
                )[0].toDouble()
            ) + 360).toInt() % 360).toFloat()

            sensorViewModel.updateAzimuth(mAzimuth)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

}
