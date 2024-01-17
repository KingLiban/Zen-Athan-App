package com.example.athanapp

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AppContainerViewModel(): ViewModel() {

    private val _uiState = MutableStateFlow(AppContainerUiState())
    val uiState: StateFlow<AppContainerUiState> get() = _uiState

    fun updateAppContainerStatus(status: Boolean) {
        _uiState.value = AppContainerUiState(isAppContainerReady = status)
    }

    data class AppContainerUiState(
        val isAppContainerReady: Boolean = false
    )
}