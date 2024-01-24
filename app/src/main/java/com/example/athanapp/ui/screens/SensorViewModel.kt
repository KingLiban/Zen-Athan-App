package com.example.athanapp.ui.screens

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SensorViewModel : ViewModel() {
    data class SensorUiState(
        val azimuth: Float = 0f
    )

    private val _uiState = MutableStateFlow(SensorUiState())

    val uiState: StateFlow<SensorUiState> get() = _uiState

    fun updateAzimuth(azimuth: Float) {
        val newUiState = _uiState.value.copy(azimuth = azimuth)
        _uiState.value = newUiState
    }
}
