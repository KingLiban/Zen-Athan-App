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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PreferencesViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val uiState: StateFlow<PreferencesUiState> =
        userPreferencesRepository.isStartScreen
            .combine(userPreferencesRepository.selectedCity) { isStartScreen, cityName ->
                PreferencesUiState(isStartScreen, cityName)
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
    val cityName: String? = ""
)