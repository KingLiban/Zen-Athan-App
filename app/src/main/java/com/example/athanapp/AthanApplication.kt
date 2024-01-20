    package com.example.athanapp

    import android.annotation.SuppressLint
    import android.app.AlarmManager
    import android.app.Application
    import android.app.NotificationChannel
    import android.app.NotificationManager
    import android.content.Context
    import android.content.pm.PackageManager
    import android.location.Geocoder
    import android.location.Location
    import android.os.Build
    import android.util.Log
    import androidx.annotation.RequiresApi
    import androidx.core.content.ContextCompat
    import androidx.datastore.core.DataStore
    import androidx.datastore.preferences.core.Preferences
    import androidx.datastore.preferences.preferencesDataStore
    import androidx.lifecycle.viewmodel.compose.viewModel
    import com.example.athanapp.AthanNotificationService.Companion.CHANNEL_DESCRIPTION
    import com.example.athanapp.AthanNotificationService.Companion.CHANNEL_NAME
    import com.example.athanapp.data.AppContainer
    import com.example.athanapp.data.DefaultAppContainer
    import com.example.athanapp.data.PrayersRepository
    import com.example.athanapp.data.UserPreferencesRepository
    import com.example.athanapp.ui.screens.AthanViewModel
    import com.example.athanapp.ui.screens.PreferencesUiState
    import com.example.athanapp.ui.screens.PreferencesViewModel
    import com.google.android.gms.location.CurrentLocationRequest
    import com.google.android.gms.location.FusedLocationProviderClient
    import com.google.android.gms.location.Granularity
    import com.google.android.gms.location.LocationServices
    import com.google.android.gms.location.Priority
    import com.google.android.gms.tasks.CancellationTokenSource
    import kotlinx.coroutines.CoroutineScope
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.SupervisorJob
    import kotlinx.coroutines.launch
    import java.io.IOException
    import java.time.LocalDate
    import java.time.LocalDateTime
    import java.time.LocalTime
    import java.time.format.DateTimeFormatter
    import java.util.Calendar
    import java.util.Locale

    private const val LAYOUT_PREFERENCE_NAME = "layout_preferences"
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = LAYOUT_PREFERENCE_NAME
    )

    class AthanApplication : Application() {
        var appContainerViewModel = AppContainerViewModel()
        private lateinit var userPreferencesRepository: UserPreferencesRepository
        lateinit var appContainer: AppContainer
        private var isLocationRetrieved = false
        private var isAppInitialized = false
        private val locationCoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
        private lateinit var preferencesViewModel: PreferencesViewModel
        lateinit var preferencesUiState: PreferencesUiState

        companion object {
            private const val COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION
            private const val FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION
        }

        override fun onCreate() {
            super.onCreate()
            appContainer = DefaultAppContainer(this, listOf(), 0.0, 0.0)
            userPreferencesRepository = UserPreferencesRepository(dataStore)
            if (ContextCompat.checkSelfPermission(
                    this,
                    FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    this,
                    COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                onLocationPermissionGranted()
            }

            createNotificationChannel()
        }

        private fun createNotificationChannel() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    AthanNotificationService.CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH // leave it high for now because prayers are important!
                ).apply {
                    description = CHANNEL_DESCRIPTION
                }
                val notificationService = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationService.createNotificationChannel(channel)
            }
        }

        fun onLocationPermissionGranted() {
            if (!isLocationRetrieved) {
                getLocation()
                isLocationRetrieved = true
            }
        }

        @SuppressLint("MissingPermission")
        private fun getLocation() {
            val fusedLocationClient: FusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(this)

            if (ContextCompat.checkSelfPermission(
                    this,
                    FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    this,
                    COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val priority = Priority.PRIORITY_HIGH_ACCURACY
                val granularity = Granularity.GRANULARITY_PERMISSION_LEVEL
                val cancellationToken = CancellationTokenSource().token

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val currentLocationRequest = CurrentLocationRequest.Builder()
                            .setGranularity(granularity)
                            .setPriority(priority)
                            .build()

                        fusedLocationClient.getCurrentLocation(currentLocationRequest, cancellationToken)
                            .addOnSuccessListener { location ->
                                if (location != null) {
                                    processLocation(location)
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.e("LocationError", "Failed to get location", exception)
                            }
                    } catch (e: Exception) {
                        Log.e("LocationError", "Exception in getting location", e)
                    }
                }
            }
        }

        private fun processLocation(location: Location) {
            if (!isAppInitialized) {
                val cityName = location.let {
                    val geocoder = Geocoder(this, Locale.getDefault())
                    try {
                        val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                        addresses?.firstOrNull()?.locality
                    } catch (e: IOException) {
                        e.printStackTrace()
                        null
                    }
                }

                locationCoroutineScope.launch {
                    initializeApp(location, cityName)
                }

                isAppInitialized = true
            }
        }

        fun setPreferencesUiState(preferencesUiState: PreferencesUiState) {
            this.preferencesUiState = preferencesUiState
        }

        private suspend fun initializeApp(location: Location?, city: String?) {
            if (location != null) {
                val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                println(currentYear)
                val (latitude, longitude) = Pair(location.latitude, location.longitude)
                val years = listOf(currentYear, currentYear + 1, currentYear + 2)
                preferencesViewModel = PreferencesViewModel(userPreferencesRepository)

                if (city != null) preferencesViewModel.setCityName(city)

                println("latitude and longitude: $latitude, $longitude")
                println("city name: $city")

                appContainer = DefaultAppContainer(
                    this,
                    years,
                    latitude,
                    longitude,
                )
                appContainerViewModel = AppContainerViewModel()

                updateDatabase(appContainer, preferencesViewModel)
                appContainerViewModel.updateAppContainerStatus(true)
            } else {
                println("Location is null")
            }
        }

        private suspend fun updateDatabase(
            appContainer: AppContainer,
            viewModel: PreferencesViewModel,) {
            val prayerDao = appContainer.prayersRepository

            val prayerEntities = appContainer.athanObjectRepository.getAthanObjects()
            println("is prayer entities not empty?")
            println(prayerEntities.isNotEmpty())

            if (prayerEntities.isNotEmpty()) {
                for (prayerEntity in prayerEntities) {
                    prayerDao.insertPrayerOnline(prayerData = prayerEntity)
                }
                setQiblaDirection(appContainer, viewModel)
            } else {
                for (prayerEntity in prayerEntities) {
                    prayerDao.insertPrayerOffline(prayerData = prayerEntity)
                }
            }
        }

        private suspend fun setQiblaDirection(
            appContainer: AppContainer,
            viewModel: PreferencesViewModel
        ) {
            val qiblaData = appContainer.athanObjectRepository.getQiblaData()
            if (qiblaData.latitude != 0.0 || qiblaData.longitude != 0.0) {
                val coordinates = Pair(qiblaData.latitude, qiblaData.longitude)
                val direction = qiblaData.direction
                viewModel.saveQiblaDirection(coordinates, direction)
            }
        }

    }


