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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PreferencesViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val uiState: StateFlow<PreferencesUiState> =
        userPreferencesRepository.isStartScreen
            .map { isStartScreen ->
            PreferencesUiState(isStartScreen)
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
)