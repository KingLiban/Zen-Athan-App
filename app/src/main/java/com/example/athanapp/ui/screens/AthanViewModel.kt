package com.example.athanapp.ui.screens

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.athanapp.AthanApplication
import com.example.athanapp.AthanNotificationReceiver
import com.example.athanapp.data.PrayersRepository
import com.example.athanapp.network.PrayerEntity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
class AthanViewModel(
    private val prayersRepository: PrayersRepository,
    private val alarmManager: AlarmManager,
    private val context: Context,
    private val preferencesUiState: PreferencesUiState
) : ViewModel() {

    private val currentDate = LocalDate.now()

    private val currentDatePlus30 = currentDate.plusDays(30)

    private val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
    private val date = currentDate.format(formatter)
    private val endDate = currentDatePlus30.format(formatter)

    private val _uiState = MutableStateFlow(AthanUiState())

    val uiState: StateFlow<AthanUiState> get() = _uiState
    private var timerJob: Job? = null

    private var isNotificationsScheduled = false
    var prayerEntityList = listOf<PrayerEntity>()
    private var is12Hour = { mutableStateOf(preferencesUiState.is12Hour) }

    init {
        println("Initializing AthanViewModel")
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

        viewModelScope.launch {
            if (!isNotificationsScheduled) {
                prayersRepository.get30DaysPrayer(date, endDate)
                    .filterNotNull()
                    .collect { list ->
                        prayerEntityList = list
                    }
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

        var currentPrayer = "Isha"
        for ((prayer, time) in prayerTimes) {
            if (time > currentTime) {
                break
            }
            currentPrayer = prayer
        }

        return currentPrayer
    }

    @SuppressLint("ScheduleExactAlarm")
    fun schedulePrayerNotifications(prayerEntityList: List<PrayerEntity>) {
        val is12HourFormat = is12Hour().value

//        val mockPrayerTime = "19:02"
        val mockPrayerTime = "7:04 PM"

        val formatter: DateTimeFormatter = if (is12HourFormat) {
            DateTimeFormatter.ofPattern("hh:mm a")
        } else {
            DateTimeFormatter.ofPattern("HH:mm")
        }

        val mockPrayerDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.parse(mockPrayerTime, formatter))
//        val mockPrayerDateTime2 = LocalDateTime.of(LocalDate.now(), LocalTime.parse(mockPrayerTime2, formatter))

        val intent = Intent(context, AthanNotificationReceiver::class.java)
        intent.action = "SHOW_NOTIFICATION"
        intent.putExtra("prayerName", "Mock Prayer")
        intent.putExtra("prayerTime", mockPrayerTime)

//        val intent2 = Intent(context, AthanNotificationReceiver::class.java)
//        intent2.action = "SHOW_NOTIFICATION"
//        intent2.putExtra("prayerName", "Mock Prayer")
//        intent2.putExtra("prayerTime", mockPrayerTime2)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

//        val pendingIntent2 = PendingIntent.getBroadcast(
//            context,
//            1,
//            intent2,
//            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
//        )

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            mockPrayerDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            pendingIntent
        )

//        alarmManager.setExact(
//            AlarmManager.RTC_WAKEUP,
//            mockPrayerDateTime2.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
//            pendingIntent2
//        )


       /*
        for (prayerEntity in prayerEntityList) {
            val prayerTimes = listOf(
                "Fajr" to LocalDateTime.of(LocalDate.now(), LocalTime.parse(prayerEntity.fajr.split(" ")[0])),
                "Sunrise" to LocalDateTime.of(LocalDate.now(), LocalTime.parse(prayerEntity.sunrise.split(" ")[0])),
                "Dhuhr" to LocalDateTime.of(LocalDate.now(), LocalTime.parse(prayerEntity.dhuhr.split(" ")[0])),
                "Asr" to LocalDateTime.of(LocalDate.now(), LocalTime.parse(prayerEntity.asr.split(" ")[0])),
                "Maghrib" to LocalDateTime.of(LocalDate.now(), LocalTime.parse(prayerEntity.maghrib.split(" ")[0])),
                "Isha" to LocalDateTime.of(LocalDate.now(), LocalTime.parse(prayerEntity.isha.split(" ")[0]))
            ).sortedBy { it.second }

            // change to 12 hour or 24 hour later

            for ((prayerName, prayerTime) in prayerTimes) {

            }
        }
        */
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
            val nextDayFajr = LocalDateTime.of(LocalDate.now().plusDays(1),
                LocalTime.parse(prayerEntity.fajr.split(" ")[0]))
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
                val prayersRepository = application.appContainer.prayersRepository
                val alarmManager = application.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val preferencesUiState = application.preferencesUiState
                val context = application.applicationContext
                AthanViewModel(prayersRepository, alarmManager, context, preferencesUiState)
            }
        }
    }
}
