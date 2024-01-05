package com.example.athanapp.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.athanapp.AthanApplication
import com.example.athanapp.data.PrayersRepository
import com.example.athanapp.network.PrayerEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Calendar

class AthanViewModel(private val prayersRepository: PrayersRepository) : ViewModel() {

    private var date = Calendar.DATE.toString()

    private val _uiState = MutableStateFlow(AthanUiState())

    val uiState: StateFlow<AthanUiState> get() = _uiState

    init {
        viewModelScope.launch {


            prayersRepository.getPrayer(date)
                .filterNotNull()
                .map { prayerEntity ->
                    _uiState.value = AthanUiState(prayerEntity)
                }
        }
    }

    data class AthanUiState(
        val prayerEntity: PrayerEntity = PrayerEntity(
            readable = "",
            fajr = "",
            sunrise = "",
            dhuhr = "",
            asr = "",
            maghrib = "",
            isha = ""
        )
    )

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as AthanApplication)
                AthanViewModel(application.appContainer.prayersRepository)
            }
        }
    }
}
