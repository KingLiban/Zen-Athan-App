package com.example.athanapp.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.athanapp.AthanApplication
import com.example.athanapp.data.PrayersRepository
import com.example.athanapp.network.PrayerEntity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Calendar
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
class AthanViewModel(private val prayersRepository: PrayersRepository) : ViewModel() {

    private var date = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(Calendar.getInstance().time)

    private val _uiState = MutableStateFlow(AthanUiState())

    val uiState: StateFlow<AthanUiState> get() = _uiState
    private var timerJob: Job? = null


    init {
        viewModelScope.launch {
            prayersRepository.getPrayer(date)
                .filterNotNull()
                .collect { prayerEntity ->
                    val currentPrayer = getCurrentPrayer(prayerEntity)
                    val newTimeLeft = getTimeUntilNextPrayer(prayerEntity)
                    val newState = _uiState.value.copy(
                        prayerEntity = prayerEntity,
                        timeLeft = newTimeLeft,
                        currentPrayer = currentPrayer
                    )
                    _uiState.value = newState
                    startTimer(prayerEntity)
                }
        }
    }

    private fun getCurrentPrayer(prayerEntity: PrayerEntity): String {
        val currentTime = SimpleDateFormat("HH:mm", Locale.ENGLISH).format(Date())
        val prayerTimes = listOf(
            "Fajr" to prayerEntity.fajr,
            "Sunrise" to prayerEntity.sunrise,
            "Dhuhr" to prayerEntity.dhuhr,
            "Asr" to prayerEntity.asr,
            "Maghrib" to prayerEntity.maghrib,
            "Isha" to prayerEntity.isha
        )

        var currentPrayer = "Fajr"
        for ((prayer, time) in prayerTimes) {
            if (time > currentTime) {
                break
            }
            currentPrayer = prayer
        }

        return currentPrayer
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getTimeUntilNextPrayer(prayerEntity: PrayerEntity): String {
        val currentTime = LocalDateTime.now()
        val prayerTimes = listOf(
            "Fajr" to LocalDateTime.of(LocalDate.now(), LocalTime.parse(prayerEntity.fajr.split(" ")[0])),
            "Sunrise" to LocalDateTime.of(LocalDate.now(), LocalTime.parse(prayerEntity.sunrise.split(" ")[0])),
            "Dhuhr" to LocalDateTime.of(LocalDate.now(), LocalTime.parse(prayerEntity.dhuhr.split(" ")[0])),
            "Asr" to LocalDateTime.of(LocalDate.now(), LocalTime.parse(prayerEntity.asr.split(" ")[0])),
            "Maghrib" to LocalDateTime.of(LocalDate.now(), LocalTime.parse(prayerEntity.maghrib.split(" ")[0])),
            "Isha" to LocalDateTime.of(LocalDate.now(), LocalTime.parse(prayerEntity.isha.split(" ")[0]))
        ).sortedBy { it.second }

        var nextPrayerTime: LocalDateTime? = null
        for ((_, time) in prayerTimes) {
            if (time > currentTime) {
                nextPrayerTime = time
                break
            }
        }

        val timeLeft = if (nextPrayerTime != null) {
            Duration.between(currentTime, nextPrayerTime)
        } else {
            val nextDayFajr = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.parse(prayerEntity.fajr.split(" ")[0]))
            Duration.between(currentTime, nextDayFajr)
        }

        val hours = timeLeft.toHoursPart()
        val minutes = timeLeft.toMinutesPart()
        val seconds = timeLeft.toSecondsPart()

        return String.format("%d:%02d:%02d", hours, minutes, seconds)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startTimer(prayerEntity: PrayerEntity) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                val newTimeLeft = getTimeUntilNextPrayer(prayerEntity)
                val newState = _uiState.value.copy(timeLeft = newTimeLeft)
                _uiState.value = newState
                delay(1000)
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
        ),
        val timeLeft: String = "",
        val city: String = "",
        val currentPrayer: String = ""
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
