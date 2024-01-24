package com.example.athanapp.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.athanapp.AthanApplication
import com.example.athanapp.data.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PreferencesViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val uiState: StateFlow<PreferencesUiState> =
        combine(
            userPreferencesRepository.isStartScreen,
            userPreferencesRepository.selectedCity,
            userPreferencesRepository.isDarkMode,
            userPreferencesRepository.is12Hour,
            userPreferencesRepository.isNotification,
            userPreferencesRepository.latitude,
            userPreferencesRepository.longitude,
            userPreferencesRepository.direction,
        ) { values ->
            PreferencesUiState(
                isStartScreen = values[0] as Boolean,
                cityName = values[1] as String,
                isDarkMode = values[2] as Boolean,
                is12Hour = values[3] as Boolean,
                isNotification = values[4] as Boolean,
                latitude = values[5] as Double,
                longitude = values[6] as Double,
                direction = values[7] as Double,
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = PreferencesUiState()
            )

    fun requiresOnBoarding(isStartScreen: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.saveLayoutPreference(isStartScreen)
        }
    }

    fun setCityName(cityName: String?) {
        viewModelScope.launch {
            userPreferencesRepository.saveSelectedCity(cityName)
        }
    }

    fun onGeneralSettingsClicked(checked: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.saveDarkModePreferences(!checked)
        }
    }

    fun onPrayerTimesClicked(checked: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.saveTimePreferences(!checked)
        }
    }

    fun onNotificationsClicked(checked: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.saveNotificationPreferences(!checked)
        }
    }

    fun saveQiblaDirection(coordinates: Pair<Double, Double>, direction: Double) {
        viewModelScope.launch {
            userPreferencesRepository.saveCoordinates(coordinates, direction)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as AthanApplication)
                PreferencesViewModel(application.userPreferencesRepository)
            }
        }
    }
}

data class PreferencesUiState(
    val isStartScreen: Boolean = true,
    val cityName: String? = "",
    val isDarkMode: Boolean = true,
    val is12Hour: Boolean = false,
    val isNotification: Boolean = false,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val direction: Double = 0.0
)